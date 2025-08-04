package com.ifpb.edu.spendwise.exception.customer;

public class CustomerBadRequestExecption extends RuntimeException {
    public CustomerBadRequestExecption(String messsage, Throwable cause){
        super(messsage, cause);
    }
    public CustomerBadRequestExecption(String message){
        super(message);
    }
}
