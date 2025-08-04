package com.ifpb.edu.spendwise.model.dto;

import groovy.transform.builder.Builder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request object for creating a new customer")
public class CreateCustomerRequest {
    
    @NotBlank(message = "Name is required")
    @Size(max = 100, min = 5, message = "Name must be between 5 and 100 characters")
    @Schema(description = "Full name of the customer", example = "John Doe")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "Customer's email address", example = "john.doe@email.com")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 8 characters long")
    @Schema(description = "Customer's password", example = "MySecurePass123!")
    private String password;
}