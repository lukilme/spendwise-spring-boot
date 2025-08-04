package com.ifpb.edu.spendwise.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ifpb.edu.spendwise.exception.customer.CustomerNotFoundExcepption;
import com.ifpb.edu.spendwise.model.Customer;
import com.ifpb.edu.spendwise.model.enumerator.UserRoles;
import com.ifpb.edu.spendwise.repository.CustomerRepository;
import com.ifpb.edu.spendwise.service.interfaces.AdministratorServiceInterface;

@Service
public class AdministratorService implements AdministratorServiceInterface {

    @Autowired
    CustomerRepository customerRepository;

    public List<Customer> getCustomers(UserRoles role_requested, int page, int size, String sort){
        
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Customer> commonCustomers = this.customerRepository.findByRole(role_requested, pageable);
        
        return commonCustomers.getContent();
    }
    
    public int countCustomersByRole(UserRoles role_requested){
        return this.customerRepository.countByRole(role_requested);
    }

    public void deleteCustomerById(Long customerId) {
        Optional<Customer> customerToDelete = this.customerRepository.findById(customerId);
        this.customerRepository.deleteById(customerId);
        if(customerToDelete.get()!=null){
            this.customerRepository.deleteById(customerId);
        }else{
            throw new CustomerNotFoundExcepption("Customer ID not found for delete",null);
        }
    }

    public Customer updateCustomer(Customer customerToUpdated){
        return customerToUpdated;
    }

    

}
