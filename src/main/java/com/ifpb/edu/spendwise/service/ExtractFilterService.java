package com.ifpb.edu.spendwise.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ifpb.edu.spendwise.model.StatementItem;
import com.ifpb.edu.spendwise.model.Transaction;
import com.ifpb.edu.spendwise.model.dto.StatementFilterDto;
import com.ifpb.edu.spendwise.model.enumerator.CategoryTypes;
import com.ifpb.edu.spendwise.repository.TransactionRepository;


@Service
public class ExtractFilterService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CommentaryService commentService;

    public List<StatementItem> generateExtract(StatementFilterDto filter) {

        List<Transaction> transactions = fetchFilteredTransactions(filter);
        
    
        return buildStatementItems(transactions, filter.getAccountId());
    }

    private List<Transaction> fetchFilteredTransactions(StatementFilterDto filter) {
        Specification<Transaction> spec = Specification.where(null);
        
        if (filter.getAccountId() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("account").get("id"), filter.getAccountId()));
        }
        
        if (filter.getStartDate() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.greaterThanOrEqualTo(root.get("transactionDate"), filter.getStartDate().atStartOfDay()));
        }
        
        if (filter.getEndDate() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.lessThanOrEqualTo(root.get("transactionDate"), filter.getEndDate().atTime(23, 59, 59)));
        }
        
        if (filter.getCategoryIds() != null && !filter.getCategoryIds().isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                root.get("category").get("id").in(filter.getCategoryIds()));
        }
        
        if (filter.getTransactionType() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("category").get("categoryType"), 
                        filter.getTransactionType() == CategoryTypes.TRANSFER ? 
                        CategoryTypes.INCOME : CategoryTypes.EXPENSE));
        }
        
        if (filter.getMinValue() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.greaterThanOrEqualTo(root.get("value"), filter.getMinValue()));
        }
        
        if (filter.getMaxValue() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.lessThanOrEqualTo(root.get("value"), filter.getMaxValue()));
        }
        
        if (filter.getSearchTerm() != null && !filter.getSearchTerm().isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.like(cb.lower(root.get("description")), 
                       "%" + filter.getSearchTerm().toLowerCase() + "%"));
        }

        return transactionRepository.findAll(spec, Sort.by("transactionDate").ascending());
    }

    private List<StatementItem> buildStatementItems(List<Transaction> transactions, Long accountId) {
        List<StatementItem> statementItems = new ArrayList<>();
        BigDecimal runningBalance = accountService.getAccountBalanceBeforeDate(accountId, 
            transactions.isEmpty() ? LocalDate.now() : transactions.get(0).getTransactionDate().toLocalDate());
        
        for (Transaction transaction : transactions) {
            BigDecimal previousBalance = runningBalance;
            
       
            if (transaction.getCategory().getCategoryType() == CategoryTypes.INCOME) {
                runningBalance = runningBalance.add(transaction.getValue());
            } else {
                runningBalance = runningBalance.subtract(transaction.getValue());
            }
            
            boolean hasComments = commentService.hasComments(transaction.getId());
            int commentCount = commentService.countComments(transaction.getId());
            
            statementItems.add(new StatementItem(
                transaction,
                previousBalance,
                runningBalance,
                hasComments,
                commentCount
            ));
        }
        
        return statementItems;
    }


    public Map<String, BigDecimal> getCategorySummary(StatementFilterDto filter) {
        return fetchFilteredTransactions(filter).stream()
            .collect(Collectors.groupingBy(
                t -> t.getCategory().getName(),
                Collectors.reducing(
                    BigDecimal.ZERO,
                    Transaction::getValue,
                    BigDecimal::add
                )
            ));
    }
}
