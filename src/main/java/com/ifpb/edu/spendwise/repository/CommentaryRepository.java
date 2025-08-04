package com.ifpb.edu.spendwise.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ifpb.edu.spendwise.model.Commentary;
import com.ifpb.edu.spendwise.model.Transaction;

@Repository
public interface CommentaryRepository extends JpaRepository<Commentary, Long> {

    List<Commentary> findByTransaction(Transaction transaction);

    List<Commentary> findByTransactionId(Long transactionId);

    boolean existsByTransactionId(Long transactionId);

    int countByTransactionId(Long transactionId);

    Page<Commentary> findByTransactionId(Long transactionId, Pageable pageable);

}
