package com.ifpb.edu.spendwise.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import com.ifpb.edu.spendwise.model.Transaction;
import com.ifpb.edu.spendwise.repository.interfaces.TransactionCommentCount;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

        List<Transaction> findByAccountId(Long accountId);

        @Query("SELECT t FROM Transaction t WHERE t.account.customer.id = :customerId")
        List<Transaction> findByCustomerId(@Param("customerId") Long customerId);

        @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND t.transactionDate BETWEEN :start AND :end")
        List<Transaction> findByPeriod(@Param("accountId") Long accountId,
                        @Param("start") LocalDate start,
                        @Param("end") LocalDate end);

        @Query("SELECT t FROM Transaction t WHERE t.category.id = :categoryId AND t.transactionDate BETWEEN :start AND :end")
        List<Transaction> findByCategoryAndPeriod(@Param("categoryId") Long categoryId,
                        @Param("start") LocalDate start,
                        @Param("end") LocalDate end);

        @Query("SELECT SUM(t.value) FROM Transaction t WHERE t.category.id = :categoryId AND t.transactionDate BETWEEN :start AND :end")
        BigDecimal sumForCategoryByTime(@Param("categoryId") Long categoryId,
                        @Param("start") LocalDate start,
                        @Param("end") LocalDate end);

        int countByAccountId(Long accountId);

        @Query("SELECT COUNT(t) FROM Transaction t WHERE t.account.customer.id = :customerId")
        int countByCustomerId(@Param("customerId") Long customerId);

        void deleteByAccountId(Long accountId);

        @Modifying
        @Query("DELETE FROM Transaction t WHERE t.account.customer.id = :customerId")
        int deleteByCustomerId(@Param("customerId") Long customerId);

        List<Transaction> findByAccountIdAndTransactionDateBefore(Long accountId, LocalDateTime date);

        List<Transaction> findAll(Specification<Transaction> spec, Sort ascending);

        @SuppressWarnings("null")
        Page<Transaction> findAll(Pageable pageable);

        Page<Transaction> findByAccountId(Long accountId, Pageable pageable);

        @Query("SELECT t AS transaction, COUNT(c) AS commentCount " +
                        "FROM Transaction t LEFT JOIN t.commentaries c " +
                        "WHERE t.account.id = :accountId " +
                        "GROUP BY t")
        Page<TransactionCommentCount> findAllWithCommentCount(@Param("accountId") Long accountId, Pageable pageable);

}
