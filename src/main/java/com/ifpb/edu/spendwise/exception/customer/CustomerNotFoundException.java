package com.ifpb.edu.spendwise.exception.customer;

public class CustomerNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public CustomerNotFoundException(String message) {
        super(message);
    }
    
    public CustomerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public CustomerNotFoundException(Long customerId) {
        super("Cliente não encontrado com ID: " + customerId);
    }
}