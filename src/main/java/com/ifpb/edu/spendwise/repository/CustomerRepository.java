package com.ifpb.edu.spendwise.repository;

import com.ifpb.edu.spendwise.model.Customer;
import com.ifpb.edu.spendwise.model.enumerator.StatusCustomer;
import com.ifpb.edu.spendwise.model.enumerator.UserRoles;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    List<Customer> findByRole(UserRoles role);

    List<Customer> findByStatus(StatusCustomer status);

    @Modifying
    @Query("UPDATE Customer c SET c.status = :status WHERE c.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") StatusCustomer status);

    @Modifying
    @Query("UPDATE Customer c SET c.lastAccess = :accessDate WHERE c.id = :id")
    void updateLastAccess(@Param("id") Long id, @Param("accessDate") LocalDateTime accessDate);

    boolean existsByEmail(String email);

    boolean existsById(Long id); 

    int countByRole(UserRoles role);

    int countByStatus(StatusCustomer status);

    List<Customer> findByAccountsIsNotEmpty();

    Page<Customer> findByRole(UserRoles role_requested, PageRequest pageable);
}

