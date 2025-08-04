package com.ifpb.edu.spendwise.service;

import com.ifpb.edu.spendwise.model.Account;
import com.ifpb.edu.spendwise.model.Customer;
import com.ifpb.edu.spendwise.model.Transaction;
import com.ifpb.edu.spendwise.model.enumerator.AccountTypes;
import com.ifpb.edu.spendwise.model.enumerator.CategoryTypes;
import com.ifpb.edu.spendwise.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ifpb.edu.spendwise.repository.AccountRepository;
import com.ifpb.edu.spendwise.repository.CustomerRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Page<Account> findByCustomerId(Long customerId, PageRequest pageable) {
        return accountRepository.findByCustomerId(customerId, pageable);
    }

    public Page<Account> findByCustomerIdAndAccountTypeIn(Long customerId, List<AccountTypes> types,
            PageRequest pageable) {
        return accountRepository.findByCustomerIdAndAccountTypeIn(customerId, types, pageable);
    }

    public List<Account> listUserAccounts(Long customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    public List<Account> listCreditAccounts(Long customerId) {
        return accountRepository.findByCustomerId(customerId).stream()
                .filter(c -> c.getAccountType() == AccountTypes.CREDIT)
                .map(c -> (Account) c)
                .collect(Collectors.toList());
    }

    public List<Account> listDebitAccounts(Long customerId) {
        return accountRepository.findByCustomerId(customerId).stream()
                .filter(c -> c.getAccountType() == AccountTypes.CREDIT)
                .map(c -> (Account) c)
                .collect(Collectors.toList());
    }

    public Account searchById(Long id) {
        return accountRepository.findById(id).orElseThrow();
    }

    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }

    public void disableAccount(Long id) {
        Account account = searchById(id);
        account.setActive(false);
        accountRepository.save(account);
    }

    public BigDecimal calculateCurrentBalance(Long accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        return transactions.stream().map(Transaction::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Account createAccountForUser(Account account, Long userId) {
        Customer customer = customerRepository.findById(userId).orElseThrow();
        account.setCustomer(customer);
        return accountRepository.save(account);
    }

    public void deleteCompleteAccount(Long accountId) {
        transactionRepository.deleteByCustomerId(accountId);
        accountRepository.deleteById(accountId);
    }

    public List<Account> listAllAccounts() {
        return accountRepository.findAll();
    }

    public List<Account> searchAccountsByCriteria(String criteria) {
        return accountRepository.findAll().stream()
                .filter(c -> c.getName().toLowerCase().contains(criteria.toLowerCase()))
                .collect(Collectors.toList());
    }

    public int countAccountTransactions(Long accountId) {
        return transactionRepository.countByAccountId(accountId);
    }

    public BigDecimal getAccountBalanceBeforeDate(Long accountId, LocalDate date) {
        List<Transaction> transactions = transactionRepository
                .findByAccountIdAndTransactionDateBefore(accountId, date.atStartOfDay());

        return transactions.stream()
                .map(tx -> {
                    BigDecimal value = tx.getValue();
                    return tx.getCategory().getCategoryType() == CategoryTypes.INCOME
                            ? value
                            : value.negate();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Optional<Account> findById(Long accountId) {
        return accountRepository.findById(accountId);
    }

    public long countAccount() {
        return accountRepository.count();
    }

}