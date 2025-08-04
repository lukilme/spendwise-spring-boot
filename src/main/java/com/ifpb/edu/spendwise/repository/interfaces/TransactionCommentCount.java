package com.ifpb.edu.spendwise.repository.interfaces;

import com.ifpb.edu.spendwise.model.Transaction;

public interface TransactionCommentCount {
    Transaction getTransaction();
    Long getCommentCount();
}
