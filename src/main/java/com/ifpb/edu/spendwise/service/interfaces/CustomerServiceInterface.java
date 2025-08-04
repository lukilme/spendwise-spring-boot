package com.ifpb.edu.spendwise.service.interfaces;

import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.ifpb.edu.spendwise.exception.customer.CustomerCreationException;
import com.ifpb.edu.spendwise.exception.customer.EmailAlreadyExistsException;
import com.ifpb.edu.spendwise.exception.customer.InvalidCustomerDataException;
import com.ifpb.edu.spendwise.model.Customer;
import com.ifpb.edu.spendwise.model.dto.CreateCustomerRequest;

public interface CustomerServiceInterface {

    /**
     * Creates a new customer in the system
     * 
     * @param customerRequest the customer data to be created
     * @return the created customer with generated ID
     * @throws CustomerCreationException    if customer creation fails
     * @throws EmailAlreadyExistsException  if email is already in use
     * @throws InvalidCustomerDataException if customer data is invalid
     */
    Customer createCustomer(CreateCustomerRequest newCustomer);

    /**
     * Counts the number of accounts associated with a given customer ID.
     * This operation is read-only and transactional.
     * 
     * @param customerId the ID of the customer to count accounts for
     * @return the number of accounts associated with the customer
     * @throws DataAccessException if there's an issue accessing the database
     */
    @Transactional(readOnly = true)
    int countAccounts(Long customerId);

    /**
     * Counts the number of transactions associated with a given customer ID.
     * This operation is read-only and transactional.
     * 
     * @param customerId the ID of the customer to count transactions for
     * @return the number of transactions associated with the customer
     * @throws DataAccessException if there's an issue accessing the database
     */
    @Transactional(readOnly = true)
    int countTransactions(Long customerId);
}
