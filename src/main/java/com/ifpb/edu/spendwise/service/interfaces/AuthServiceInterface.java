package com.ifpb.edu.spendwise.service.interfaces;

import com.ifpb.edu.spendwise.model.Customer;

/**
 * Authentication Service Interface
 * 
 * This interface defines the contract for authentication-related operations
 * including user login validation, authentication checks, password management,
 * and role-based access control.
 * 
 */
public interface AuthServiceInterface {
    
    /**
     * Validates user login credentials and returns customer information
     * 
     * @param customerLogin DTO containing login credentials (email/username and password)
     * @return CustomerResponseDTO containing authenticated customer data
     * @throws AuthenticationException if credentials are invalid
     * @throws IllegalArgumentException if customerLogin is null or contains invalid data
     */
    Customer validateLogin(Customer customerLogin);
    
    /**
     * Checks if a customer is currently authenticated
     * 
     * @param customerCredential DTO containing customer credentials to verify
     * @return true if customer is authenticated, false otherwise
     * @throws IllegalArgumentException if customerCredential is null
     */
    boolean isAuthenticated(Customer customerCredential);
    
    /**
     * Resets customer password using provided update information
     * 
     * @param customerPasswordUpdater DTO containing password reset data (old password, new password, etc.)
     * @return CustomerResponseDTO containing updated customer information
     * @throws AuthenticationException if current password is incorrect
     * @throws ValidationException if new password doesn't meet security requirements
     * @throws IllegalArgumentException if customerPasswordUpdater is null
     */
    Customer resetPassword(Customer customerPasswordUpdater);
    
    /**
     * Initiates forgot password process by sending reset instructions to customer's email
     * 
     * @param email customer's email address for password reset
     * @throws EmailNotFoundException if email doesn't exist in the system
     * @throws MessagingException if email sending fails
     * @throws IllegalArgumentException if email is null or invalid format
     */
    void forgotPassword(String email);
    
    /**
     * Checks if the current authenticated user has specific roles
     * 
     * @param roles array of role names to check against current user
     * @return true if user has at least one of the specified roles, false otherwise
     * @throws AuthenticationException if no user is currently authenticated
     * @throws IllegalArgumentException if roles array is null or empty
     */
    boolean hasRole(String roles);
    
    /**
     * Logs out the current authenticated user
     * 
     * @param token authentication token to invalidate
     * @throws AuthenticationException if token is invalid or already expired
     * @throws IllegalArgumentException if token is null
     */
    void logout(String token);
    
    /**
     * Refreshes authentication token for extended session
     * 
     * @param refreshToken current refresh token
     * @return new authentication token
     * @throws AuthenticationException if refresh token is invalid or expired
     * @throws IllegalArgumentException if refreshToken is null
     */
    String refreshAuthToken(String refreshToken);
}