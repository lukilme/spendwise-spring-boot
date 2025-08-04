package com.ifpb.edu.spendwise.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/metrics")
public class MetricController {
    @GetMapping
    public void index(HttpSession session) {
        System.out.println("useless");
    }
}
