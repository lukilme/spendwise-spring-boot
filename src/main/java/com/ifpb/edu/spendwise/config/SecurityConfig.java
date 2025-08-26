package com.ifpb.edu.spendwise.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.ifpb.edu.spendwise.model.Customer;
import com.ifpb.edu.spendwise.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    CustomerRepository customerRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/customer/form", "/customer/register").permitAll()
                        .requestMatchers("/administrator/**").hasRole("ADMINISTRATOR")
                        .requestMatchers("/customer/**").hasAnyRole("ADMINISTRATOR", "COMMON")
                        .requestMatchers("/transactions/**").hasAnyRole("ADMINISTRATOR", "COMMON")
                        .requestMatchers("/commentary/**").hasAnyRole("ADMINISTRATOR", "COMMON")
                        .requestMatchers("/account/**").hasAnyRole("ADMINISTRATOR", "COMMON")
                        .requestMatchers("/categories/**").hasAnyRole("ADMINISTRATOR")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/customer/login")
                        .loginProcessingUrl("/auth/login")
                        .defaultSuccessUrl("/customer/painel", true)
                        .failureUrl("/customer/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/customer/logout")
                        .logoutSuccessUrl("/customer/login")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID"))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)

                )
                .requestCache(requestCache -> requestCache.disable());

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Customer customer = customerRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return org.springframework.security.core.userdetails.User
                    .withUsername(customer.getEmail())
                    .password(customer.getPassword())
                    .roles(customer.getRole().name().replace("ROLE_", ""))
                    .disabled(!customer.getActive())
                    .build();
        };
    }

    // @Bean
    // public SecurityFilterChain publicEndpointsFilterChain(HttpSecurity http)
    // throws Exception {
    // http
    // .securityMatcher("/public/**", "/api/public/**")
    // .authorizeHttpRequests(auth -> auth
    // .anyRequest().permitAll())
    // .csrf(csrf -> csrf.disable())
    // .cors(cors -> cors.disable())
    // .formLogin(form -> form.disable())
    // .httpBasic(basic -> basic.disable())
    // .logout(logout -> logout.disable())
    // .sessionManagement(session -> session
    // .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    // return http.build();
    // }
}