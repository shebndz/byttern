package com.group6.byttern.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Handles the home page
@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
}
