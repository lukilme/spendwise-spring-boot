// package com.ifpb.edu.spendwise.controller;

// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.servlet.ModelAndView;

// import com.ifpb.edu.spendwise.model.Customer;
// import com.ifpb.edu.spendwise.model.enumerator.UserRoles;

// import jakarta.servlet.http.HttpSession;

// @Controller
// @RequestMapping("/")
// public class DefaultController {

//     @GetMapping
//     public ModelAndView index(HttpSession session) {
//         return new ModelAndView("redirect:/home");
//     }

//     @GetMapping("/home")
// public ModelAndView redirecionarPorRole(HttpSession session) {
//     Customer customer = (Customer) session.getAttribute("customer");
//     if (customer == null) {
//         return new ModelAndView("redirect:/auth/login");
//     }
//     if (customer.getRole() == UserRoles.ADMINISTRATOR) {
//         return new ModelAndView("redirect:/administrator/painel");
//     } else {
//         return new ModelAndView("redirect:/customer/painel"); 
//     }
// }
// }
