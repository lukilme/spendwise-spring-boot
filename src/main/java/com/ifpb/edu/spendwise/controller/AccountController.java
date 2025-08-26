package com.ifpb.edu.spendwise.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ifpb.edu.spendwise.model.Account;
import com.ifpb.edu.spendwise.model.Customer;
import com.ifpb.edu.spendwise.model.Transaction;
import com.ifpb.edu.spendwise.model.dto.TransactionDTO;
import com.ifpb.edu.spendwise.model.dto.TransactionListDTO;
import com.ifpb.edu.spendwise.model.enumerator.AccountTypes;
import com.ifpb.edu.spendwise.repository.interfaces.TransactionCommentCount;
import com.ifpb.edu.spendwise.service.AccountService;
import com.ifpb.edu.spendwise.service.CustomerService;
import com.ifpb.edu.spendwise.service.TransactionService;
import com.ifpb.edu.spendwise.util.SessionUtil;

import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/account")
public class AccountController {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String BLUE = "\u001B[34m";
    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SessionUtil sessionUtil;

    @GetMapping("/filter")
    public String filterAccounts(
            HttpSession session,
            @RequestParam(name = "categoria", required = false) List<AccountTypes> categorias,
            @RequestParam(name = "ordem", defaultValue = "asc") String ordem) {

        session.setAttribute("filterCategories", categorias);
        session.setAttribute("filterOrder", ordem);

        return "redirect:/customer/painel";
    }

@PostMapping
public String createAccount(
        @ModelAttribute("account") @Valid Account account,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes,
        @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

    try {
      
        String email = userDetails.getUsername();
        Customer loggedCustomer = customerService.findByEmail(email);

        if (loggedCustomer == null) {
            redirectAttributes.addFlashAttribute("error", "Usuário não encontrado. Faça login novamente.");
            return "redirect:/customer/login";
        }

        account.setCustomer(loggedCustomer);

        if (result.hasErrors()) {
            model.addAttribute("error", "Corrija os erros no formulário.");
            model.addAttribute("account", account);
            model.addAttribute("customer", loggedCustomer);

            return "redirect:customer/painel"; 
        }

        accountService.createAccount(account);
        redirectAttributes.addFlashAttribute("success", "Conta criada com sucesso!");
        return "redirect:/customer/painel";

    } catch (DataIntegrityViolationException e) {
        model.addAttribute("error", "Existe uma conta com esses dados. Verifique as informações.");
        model.addAttribute("account", account);

        return "redirect:customer/painel";

    } catch (IllegalArgumentException e) {
        model.addAttribute("error", "Dados inválidos fornecidos. Verifique as informações.");
        model.addAttribute("account", account);


        return "redirect:customer/painel";
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Ocorreu um erro inesperado. Tente novamente.");
        return "redirect:/customer/painel";
    }
}


@GetMapping
public String viewAccount(
        @RequestParam("id") Long accountId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "transactionDate") String sortBy,
        @RequestParam(defaultValue = "desc") String sortDir,
        HttpSession session,
        Model model) {

    Account account = accountService.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

    Sort sort = sortDir.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();
    Pageable pageable = PageRequest.of(page, size, sort);

    // Buscar TransactionCommentCount (ou projection)
    Page<TransactionCommentCount> transactionPage = transactionService.findAllWithCommentCount(accountId, pageable);

    // Mapear para DTO
    Page<TransactionListDTO> dtoPage = transactionPage.map(tc -> new TransactionListDTO(tc.getTransaction(), tc.getCommentCount()));

    model.addAttribute("account", account);
    model.addAttribute("transactionPage", dtoPage); // Page<TransactionListDTO>
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortDir", sortDir);
    model.addAttribute("accountId", accountId);

    return "account/details";
}



}