package com.ifpb.edu.spendwise.service;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ifpb.edu.spendwise.exception.customer.CustomerCreationException;
import com.ifpb.edu.spendwise.exception.customer.EmailAlreadyExistsException;
import com.ifpb.edu.spendwise.exception.customer.InvalidCustomerDataException;
import com.ifpb.edu.spendwise.model.Account;
import com.ifpb.edu.spendwise.model.Customer;
import com.ifpb.edu.spendwise.model.Transaction;
import com.ifpb.edu.spendwise.model.dto.CreateCustomerRequest;
import com.ifpb.edu.spendwise.repository.AccountRepository;
import com.ifpb.edu.spendwise.repository.CustomerRepository;
import com.ifpb.edu.spendwise.repository.TransactionRepository;
import com.ifpb.edu.spendwise.util.LoggerHandle;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public Customer createCustomer(CreateCustomerRequest newCustomer) {

        LoggerHandle.info("Starting customer creation process for email: {}".formatted(newCustomer.getEmail()));

        try {

            validateCustomerRequest(newCustomer);

            validateEmailUniqueness(newCustomer.getEmail());

            Customer customer = buildCustomerFromRequest(newCustomer);

            encryptPassword(customer);

            Customer savedCustomer = customerRepository.save(customer);

            return savedCustomer;

        } catch (EmailAlreadyExistsException | InvalidCustomerDataException ex) {

            LoggerHandle.warning("Fail to create customer with email: " + newCustomer.getEmail());
            LoggerHandle.warning("Customer exception failed: " + ex.getMessage());
            LoggerHandle.erro(ex);

            throw ex;
        } catch (Exception ex) {

            LoggerHandle.warning("Unexpected error during customer creation for email: " + newCustomer.getEmail());
            LoggerHandle.info("Customer exception failed: " + ex.getMessage());
            LoggerHandle.erro(ex);

            throw new CustomerCreationException("Failed to create customer", ex);
        }
    }

    @Transactional(readOnly = true)
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));
    }

    @Transactional(readOnly = true)
    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email " + email));
    }

    public Customer updateCustomer(Long id, Customer customer) {
        Customer existing = findById(id);
        existing.setName(customer.getName());
        existing.setEmail(customer.getEmail());
        existing.setActive(customer.getActive());
        existing.setRole(customer.getRole());
        existing.setStatus(customer.getStatus());
        return customerRepository.save(existing);
    }

    public void deactivateCustomer(Long id) {
        Customer existing = findById(id);
        existing.setActive(false);
        customerRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return customerRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean canAccessSystem(Long customerId) {
        Customer customer = findById(customerId);
        return customer.getActive() && customer.getStatus().name().equals("ACTIVE");
    }

    public void updateLastAccess(Long customerId) {
        Customer customer = findById(customerId);
        customer.setLastAccess(LocalDateTime.now());
        customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Customer> findAllAccountHolders() {
        return customerRepository.findByAccountsIsNotEmpty();
    }

    // @Transactional(readOnly = true)
    // public List<Customer> findCustomersByCriteria(String criteria) {
    // return customerRepository.findByEmail(criteria);
    // }

    public void deleteCustomerAndData(Long customerId) {
        // delete transactions and accounts first
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        for (Account acc : accounts) {
            List<Transaction> txs = transactionRepository.findByAccountId(acc.getId());
            transactionRepository.deleteAll(txs);
        }
        accountRepository.deleteAll(accounts);
        customerRepository.deleteById(customerId);
    }

    @Transactional(readOnly = true)
    public int countAccounts(Long customerId) {
        return accountRepository.countByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public int countTransactions(Long customerId) {
        return transactionRepository.countByCustomerId(customerId);
    }

    private void validateEmailUniqueness(String email) {
        if (customerRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyExistsException("Email already in use: " + email);
        }
    }

    private void validateCustomerRequest(CreateCustomerRequest request) {
        if (request == null) {
            throw new InvalidCustomerDataException("Customer request cannot be null");
        }
    }

    private Customer buildCustomerFromRequest(CreateCustomerRequest request) {
        return Customer.builder()
                .name(request.getName().trim())
                .email(request.getEmail().toLowerCase().trim())
                .password(request.getPassword())
                .build();
    }

    private void encryptPassword(Customer customer) {
        try {
            String hashedPassword = AuthService.hashPassword(customer.getPassword());
            customer.setPassword(hashedPassword);
        } catch (Exception e) {
            throw new CustomerCreationException("Failed to encrypt password", e);
        }
    }

    // @Transactional(readOnly = true)
    // public BigDecimal calculateWealth(Long customerId) {
    // return
    // transactionRepository.sumBalanceByCustomerId(customerId).orElse(BigDecimal.ZERO);
    // }

}