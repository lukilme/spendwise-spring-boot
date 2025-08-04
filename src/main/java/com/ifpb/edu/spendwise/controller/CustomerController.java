package com.ifpb.edu.spendwise.controller;

import java.math.BigDecimal;
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
import com.ifpb.edu.spendwise.model.enumerator.AccountTypes;
import com.ifpb.edu.spendwise.service.AccountService;
import com.ifpb.edu.spendwise.service.AuthService;
import com.ifpb.edu.spendwise.service.CustomerService;
import com.ifpb.edu.spendwise.util.LoggerHandle;
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

    @Autowired
    private AuthService authService;

    @GetMapping("/form")
    public ModelAndView registerCustomer(HttpSession session) {
        Customer loggedCustomer = sessionUtil.getLoggedCustomer(session);

        if (loggedCustomer != null) {
            log.info("Cliente já logado tentando acessar formulário de registro. ID: {}", loggedCustomer.getId());
            return new ModelAndView("dashboard");
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
            LoggerHandle.warning("Tentativa de acesso ao painel sem autenticação");
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
    public ModelAndView login(HttpSession session) {
        Customer loggedCustomer = (Customer) session.getAttribute("customer");
        if (loggedCustomer != null) {
            return new ModelAndView("redirect:/customer/painel");
        }

        ModelAndView model = new ModelAndView("customer/login");
        model.addObject("customerLoginCredential", new Customer());
        return model;
    }

    private Page<Account> getFilteredAccounts(Long customerId, List<AccountTypes> types, PageRequest pageable) {
        if (types != null && !types.isEmpty()) {
            return accountService.findByCustomerIdAndAccountTypeIn(customerId, types,
                    pageable);
        } else {
            return accountService.findByCustomerId(customerId, pageable);
        }
    }

    @PostMapping
    public String saveNewCustomer(
            @Valid @ModelAttribute("candidateCustomer") Customer customer,
            BindingResult result,
            Model model,
            RedirectAttributes attr,
            HttpSession session) {

        log.info("Iniciando criação de cliente com email: {}", customer.getEmail());

        if (result.hasErrors()) {
            log.warn("Erro de validação ao criar cliente: {}", result.getAllErrors());
            model.addAttribute("candidateCustomer", customer);
            return "customer/form";
        }

        try {
            Customer savedCustomer = customerService.createCustomer(customer);

            sessionUtil.setLoggedCustomer(session, savedCustomer);

            attr.addFlashAttribute("successMessage", "Cliente criado com sucesso!");
            log.info("Cliente criado com sucesso. ID: {}, Email: {}", savedCustomer.getId(), savedCustomer.getEmail());

            return "redirect:/customer/painel";

        } catch (DataIntegrityViolationException e) {
            log.warn("Tentativa de criar cliente duplicado: {}", customer.getEmail());
            result.rejectValue("email", "duplicate", "Cliente já existe com este email");
            model.addAttribute("candidateCustomer", customer);
            return "customer/form";

        } catch (Exception e) {
            log.error("Erro inesperado ao criar cliente", e);
            model.addAttribute("errorMessage", "Erro interno do servidor. Tente novamente.");
            model.addAttribute("candidateCustomer", customer);
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

    @PostMapping("/login")
    public String validate(
            @ModelAttribute Customer customer,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            Customer authenticatedCustomer = authService.validateLogin(customer);
            System.out.println(authenticatedCustomer);

            session.setAttribute("customer", authenticatedCustomer);

            session.setMaxInactiveInterval(5 * 60);

            redirectAttributes.addFlashAttribute("message", "Login successful");
            return "redirect:/customer/painel";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/customer/login";
        }

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

// import java.util.ArrayList;
// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.dao.DataIntegrityViolationException;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.validation.BindingResult;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.servlet.ModelAndView;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// import com.ifpb.edu.spendwise.exception.customer.CustomerNotFoundException;
// import com.ifpb.edu.spendwise.exception.customer.UnauthorizedAccessException;
// import com.ifpb.edu.spendwise.logger.ApplicationLogger;
// import com.ifpb.edu.spendwise.model.Account;
// import com.ifpb.edu.spendwise.model.Customer;
// import com.ifpb.edu.spendwise.model.dto.CustomerDTO;
// import com.ifpb.edu.spendwise.model.enumerator.AccountTypes;
// import com.ifpb.edu.spendwise.service.AccountService;
// import com.ifpb.edu.spendwise.service.CustomerService;
// import com.ifpb.edu.spendwise.util.SessionUtil;

// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Sort;
// import org.springframework.security.access.prepost.PreAuthorize;

// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpSession;
// import jakarta.validation.Valid;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Controller
// @RequestMapping("/customer")
// public class CustomerController {

// @Autowired
// private CustomerService customerService;

// @Autowired
// private AccountService accountService;

// @Autowired
// private SessionUtil sessionUtil;

// private static final String REDIRECT_HOME = "redirect:/home";
// private static final String REDIRECT_LOGIN = "redirect:/auth/login";
// private static final String "redirect:/customer/painel" =
// "redirect:/customer/painel";
// private static final String CUSTOMER_SESSION_ATTR = "customer";
// private static final String FILTER_ORDER_ATTR = "filterOrder";
// private static final String FILTER_CATEGORIES_ATTR = "filterCategories";
// private static final String DEFAULT_SORT_ORDER = "asc";
// private static final String "customer/form" = "customer/form";
// private static final String "customer/painel" = "customer/painel";
// private static final String CUSTOMER_EDIT_VIEW = "customer/edit";
// private static final String CUSTOMER_LIST_VIEW = "customer/list";

// @GetMapping("/form")
// public ModelAndView registerCustomer(HttpSession session) {
// Customer loggedCustomer = sessionUtil.getLoggedCustomer(session);

// if (loggedCustomer != null) {
// log.info("Cliente já logado tentando acessar formulário de registro. ID: {}",
// loggedCustomer.getId());
// return new ModelAndView(REDIRECT_HOME);
// }

// ModelAndView model = new ModelAndView("customer/form");
// model.addObject("candidateCustomer", new Customer());
// return model;
// }

// @GetMapping("/painel")
// public String painel(HttpSession session, Model model,
// @RequestParam(defaultValue = "0") int page,
// @RequestParam(defaultValue = "10") int size) {

// Customer customer = sessionUtil.getLoggedCustomer(session);
// if (customer == null) {
// log.warn("Tentativa de acesso ao painel sem autenticação");
// return REDIRECT_LOGIN;
// }

// try {
// // Configurar paginação e ordenação
// Sort sort = buildSortFromSession(session);
// PageRequest pageable = PageRequest.of(page, size, sort);

// // Buscar contas com filtros
// List<AccountTypes> filterTypes = getAccountTypesFromSession(session);
// Page<Account> accountsPage = getFilteredAccounts(customer.getId(),
// filterTypes, pageable);

// // Adicionar atributos ao model
// Account newAccount = new Account();
// newAccount.setCustomer(customer);

// model.addAttribute("accountsPage", accountsPage);
// model.addAttribute("accounts", accountsPage.getContent());
// model.addAttribute("accountTypeFilter", filterTypes);
// model.addAttribute("sort", sort);
// model.addAttribute("customer", customer);
// model.addAttribute("account", newAccount);
// model.addAttribute("currentPage", page);
// model.addAttribute("totalPages", accountsPage.getTotalPages());

// log.info("Painel carregado para cliente ID: {} com {} contas",
// customer.getId(), accountsPage.getTotalElements());

// return "customer/painel";
// } catch (Exception e) {
// log.error("Erro ao carregar painel do cliente ID: {}", customer.getId(), e);
// model.addAttribute("errorMessage", "Erro ao carregar dados do painel");
// return "customer/painel";
// }
// }

// @PostMapping
// public String saveNewCustomer(
// @Valid @ModelAttribute("candidateCustomer") Customer customer,
// BindingResult result,
// Model model,
// RedirectAttributes attr,
// HttpSession session) {

// log.info("Iniciando criação de cliente com email: {}", customer.getEmail());

// if (result.hasErrors()) {
// log.warn("Erro de validação ao criar cliente: {}", result.getAllErrors());
// model.addAttribute("candidateCustomer", customer);
// return "customer/form";
// }

// try {
// Customer savedCustomer = customerService.save(customer);

// // Configurar sessão para o novo cliente
// sessionUtil.setLoggedCustomer(session, savedCustomer);

// attr.addFlashAttribute("successMessage", "Cliente criado com sucesso!");
// log.info("Cliente criado com sucesso. ID: {}, Email: {}",
// savedCustomer.getId(), savedCustomer.getEmail());

// return "redirect:/customer/painel";

// } catch (DataIntegrityViolationException e) {
// log.warn("Tentativa de criar cliente duplicado: {}", customer.getEmail());
// result.rejectValue("email", "duplicate", "Cliente já existe com este email");
// model.addAttribute("candidateCustomer", customer);
// return "customer/form";

// } catch (Exception e) {
// log.error("Erro inesperado ao criar cliente", e);
// model.addAttribute("errorMessage", "Erro interno do servidor. Tente
// novamente.");
// model.addAttribute("candidateCustomer", customer);
// return "customer/form";
// }
// }

// @GetMapping("/edit")
// public ModelAndView editCustomer(@RequestParam Long id,
// @RequestParam(defaultValue = "false") boolean editMode,
// HttpSession session) {

// Customer loggedCustomer = sessionUtil.getLoggedCustomer(session);

// try {
// validateCustomerAccess(loggedCustomer, id);

// Customer customer = customerService.getCustomerById(id)
// .orElseThrow(() -> new CustomerNotFoundException("Cliente não encontrado"));

// ModelAndView modelAndView = new ModelAndView(CUSTOMER_EDIT_VIEW);
// modelAndView.addObject("customer", customer);
// modelAndView.addObject("customerId", id);
// modelAndView.addObject("editMode", editMode);

// log.info("Formulário de edição carregado para cliente ID: {}", id);
// return modelAndView;

// } catch (CustomerNotFoundException e) {
// log.warn("Tentativa de editar cliente inexistente. ID: {}", id);
// ModelAndView errorView = new ModelAndView("redirect:/customer/painel");
// return errorView;
// } catch (UnauthorizedAccessException e) {
// log.warn("Tentativa de acesso não autorizado para editar cliente ID: {} por
// cliente ID: {}",
// id, loggedCustomer != null ? loggedCustomer.getId() : "null");
// ModelAndView errorView = new ModelAndView(REDIRECT_LOGIN);
// return errorView;
// }
// }

// @PostMapping("/update")
// public String updateCustomer(
// @Valid @ModelAttribute Customer customer,
// BindingResult result,
// Model model,
// RedirectAttributes attr,
// HttpSession session) {

// Customer loggedCustomer = sessionUtil.getLoggedCustomer(session);

// try {
// validateCustomerAccess(loggedCustomer, customer.getId());

// if (result.hasErrors()) {
// model.addAttribute("customer", customer);
// return CUSTOMER_EDIT_VIEW;
// }

// Customer updatedCustomer = customerService.update(customer);
// sessionUtil.setLoggedCustomer(session, updatedCustomer);

// attr.addFlashAttribute("successMessage", "Cliente atualizado com sucesso!");
// log.info("Cliente atualizado com sucesso. ID: {}", updatedCustomer.getId());

// return "redirect:/customer/painel";

// } catch (DataIntegrityViolationException e) {
// log.warn("Tentativa de atualizar cliente com email duplicado: {}",
// customer.getEmail());
// result.rejectValue("email", "duplicate", "Email já está em uso por outro
// cliente");
// model.addAttribute("customer", customer);
// return CUSTOMER_EDIT_VIEW;
// } catch (Exception e) {
// log.error("Erro ao atualizar cliente ID: {}", customer.getId(), e);
// model.addAttribute("errorMessage", "Erro ao atualizar cliente");
// model.addAttribute("customer", customer);
// return CUSTOMER_EDIT_VIEW;
// }
// }

// @GetMapping("/view/{id}")
// public ModelAndView viewCustomer(@PathVariable Long id, HttpSession session)
// {
// Customer loggedCustomer = sessionUtil.getLoggedCustomer(session);

// try {
// validateCustomerAccess(loggedCustomer, id);

// Customer customer = customerService.getCustomerById(id).orElseThrow(() -> new
// CustomerNotFoundException(id));

// List<Account> accounts = accountService.findByCustomerId(id,
// Sort.by("createdAt").descending());

// ModelAndView modelAndView = new ModelAndView("customer/view");
// modelAndView.addObject("customer", customer);
// modelAndView.addObject("accounts", accounts);
// modelAndView.addObject("totalAccounts", accounts.size());

// log.info("Detalhes do cliente visualizados. ID: {}", id);
// return modelAndView;

// } catch (CustomerNotFoundException e) {
// log.warn("Tentativa de visualizar cliente inexistente. ID: {}", id);
// return new ModelAndView("redirect:/customer/painel");
// } catch (UnauthorizedAccessException e) {
// log.warn("Tentativa de acesso não autorizado aos detalhes do cliente ID: {}",
// id);
// return new ModelAndView(REDIRECT_LOGIN);
// }
// }

// @GetMapping("/list")
// public String listCustomers(Model model,
// @RequestParam(defaultValue = "0") int page,
// @RequestParam(defaultValue = "10") int size,
// @RequestParam(defaultValue = "name") String sortBy,
// @RequestParam(defaultValue = "asc") String sortDir) {

// try {
// Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
// Sort.Direction.DESC : Sort.Direction.ASC;
// PageRequest pageable = PageRequest.of(page, size, Sort.by(direction,
// sortBy));

// Page<Customer> customersPage = customerService.findAll(pageable);

// model.addAttribute("customersPage", customersPage);
// model.addAttribute("customers", customersPage.getContent());
// model.addAttribute("currentPage", page);
// model.addAttribute("totalPages", customersPage.getTotalPages());
// model.addAttribute("sortBy", sortBy);
// model.addAttribute("sortDir", sortDir);

// log.info("Lista de clientes carregada. Página: {}, Total: {}", page,
// customersPage.getTotalElements());
// return CUSTOMER_LIST_VIEW;

// } catch (Exception e) {
// log.error("Erro ao listar clientes", e);
// model.addAttribute("errorMessage", "Erro ao carregar lista de clientes");
// return CUSTOMER_LIST_VIEW;
// }
// }

// @PostMapping("/change-password")
// public String changePassword(
// @RequestParam String currentPassword,
// @RequestParam String newPassword,
// @RequestParam String confirmPassword,
// RedirectAttributes attr,
// HttpSession session) {

// Customer loggedCustomer = sessionUtil.getLoggedCustomer(session);
// if (loggedCustomer == null) {
// return REDIRECT_LOGIN;
// }

// try {
// if (!newPassword.equals(confirmPassword)) {
// attr.addFlashAttribute("errorMessage", "Nova senha e confirmação não
// coincidem");
// return "redirect:/customer/painel";
// }

// if (newPassword.length() < 6) {
// attr.addFlashAttribute("errorMessage", "Nova senha deve ter pelo menos 6
// caracteres");
// return "redirect:/customer/painel";
// }

// customerService.changePassword(loggedCustomer.getId(), currentPassword,
// newPassword);
// attr.addFlashAttribute("successMessage", "Senha alterada com sucesso!");
// log.info("Senha alterada para cliente ID: {}", loggedCustomer.getId());

// } catch (Exception e) {
// log.error("Erro ao alterar senha do cliente ID: {}", loggedCustomer.getId(),
// e);
// attr.addFlashAttribute("errorMessage", "Erro ao alterar senha: " +
// e.getMessage());
// }

// return "redirect:/customer/painel";
// }
// @PostMapping("/filter")
// public String applyFilters(@ModelAttribute CustomerFilterDTO filterDTO,
// HttpSession session) {
// session.setAttribute(FILTER_CATEGORIES_ATTR, filterDTO.getAccountTypes());
// session.setAttribute(FILTER_ORDER_ATTR, filterDTO.getSortOrder());

// log.info("Filtros aplicados: tipos={}, ordem={}",
// filterDTO.getAccountTypes(), filterDTO.getSortOrder());
// return "redirect:/customer/painel";
// }

// @ExceptionHandler(CustomerNotFoundException.class)
// public ModelAndView handleCustomerNotFound(CustomerNotFoundException e) {
// log.error("Cliente não encontrado", e);
// ModelAndView mav = new ModelAndView("redirect:/customer/painel");
// mav.addObject("errorMessage", "Cliente não encontrado");
// return mav;
// }

// @ExceptionHandler(UnauthorizedAccessException.class)
// public ModelAndView handleUnauthorizedAccess(UnauthorizedAccessException e) {
// log.error("Acesso não autorizado", e);
// ModelAndView mav = new ModelAndView(REDIRECT_LOGIN);
// mav.addObject("errorMessage", "Acesso não autorizado");
// return mav;
// }

// @ExceptionHandler(Exception.class)
// public ModelAndView handleGenericException(Exception e) {
// log.error("Erro inesperado no controller", e);
// ModelAndView mav = new ModelAndView("error");
// mav.addObject("errorMessage", "Ocorreu um erro inesperado");
// return mav;
// }

// private Sort buildSortFromSession(HttpSession session) {
// String order = (String) session.getAttribute(FILTER_ORDER_ATTR);
// if (order == null) {
// order = DEFAULT_SORT_ORDER;
// }

// Sort.Direction direction = DEFAULT_SORT_ORDER.equalsIgnoreCase(order) ?
// Sort.Direction.ASC : Sort.Direction.DESC;

// return Sort.by(direction, "expirationDate");
// }

// @SuppressWarnings("unchecked")
// private List<AccountTypes> getAccountTypesFromSession(HttpSession session) {
// Object attr = session.getAttribute(FILTER_CATEGORIES_ATTR);
// List<AccountTypes> types = new ArrayList<>();

// if (attr instanceof List<?>) {
// List<?> rawList = (List<?>) attr;
// for (Object item : rawList) {
// if (item instanceof AccountTypes) {
// types.add((AccountTypes) item);
// } else {
// log.warn("Item inválido encontrado na lista de filtros: {}", item);
// }
// }
// }

// return types;
// }

// private Page<Account> getFilteredAccounts(Long customerId, List<AccountTypes>
// types, PageRequest pageable) {
// if (types != null && !types.isEmpty()) {
// return accountService.findByCustomerIdAndAccountTypeIn(customerId, types,
// pageable);
// } else {
// return accountService.findByCustomerId(customerId, pageable);
// }
// }

// private void validateCustomerAccess(Customer loggedCustomer, Long
// targetCustomerId) {
// if (loggedCustomer == null) {
// throw new UnauthorizedAccessException("Cliente não está logado");
// }

// if (!loggedCustomer.getId().equals(targetCustomerId) &&
// !loggedCustomer.isAdmin()) {
// throw new UnauthorizedAccessException("Acesso não autorizado ao cliente");
// }
// }

// private CustomerDTO convertToDTO(Customer customer) {
// CustomerDTO dto = new CustomerDTO();
// dto.setId(customer.getId());
// dto.setName(customer.getName());
// dto.setEmail(customer.getEmail());
// dto.setActive(customer.getActive());

// // Contar total de contas (pode ser otimizado com query específica)
// List<Account> accounts = accountService.findByCustomerId(customer.getId(),
// Sort.unsorted());
// dto.setTotalAccounts(accounts.size());

// return dto;
// }

// }
