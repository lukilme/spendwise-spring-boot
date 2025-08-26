package com.ifpb.edu.spendwise.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ifpb.edu.spendwise.model.Transaction;

import lombok.Data;
@Data
public class TransactionWithCountDTO {
    private Long id;
    private BigDecimal value;
    private LocalDateTime transactionDate;
    private String categoryName;
    private long commentCount;

    public TransactionWithCountDTO(Transaction t, long commentCount) {
        this.id = t.getId();
        this.value = t.getValue();
        this.transactionDate = t.getTransactionDate();
        this.categoryName = t.getCategory() != null ? t.getCategory().getName() : null;
        this.commentCount = commentCount;
    }
}

