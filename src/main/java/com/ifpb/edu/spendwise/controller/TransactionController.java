package com.ifpb.edu.spendwise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ifpb.edu.spendwise.model.Account;
import com.ifpb.edu.spendwise.model.Customer;
import com.ifpb.edu.spendwise.model.Transaction;
import com.ifpb.edu.spendwise.service.AccountService;
import com.ifpb.edu.spendwise.service.CategoryService;
import com.ifpb.edu.spendwise.service.TransactionService;
import com.ifpb.edu.spendwise.util.SessionUtil;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;

import org.springframework.web.bind.annotation.PostMapping;

@Log4j2
@Controller
@RequestMapping("/transactions")
public class TransactionController {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String BLUE = "\u001B[34m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";
    public static final String MAGENTA = "\u001B[35m";
    @Autowired
    CategoryService categoryService;

    @Autowired
    TransactionService transactionService;
    
    @Autowired
    SessionUtil sessionUtil;

    @Autowired
    AccountService accountService;

    @GetMapping("/new")
    public String showCreateForm(
            HttpSession session, Model model) {

        Long accountId = (Long) session.getAttribute("accountId");
        Account account = accountService.findById(accountId).orElseThrow();

        log.info(YELLOW + account + RESET);

        Transaction transactionTarget = new Transaction();
        transactionTarget.setAccount(account);

        log.info(YELLOW + transactionTarget + RESET);

        model.addAttribute("account", account);
        // List<Category> categories = categoryService.findAll();

        model.addAttribute("transaction", transactionTarget);
        model.addAttribute("categories", categoryService.findAll());

        return "/account/transaction";
    }

    @PostMapping
    public String saveTransaction(
            @ModelAttribute("transaction") @Valid Transaction transaction,
            BindingResult br,
            Model model, HttpSession session) {

        log.info(GREEN + transaction + RESET);
        transactionService.createTransaction(transaction);
        Long accountId = (Long) session.getAttribute("accountId");
        return "redirect:/account?id=" + accountId;
    }

    @PostMapping("/edit/{id}")
    public String updateTransaction(
            @PathVariable Long id,
            @ModelAttribute("transaction") @Valid Transaction transaction,
            BindingResult br,
            Model model,
            HttpSession session) {

        if (br.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());

            Long accountId = (Long) session.getAttribute("accountId");
            Account account = accountService.findById(accountId).orElseThrow();

            model.addAttribute("account", account);

            return "/transactions/edit"; 
        }

        transaction.setId(id);

        if (transaction.getAccount() == null) {
            Long accountId = (Long) session.getAttribute("accountId");
            Account account = accountService.findById(accountId).orElseThrow();
            transaction.setAccount(account);
        }

        transactionService.updateTransaction(transaction);

        return "redirect:/transactions/edit/" + id;
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        Transaction transaction = transactionService.findById(id);

        Customer loggedCustomer = sessionUtil.getLoggedCustomer(session);

        Long accountId = (Long) session.getAttribute("accountId");
        Account account = accountService.findById(accountId).orElseThrow();

        transaction.setAccount(account);
        model.addAttribute("customer", loggedCustomer);
        model.addAttribute("transaction", transaction);
        model.addAttribute("account", account);
        model.addAttribute("categories", categoryService.findAll());
        return "/transactions/edit";
    }

    @PostMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id, HttpSession session) {

        Long accountId = (Long) session.getAttribute("accountId");
        transactionService.deleteTransaction(id);
        return "redirect:/account?id=" + accountId;
    }

}
