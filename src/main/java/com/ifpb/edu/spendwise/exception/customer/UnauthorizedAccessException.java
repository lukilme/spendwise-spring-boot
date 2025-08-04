package com.ifpb.edu.spendwise.exception.customer;

public class UnauthorizedAccessException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public UnauthorizedAccessException(String message) {
        super(message);
    }
    
    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}