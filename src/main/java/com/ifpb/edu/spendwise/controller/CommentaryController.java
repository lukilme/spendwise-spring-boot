package com.ifpb.edu.spendwise.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ifpb.edu.spendwise.model.Commentary;
import com.ifpb.edu.spendwise.model.Customer;
import com.ifpb.edu.spendwise.service.AccountService;
import com.ifpb.edu.spendwise.service.CommentaryService;
import com.ifpb.edu.spendwise.util.Log;
import com.ifpb.edu.spendwise.util.SessionUtil;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/commentary")
public class CommentaryController {
    @Autowired
    AccountService accountService;

    @Autowired
    CommentaryService commentaryService;

    @Autowired
    SessionUtil sessionUtil;

    @GetMapping
    public String redirecionarPorRole(@RequestParam("id") Long transactionId, Model model, HttpSession session) {
        // Account account = accountService.findById(accountId).orElseThrow();
        // LoggerHandle.info(account.toString());
        Customer loggedCustomer = sessionUtil.getLoggedCustomer(session);
        List<Commentary> commentaries = commentaryService.listCommentsByTransaction(transactionId);
        //session.setAttribute("commentaries", commentaries);
        model.addAttribute("commentaries", commentaries);
        int count = 0;

        for (Commentary comnt : commentaries) {

            Log.infoIterator(count, comnt.toString());
            count++;
        }

        model.addAttribute("customer", loggedCustomer);
        model.addAttribute("transactionId", transactionId);
        return "commentaries/list";
    }

    @PostMapping
    public String createComment(
            @RequestParam("id") Long transactionId,
            @RequestParam("text") String text,
            Model model) {

        if (text == null || text.trim().isEmpty()) {
            model.addAttribute("error", "Comentário não pode ser vazio.");
            return "redirect:/commentary?id=" + transactionId;
        }

        commentaryService.addComment(transactionId, text);
        return "redirect:/commentary?id=" + transactionId;
    }

    @PostMapping("/delete/{commentId}")
    public String deleteComment(@PathVariable Long commentId, @RequestParam Long transactionId) {
        commentaryService.deleteComment(commentId);
        return "redirect:/commentary?id=" + transactionId;
    }

    @GetMapping("/{commentId}/edit")
    public String showEditForm(@PathVariable Long commentId, Model model, HttpSession session) {
        Commentary commentary = commentaryService.findById(commentId);

        Customer customer = sessionUtil.getLoggedCustomer(session);

        model.addAttribute("customer", customer);
        model.addAttribute("commentary", commentary);
        model.addAttribute("transactionId", commentary.getTransaction().getId());
        return "commentaries/edit"; 
    }

    @PostMapping("/{commentId}/edit")
    public String updateComment(
            @PathVariable Long commentId,
            @RequestParam("text") String newText,
            @RequestParam Long transactionId) {

        commentaryService.editComment(commentId, newText);
        return "redirect:/commentary?id=" + transactionId;
    }
}
