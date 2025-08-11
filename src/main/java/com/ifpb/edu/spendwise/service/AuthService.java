package com.ifpb.edu.spendwise.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ifpb.edu.spendwise.exception.customer.CustomerBadRequestExecption;
import com.ifpb.edu.spendwise.model.Customer;
import com.ifpb.edu.spendwise.repository.CustomerRepository;

@Service
public class AuthService {

    @Autowired
    private CustomerRepository customerRepository;

    public static String hashPassword(String plainTextPassword) {
        BCryptPasswordEncoder sup = new BCryptPasswordEncoder();
        return sup.encode(plainTextPassword);
    }

    public static boolean checkPass(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    public Customer validateLogin(Customer customerLogin) {
        try {
            if (customerLogin.getEmail().trim().isEmpty() || customerLogin.getPassword().trim().isEmpty()) {
                throw new Exception("Request format invalid");
            }

            Optional<Customer> customerTarget_ = this.customerRepository.findByEmail(customerLogin.getEmail());

            if (customerTarget_.isPresent()) {
                Customer customerTarget = customerTarget_.get();

                if (checkPass(customerLogin.getPassword(), customerTarget.getPassword())) {
                    return customerTarget;
                }
            }

            return null;
        } catch (Exception e) {
            throw new CustomerBadRequestExecption("Customer login request was unsuccessful", e);
        }
    }

}
