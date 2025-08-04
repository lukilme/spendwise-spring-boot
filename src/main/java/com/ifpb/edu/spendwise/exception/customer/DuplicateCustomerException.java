package com.ifpb.edu.spendwise.exception.customer;

public class DuplicateCustomerException extends RuntimeException{
    public DuplicateCustomerException (String messsage, Throwable cause){
        super(messsage, cause);
    }
}
