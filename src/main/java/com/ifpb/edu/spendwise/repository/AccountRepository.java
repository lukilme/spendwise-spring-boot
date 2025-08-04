package com.ifpb.edu.spendwise.repository;

import com.ifpb.edu.spendwise.model.enumerator.AccountTypes;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ifpb.edu.spendwise.model.Account;
import com.ifpb.edu.spendwise.model.Customer;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByCustomerId(Long customerId);

    List<Account> findByCustomer(Customer customer);

    @SuppressWarnings("null")
    Optional<Account> findById(Long accountId);

    List<Account> findByCustomerIdAndAccountType(Long customerId, AccountTypes type);

    boolean existsById(Long id);

    @Query("SELECT COUNT(c) FROM Account c WHERE c.customer.id = :customerId")
    int countByCustomerId(@Param("customerId") Long customerId);

    void deleteByCustomerId(Long customerId);

    Page<Account> findByCustomerId(Long customerId, PageRequest pageable);

    Page<Account> findByCustomerIdAndAccountTypeIn(Long customerId, List<AccountTypes> types, PageRequest pageable);


}
