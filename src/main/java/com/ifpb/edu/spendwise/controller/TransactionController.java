package com.ifpb.edu.spendwise.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ifpb.edu.spendwise.model.Account;
import com.ifpb.edu.spendwise.model.Category;
import com.ifpb.edu.spendwise.model.Customer;
import com.ifpb.edu.spendwise.model.Transaction;
import com.ifpb.edu.spendwise.model.dto.TransactionDTO;
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
public String showCreateForm(@RequestParam(name = "accountId") Long accountId, Model model){
    Account account = accountService.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Conta com ID " + accountId + " não encontrada"));
    
    TransactionDTO transactionDTO = new TransactionDTO();
    transactionDTO.setAccountId(account.getId());
    transactionDTO.setTransactionDate(LocalDateTime.now());
    model.addAttribute("account", account);
    model.addAttribute("transactionDTO", transactionDTO);
    model.addAttribute("categories", categoryService.findAll());
    
    return "account/transaction";
}

@PostMapping
public String createTransaction(@Valid @ModelAttribute("transactionDTO") TransactionDTO dto,
        BindingResult result, Model model, RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
        model.addAttribute("categories", categoryService.findAll());
        System.out.println(result.getAllErrors());
        for(ObjectError obj :result.getAllErrors()){
            System.out.println(obj.getDefaultMessage());
        }
        redirectAttributes.addAttribute("id", dto.getAccountId());
        return "redirect:/account?erro";
    }

    Transaction transaction = new Transaction();
    transaction.setValue(dto.getValue());
    transaction.setTransactionDate(dto.getTransactionDate());
    transaction.setAccount(accountService.findById(dto.getAccountId()).orElseThrow());
    transaction.setCategory(categoryService.findById(dto.getCategoryId()).orElseThrow());

    transactionService.createTransaction(transaction);

    redirectAttributes.addAttribute("id", dto.getAccountId());
    return "redirect:/account";
}


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        Transaction transaction = transactionService.findById(id);

        TransactionDTO dto = transactionService.toDTO(transaction);

        Customer loggedCustomer = sessionUtil.getLoggedCustomer(session);
        Long accountId = (Long) session.getAttribute("accountId");
        Account account = accountService.findById(accountId).orElseThrow();

        model.addAttribute("customer", loggedCustomer);
        model.addAttribute("transactionDTO", dto);
        model.addAttribute("account", account);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("transactionId", transaction.getId());
        
        return "/transactions/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateTransaction(
            @PathVariable Long id,
            @ModelAttribute("transactionDTO") @Valid TransactionDTO dto,
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

        dto.setId(id);
        Transaction transaction = transactionService.toEntity(dto);
        transactionService.updateTransaction(transaction);

        return "redirect:/account?id=" + dto.getAccountId();
    }

    @PostMapping("/delete/{id}")
public String deleteTransaction(@PathVariable Long id, HttpSession session) {
    Long accountId = (Long) session.getAttribute("accountId");
    transactionService.deleteTransaction(id);
    return "redirect:/account?id=" + accountId;
}

}
