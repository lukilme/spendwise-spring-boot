package com.ifpb.edu.spendwise.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ifpb.edu.spendwise.model.Transaction;

import lombok.Data;
@Data
public class TransactionListDTO {
    private Long id;
    private BigDecimal value;
    private LocalDateTime transactionDate;
    private String categoryName;
    private long commentCount;

    public TransactionListDTO(Transaction transaction, long commentCount) {
        this.id = transaction.getId();
        this.value = transaction.getValue();
        this.transactionDate = transaction.getTransactionDate();
        this.categoryName = transaction.getCategory() != null ? transaction.getCategory().getName() : null;
        this.commentCount = commentCount;
    }

    // getters e setters
}

