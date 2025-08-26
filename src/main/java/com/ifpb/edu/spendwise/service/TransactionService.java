package com.ifpb.edu.spendwise.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable; 
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.ifpb.edu.spendwise.model.StatementItem;
import com.ifpb.edu.spendwise.model.Transaction;
import com.ifpb.edu.spendwise.model.dto.StatementFilterDto;
import com.ifpb.edu.spendwise.model.dto.TransactionDTO;
import com.ifpb.edu.spendwise.repository.TransactionRepository;
import com.ifpb.edu.spendwise.repository.interfaces.TransactionCommentCount;

import jakarta.transaction.Transactional;


@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ExtractFilterService extractFilterService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    AccountService accountService;

    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction editTransaction(Long id, Transaction transaction) {
        transaction.setId(id);
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    public Page<Transaction> findAll(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }
    
    public Page<TransactionCommentCount> findAllWithCommentCount(Long accountId,Pageable pageable) {
        return transactionRepository.findAllWithCommentCount(accountId, pageable);
    }

    public Page<Transaction> findAllAccountById(Long accountId, Pageable pageable) {
        return transactionRepository.findByAccountId(accountId, pageable);
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + id));
    }

    public List<StatementItem> getExtract(StatementFilterDto filter) {
        return extractFilterService.generateExtract(filter);
    }

    // public List<ExtractItem> getCurrentMonthExtract(Long accountId) {
    // LocalDate start = LocalDate.now().withDayOfMonth(1);
    // LocalDate end = start.plusMonths(1).minusDays(1);
    // return extractFilterService.generateExtract(new ExtractFilter(accountId,
    // start, end));
    // }

    public BigDecimal calculateBalanceInPeriod(Long accountId, LocalDate start, LocalDate end) {
        return transactionRepository.findByPeriod(accountId, start, end).stream()
                .map(Transaction::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Transaction> findAccountById(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    public void updateTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public Page<Transaction> TransactionCommentCount(Long accountId, Pageable pageable) {
        throw new UnsupportedOperationException("Unimplemented method 'TransactionCommentCount'");
    }
    public TransactionDTO toDTO(Transaction t) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(t.getId());
        dto.setValue(t.getValue());
        dto.setTransactionDate(t.getTransactionDate());
        dto.setAccountId(t.getAccount().getId());
        dto.setCategoryId(t.getCategory() != null ? t.getCategory().getId() : null);
        return dto;
    }

    public Transaction toEntity(TransactionDTO dto) {
        Transaction t = new Transaction();
        t.setId(dto.getId());
        t.setValue(dto.getValue());
        t.setTransactionDate(dto.getTransactionDate());
        t.setAccount(accountService.findById(dto.getAccountId()).orElseThrow());
        t.setCategory(categoryService.findById(dto.getCategoryId()).orElseThrow());
        return t;
    }

    // public List<Transaction> findTransactionsByCategory(Long categoryId, int
    // year, int month) {
    // LocalDate start = LocalDate.of(year, month, 1);
    // LocalDate end = start.plusMonths(1).minusDays(1);
    // return transactionRepository.findByCategoryAndPeriod(categoryId, start, end);
    // }
}
