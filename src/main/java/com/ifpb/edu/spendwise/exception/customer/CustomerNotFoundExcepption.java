package com.ifpb.edu.spendwise.exception.customer;

public class CustomerNotFoundExcepption extends RuntimeException {
    public CustomerNotFoundExcepption(String messsage, Throwable cause){
        super(messsage, cause);
    }
}
