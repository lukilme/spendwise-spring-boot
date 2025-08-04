// package com.ifpb.edu.spendwise.controller;


// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.servlet.ModelAndView;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// import com.ifpb.edu.spendwise.model.Customer;
// import com.ifpb.edu.spendwise.service.AuthService;

// import jakarta.servlet.http.HttpSession;

// @Controller
// @RequestMapping("/auth")
// public class AuthController {

//     @Autowired
//     private AuthService authService;

//     @GetMapping("/login")
//     public ModelAndView login(ModelAndView model, HttpSession session) {
//         Customer loggedCustomer = (Customer) session.getAttribute("customer");
//         if (loggedCustomer != null) {
//             return new ModelAndView("redirect:/home");
//         }
//         model.setViewName("auth/login");
//         model.addObject("customerLoginCredential", new Customer());
  
//         return model;
//     }

//     @PostMapping("/login")
//     public String validate(
//             @ModelAttribute Customer customer,
//             HttpSession session,
//             RedirectAttributes redirectAttributes) {

//         Customer authenticatedCustomer = authService.validateLogin(customer);
//         System.out.println(authenticatedCustomer);
//         if (authenticatedCustomer != null) {

//             session.setAttribute("customer", authenticatedCustomer);

//             session.setMaxInactiveInterval(5 * 60);

//             redirectAttributes.addFlashAttribute("message", "Login successful");
//             return "redirect:/home";
            
//         } else {
//             redirectAttributes.addFlashAttribute("error", "Some credentials are invalid");
//             return "redirect:/auth/login";
//         }
//     }

//     @GetMapping("/logout")
//     public ModelAndView logout(ModelAndView model, HttpSession session) {
//         session.invalidate();
//         return new ModelAndView("redirect:/auth/login");
//     }

//     public ModelAndView changeCredentialCustomer(ModelAndView model, HttpSession session){
//         return model;
//     }

//     // @PostMapping
//     // public ModelAndView validate( CustomerLoginDTO customerLogin,
//     // HttpSession session,
//     // ModelAndView model,
//     // RedirectAttributes redirectAtts
//     // ){

//     // CustomerResponseDTO customerLogged =
//     // this.authService.validateLogin(customerLogin);
//     // if ((customerLogged = this.credentialValidateLogin(customerLogin)) != null) {

//     // session.setAttribute("customer", customerLogged);
//     // model.setViewName("redirect:/home");
//     // } else {
//     // redirectAtts.addFlashAttribute("mensagem", "Login e/ou senha inválidos!");
//     // model.setViewName("redirect:/auth/login");
//     // }

//     // return model;
//     // }

//     // private CustomerResponseDTO credentialValidateLogin(CustomerLoginDTO
//     // customerLogin){
//     // Optional<Customer> customerTarget =
//     // repository.findByEmail(customerLogin.getEmail());
//     // boolean isValid = false;
//     // if(customerTarget.isPresent()){
//     // if(passwordEcorder.matches(new Customer(customerTarget),
//     // customerLogin.getPassword())){
//     // isValid = true;
//     // CustomerResponseDTO customerLogged = new CustomerResponseDTO(customerTarget);
//     // return customerLogged;
//     // }
//     // }
//     // return null;
//     // }
// }
