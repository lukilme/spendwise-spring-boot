package com.ifpb.edu.spendwise.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class StatementItem{
    private Transaction transaction;
    private BigDecimal previousBalance;
    private BigDecimal currentBalance;
    private boolean hasComments;
    private int commentCount;

    public StatementItem(Transaction transaction, BigDecimal previousBalance, 
                           BigDecimal currentBalance, boolean hasComments, int commentCount) {
        this.transaction = transaction;
        this.previousBalance = previousBalance;
        this.currentBalance = currentBalance;
        this.hasComments = hasComments;
        this.commentCount = commentCount;
    }

    public StatementItem(){

    }
}
