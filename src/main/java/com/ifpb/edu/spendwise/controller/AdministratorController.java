package com.ifpb.edu.spendwise.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ifpb.edu.spendwise.model.Customer;
import com.ifpb.edu.spendwise.model.enumerator.UserRoles;
import com.ifpb.edu.spendwise.service.AdministratorService;
import com.ifpb.edu.spendwise.service.AuthService;
import com.ifpb.edu.spendwise.service.CustomerService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/manager")
public class AdministratorController {

    @Autowired
    private AdministratorService administratorService;

    @Autowired
    private CustomerService customerService;

    @GetMapping()
    public ModelAndView maanger(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session, ModelAndView model) {

        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null || customer.getRole() != UserRoles.ROLE_ADMINISTRATOR) {
            model.setViewName("redirect:/customer/login");
            return model;
        }
        model.addObject("customer", customer);
        model.setViewName("administrator/user_manager");
        List<Customer> admins = administratorService.getCustomers(UserRoles.ROLE_ADMINISTRATOR, page, size, "id");
        List<Customer> commons = administratorService.getCustomers(UserRoles.ROLE_COMMON, page, size, "id");

        model.addObject("admins", admins);
        model.addObject("commons", commons);
        return model;
    }

    @PostMapping("/delete_customer")
    public String deleteCustomer(@RequestParam("customerIdInput") Long customerId,
            @RequestParam("adminPassword") String password,
            HttpSession session, RedirectAttributes redirect) {

        Customer customerTarget = this.customerService.findById(customerId);
        System.out.println(customerTarget);

        Customer admin = (Customer) session.getAttribute("customer");
        System.out.println(admin);
        System.out.println("Senha digitada: " + password);
        System.out.println("Senha do admin: " + admin.getPassword());
        System.out.println("Resultado da verificação: " +
                AuthService.checkPass(password, admin.getPassword()));
        if (admin == null || !AuthService.checkPass(password, admin.getPassword())) {
            redirect.addFlashAttribute("error", "Senha incorreta.");
            System.out.println("Senha errada");
            return "redirect:/administrator/painel";
        }
        administratorService.deleteCustomerById(customerTarget.getId());
        System.out.println("ID : " + customerTarget + "deleted");
        redirect.addFlashAttribute("success", "Cliente excluído com sucesso.");
        return "redirect:/administrator/painel";
    }

    public ModelAndView customerEditor(ModelAndView model, HttpSession session) {

        throw new UnsupportedOperationException("Unimplemented method 'customerEditor'");
    }

}
