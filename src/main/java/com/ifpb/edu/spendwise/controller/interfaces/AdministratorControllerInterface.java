package com.ifpb.edu.spendwise.controller.interfaces;

import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Interface that defines controller methods related to system administration.
 *
 * This interface should be implemented by classes responsible for handling
 * administrative actions, such as accessing the admin dashboard and managing customers.
 *
 * Methods:
 * - painelAdministrator(): Returns the view for the admin dashboard.
 * - customerEditor(): Returns the view for customer editing.
 */
public interface AdministratorControllerInterface {
    
    /**
     * Displays the administrator dashboard with relevant information and features.
     *
     * @param session the current HTTP session, used to retrieve or store user-related data.
     * @return ModelAndView representing the administrator dashboard view.
     */
    public ModelAndView painelAdministrator(HttpServletRequest request, HttpSession session);

    /**
     * Displays the customer editing interface for the administrator.
     *
     * @param model   the ModelAndView object used to render the view.
     * @param session the current HTTP session, used to retrieve or store user-related data.
     * @return ModelAndView representing the customer editor view.
     */
    public ModelAndView customerEditor(ModelAndView model, HttpSession session);
    
}
