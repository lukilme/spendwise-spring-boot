package com.ifpb.edu.spendwise.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ifpb.edu.spendwise.model.Account;
import com.ifpb.edu.spendwise.model.Customer;
import com.ifpb.edu.spendwise.model.dto.CreateCustomerRequest;
import com.ifpb.edu.spendwise.model.enumerator.AccountTypes;
import com.ifpb.edu.spendwise.service.AccountService;
import com.ifpb.edu.spendwise.service.CustomerService;
import com.ifpb.edu.spendwise.util.Log;
import com.ifpb.edu.spendwise.util.SessionUtil;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SessionUtil sessionUtil;

    @GetMapping("/form")
    public ModelAndView registerCustomer(HttpSession session, Principal principal) {
        if(principal != null){
            Log.warning(principal.toString());
        }

        ModelAndView model = new ModelAndView("customer/form");
        model.addObject("candidateCustomer", new Customer());
        return model;
    }

    @GetMapping("/painel")
    public String painel(HttpSession session, Model model,
            @RequestParam(required = false) List<String> categoria,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "6") Integer size,
            @RequestParam(required = false, defaultValue = "desc") String order) {

        Customer customer = sessionUtil.getLoggedCustomer(session);
        if (!isCustomerAuthenticated(customer, model)) {
            return "customer/login";
        }

        try {
            Sort sort = createSort(order);
            PageRequest pageable = PageRequest.of(page, size, sort);
            List<AccountTypes> filterTypes = parseAccountTypes(categoria);

            Page<Account> accountsPage = getFilteredAccounts(customer.getId(), filterTypes, pageable);
            Account newAccount = createDefaultAccount(customer);

            addPaginationAttributes(model, page, size, accountsPage);
            addAccountAttributes(model, accountsPage, filterTypes, sort, customer, newAccount);

            return "customer/painel";
        } catch (Exception e) {
            handleError(customer, model, e);
            return "customer/painel";
        }
    }

    private boolean isCustomerAuthenticated(Customer customer, Model model) {
        if (customer == null) {
            Log.warning("Tentativa de acesso ao painel sem autenticação");
            model.addAttribute("customerLoginCredential", new Customer());
            return false;
        }
        return true;
    }

    private Sort createSort(String order) {
        Sort sort = Sort.by("id");
        return "asc".equalsIgnoreCase(order) ? sort.ascending() : sort.descending();
    }

    private List<AccountTypes> parseAccountTypes(List<String> categories) {
        if (categories == null) {
            return List.of();
        }

        logSelectedCategories(categories);
        return categories.stream()
                .map(String::toUpperCase)
                .map(AccountTypes::valueOf)
                .collect(Collectors.toList());
    }

    private void logSelectedCategories(List<String> categories) {
        categories.forEach(cat -> System.out.println("Categoria selecionada: " + cat));
    }

    private Account createDefaultAccount(Customer customer) {
        Account newAccount = new Account();
        newAccount.setName("Generic Card");
        newAccount.setActive(true);
        newAccount.setBalance(BigDecimal.ZERO);
        newAccount.setExpirationDate(LocalDate.now().plusYears(10));
        newAccount.setAccountType(AccountTypes.CREDIT);
        newAccount.setCustomer(customer);
        return newAccount;
    }

    private void addPaginationAttributes(Model model, int page, int size, Page<Account> accountsPage) {
        long totalItems = accountsPage.getTotalElements();
        int inicio = page * size + 1;
        long fim = Math.min((page + 1) * size, totalItems);

        model.addAttribute("inicio", inicio);
        model.addAttribute("fim", fim);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPages", accountsPage.getTotalPages());
    }

    private void addAccountAttributes(Model model, Page<Account> accountsPage,
            List<AccountTypes> filterTypes, Sort sort, Customer customer, Account newAccount) {
        model.addAttribute("accountsPage", accountsPage);
        model.addAttribute("accounts", accountsPage.getContent());
        model.addAttribute("accountTypeFilter", filterTypes);
        model.addAttribute("sort", sort);
        model.addAttribute("customer", customer);
        model.addAttribute("account", newAccount);
    }

    private void handleError(Customer customer, Model model, Exception e) {
        log.error("Erro ao carregar painel do cliente ID: {}", customer.getId(), e);
        model.addAttribute("errorMessage", "Erro ao carregar dados do painel");
    }

    @SuppressWarnings("unused")
    private List<AccountTypes> getAccountTypesFromSession(HttpSession session) {
        Object attr = session.getAttribute("filterCategories");
        List<AccountTypes> types = new ArrayList<>();

        if (attr instanceof List<?>) {
            List<?> rawList = (List<?>) attr;
            for (Object item : rawList) {
                if (item instanceof AccountTypes) {
                    types.add((AccountTypes) item);
                } else {
                    log.warn("Item inválido encontrado na lista de filtros: {}", item);
                }
            }
        }

        return types;
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout, HttpSession session, Model model) {

        if (error != null)
            model.addAttribute("error", "Usuário ou senha inválidos.");
        if (logout != null)
            model.addAttribute("msg", "Logout realizado com sucesso.");
        return "customer/login";
    }

    private Page<Account> getFilteredAccounts(Long customerId, List<AccountTypes> types, PageRequest pageable) {
        if (types != null && !types.isEmpty()) {
            return accountService.findByCustomerIdAndAccountTypeIn(customerId, types,
                    pageable);
        } else {
            return accountService.findByCustomerId(customerId, pageable);
        }
    }

    @PostMapping("/register")
    public String saveNewCustomer(
            @Valid @ModelAttribute("candidateCustomer") CreateCustomerRequest newCustomer,
            BindingResult result,
            Model model,
            RedirectAttributes attr,
            HttpSession session) {

        log.info("Iniciando criação de cliente com email: {}", newCustomer.getEmail());

        if (result.hasErrors()) {
            log.warn("Erro de validação ao criar cliente: {}", result.getAllErrors());
            model.addAttribute("candidateCustomer", newCustomer);
            return "customer/form";
        }

        try {

            Customer savedCustomer = customerService.createCustomer(newCustomer);

            sessionUtil.setLoggedCustomer(session, savedCustomer);

            attr.addFlashAttribute("successMessage", "Cliente criado com sucesso!");
            log.info("Cliente criado com sucesso. ID: {}, Email: {}", savedCustomer.getId(), savedCustomer.getEmail());

            return "redirect:/customer/login";

        } catch (DataIntegrityViolationException e) {
            log.warn("Tentativa de criar cliente duplicado: {}", newCustomer.getEmail());
            result.rejectValue("email", "duplicate", "Cliente já existe com este email");
            model.addAttribute("candidateCustomer", newCustomer);
            return "customer/form";

        } catch (Exception e) {
            log.error("Erro inesperado ao criar cliente", e);
            model.addAttribute("errorMessage", "Erro interno do servidor. Tente novamente.");
            model.addAttribute("candidateCustomer", newCustomer);
            return "customer/form";
        }
    }

    @SuppressWarnings("unused")
    private Sort buildSortFromSession(HttpSession session, String order) {
        if (order == null) {
            order = "asc";
        }
        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;

        return Sort.by(direction, "expirationDate");
    }

    @GetMapping("/logout")
    public ModelAndView logout(ModelAndView model, HttpSession session) {
        session.invalidate();
        return new ModelAndView("redirect:/customer/login");
    }

    public ModelAndView changeCredentialCustomer(ModelAndView model, HttpSession session) {
        return model;
    }

}
