package com.ifpb.edu.spendwise.exception.category;

public class CategoryException extends RuntimeException {
    public CategoryException(String messsage, Throwable cause){
        super(messsage, cause);
    }
    public CategoryException(String message){
        super(message);
    }
}

