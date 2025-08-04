package com.ifpb.edu.spendwise.service.interfaces;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ifpb.edu.spendwise.model.Customer;

public interface CustomerServiceInterface {
    
    /**
     * Salva um novo cliente
     */
    Customer save(Customer customer);
    
    /**
     * Atualiza um cliente existente
     */
    Customer update(Customer customer);
    
    /**
     * Busca cliente por ID
     */
    Optional<Customer> getCustomerById(Long id);
    
    /**
     * Busca cliente por email
     */
    Optional<Customer> findByEmail(String email);
    
    /**
     * Lista todos os clientes com paginação
     */
    Page<Customer> findAll(Pageable pageable);
    
    /**
     * Busca clientes por nome ou email
     */
    Page<Customer> searchByNameOrEmail(String term, Pageable pageable);
    
    /**
     * Deleta cliente por ID
     */
    void deleteById(Long id);
    
    /**
     * Verifica se cliente existe por email
     */
    boolean existsByEmail(String email);
    
    /**
     * Ativa/desativa cliente
     */
    Customer toggleActiveStatus(Long id);
    
    /**
     * Busca clientes ativos
     */
    Page<Customer> findByActiveTrue(Pageable pageable);
    
    /**
     * Altera senha do cliente
     */
    void changePassword(Long customerId, String currentPassword, String newPassword);
}
