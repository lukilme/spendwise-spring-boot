package com.ifpb.edu.spendwise.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ifpb.edu.spendwise.exception.customer.CustomerBadRequestExecption;
import com.ifpb.edu.spendwise.exception.customer.CustomerNotFoundException;
import com.ifpb.edu.spendwise.exception.customer.CustomerSaveException;
import com.ifpb.edu.spendwise.exception.customer.DuplicateCustomerException;

@ControllerAdvice
public class CustomerException {
    @ExceptionHandler(CustomerSaveException.class)
    public ResponseEntity<String> handleCustomerSaveError(CustomerSaveException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to save customer. Please try again later:\n"+ex.getMessage());
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<String> handleCustomerNotFound(CustomerNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to find customer. Customer may not exists:\n"+ex.getMessage());
    }

    @ExceptionHandler(CustomerBadRequestExecption.class)
    public ResponseEntity<String> handleCustomerBadResquest(CustomerBadRequestExecption ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to request service, your request was poorly prepared:\n"+ex.getMessage());
    }

    @ExceptionHandler(DuplicateCustomerException.class)
    public ResponseEntity<String> handleDuplicateCustomerException(DuplicateCustomerException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Failed to request service, your request was poorly prepared:\\n" + ex.getMessage());
                   
    }

}
