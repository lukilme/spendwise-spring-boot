package com.ifpb.edu.spendwise.exception.customer;

public class CustomerSaveException extends RuntimeException {
    public CustomerSaveException(String messsage, Throwable cause){
        super(messsage, cause);
    }
}
