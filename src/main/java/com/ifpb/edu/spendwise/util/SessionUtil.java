package com.ifpb.edu.spendwise.util;

import org.springframework.stereotype.Component;
import com.ifpb.edu.spendwise.model.Customer;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SessionUtil {
    

    private static final String LAST_ACCESS_TIME = "lastAccessTime";
    

    public Customer getLoggedCustomer(HttpSession session) {
        if (session == null) {
            return null;
        }
        try {
            Object customerObj = session.getAttribute("customer");
            if (customerObj instanceof Customer) {
                updateLastAccess(session);
                return (Customer) customerObj;
            }
        } catch (Exception e) {
            log.warn("Erro ao recuperar cliente da sessão", e);
        }
        
        return null;
    }
    
    public void setLoggedCustomer(HttpSession session, Customer customer) {
        if (session != null && customer != null) {
            session.setAttribute("customer", customer);
            updateLastAccess(session);
            log.debug("Cliente definido na sessão. ID: {}", customer.getId());
        }
    }
    
    public void removeLoggedCustomer(HttpSession session) {
        if (session != null) {
            session.removeAttribute("customer");
            session.removeAttribute(LAST_ACCESS_TIME);
            log.debug("Cliente removido da sessão");
        }
    }
    
    public boolean isCustomerLoggedIn(HttpSession session) {
        return getLoggedCustomer(session) != null;
    }
    
    public void invalidateSession(HttpSession session) {
        if (session != null) {
            try {
                session.invalidate();
                log.debug("Sessão invalidada");
            } catch (IllegalStateException e) {
                log.warn("Tentativa de invalidar sessão já invalidada", e);
            }
        }
    }
    
    private void updateLastAccess(HttpSession session) {
        session.setAttribute(LAST_ACCESS_TIME, System.currentTimeMillis());
    }
    
    public boolean isSessionExpired(HttpSession session, long timeoutMillis) {
        if (session == null) {
            return true;
        }
        
        Long lastAccess = (Long) session.getAttribute(LAST_ACCESS_TIME);
        if (lastAccess == null) {
            return true;
        }
        
        return (System.currentTimeMillis() - lastAccess) > timeoutMillis;
    }
}
