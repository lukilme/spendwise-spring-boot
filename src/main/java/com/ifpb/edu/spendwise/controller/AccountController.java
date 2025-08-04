package com.ifpb.edu.spendwise.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
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
import com.ifpb.edu.spendwise.model.enumerator.AccountTypes;
import com.ifpb.edu.spendwise.repository.interfaces.TransactionCommentCount;
import com.ifpb.edu.spendwise.service.AccountService;
import com.ifpb.edu.spendwise.service.TransactionService;
import com.ifpb.edu.spendwise.util.LoggerHandle;
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
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Customer loggedCustomer = (Customer) session.getAttribute("customer");

    
            if (loggedCustomer == null) {
                redirectAttributes.addFlashAttribute("error", "Sessão expirada. Faça login novamente.");
                return "redirect:/customer/login";
            }

            account.setCustomer(loggedCustomer);

            if (result.hasErrors()) {
                model.addAttribute("error", "Corrija os erros no formulário.");
                LoggerHandle.warning("Erros de validação ao criar conta para usuário ID:"+loggedCustomer.getId());
                return "account/form";
            }

            accountService.createAccount(account);
            redirectAttributes.addFlashAttribute("success", "Conta criada com sucesso!");
            LoggerHandle.info("Conta criada com sucesso para usuário ID: "+ loggedCustomer.getId());

            return "redirect:/customer/painel";

        } catch (DataIntegrityViolationException e) {
            LoggerHandle.erro(e);
            model.addAttribute("error", "existe uma conta com esses dados. Verifique as informações.");
            return "account/form";

        } catch (IllegalArgumentException e) {
            LoggerHandle.erro(e);
            model.addAttribute("error", "dados inválidos fornecidos. Verifique as informações.");
            return "account/form";

        } catch (Exception e) {
            LoggerHandle.erro(e);
            redirectAttributes.addFlashAttribute("error", "Ocorreu um erro inesperado. Tente novamente.");
            return "redirect:/account/form";
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

        Customer loggedCustomer = sessionUtil.getLoggedCustomer(session);
        if (loggedCustomer == null) {
            return "redirect:/customer/login";
        }

        Optional<Account> accountOpt = accountService.findById(accountId);
        if (accountOpt.isEmpty()) {
            return "redirect:/customer/painel";
        }

        Account account = accountOpt.get();
        if (!account.getCustomer().getId().equals(loggedCustomer.getId())) {
            return "redirect:/customer/painel";
        }

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TransactionCommentCount> transactionPage = transactionService.findAllWithCommentCount(accountId, pageable);

        model.addAttribute("account", account);
        model.addAttribute("customer", loggedCustomer);
        model.addAttribute("transactionPage", transactionPage);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("accountId", accountId);
        session.setAttribute("accountId", account.getId());
        
        // (src/main/resources/templates/account/details.html)
        return "account/details";
    }

}