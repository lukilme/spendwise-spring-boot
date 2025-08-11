package com.ifpb.edu.spendwise.config;

import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

import com.ifpb.edu.spendwise.model.Customer;
import com.ifpb.edu.spendwise.service.CustomerService;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final CustomerService userRepository;

    public MyUserDetailsService(CustomerService userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = userRepository.findByEmail(username);
        
        return new User(
            customer.getEmail(),
            customer.getPassword(),
            List.of(new SimpleGrantedAuthority(customer.getRole().toString()))
        );
    }
}