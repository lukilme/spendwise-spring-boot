package com.ifpb.edu.spendwise.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.ifpb.edu.spendwise.model.Customer;
import com.ifpb.edu.spendwise.model.enumerator.UserRoles;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@SuppressWarnings("null")
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);

        if (session != null) {
            Customer customer = (Customer) session.getAttribute("customer");

            if (customer != null) {
                String contextPath = request.getContextPath();
                String path = request.getRequestURI();
                String relativePath = path.substring(contextPath.length());

                boolean requerAdmin = relativePath.startsWith("/customer") || relativePath.startsWith("/account");

                if (requerAdmin && customer.getRole() != UserRoles.ROLE_ADMINISTRATOR) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado.");
                    return false;
                }
                return true;
            }
        }

        String loginUrl = request.getContextPath() + "/customer/login";
        response.sendRedirect(response.encodeRedirectURL(loginUrl));
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView model) throws Exception {
        if (model != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                
                Customer customer = (Customer) session.getAttribute("customer");
                if (customer != null) {
                    model.addObject("customer",customer);
                    if ("ADMINISTRATOR".equals(customer.getRole().toString())) {
                        model.addObject("isAdministrator", true);
                    }
                }
            }

            model.addObject("currentUri", request.getRequestURI());
        }
    }

}
