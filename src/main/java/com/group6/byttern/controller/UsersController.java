package com.group6.byttern.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.group6.byttern.entity.Users;
import com.group6.byttern.entity.UsersType;
import com.group6.byttern.services.UsersService;
import com.group6.byttern.services.UsersTypeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

// Handles user registration and login
@Controller
public class UsersController {
    @Autowired
    private final UsersTypeService usersTypeService;
    private final UsersService usersService;

    // Constructor
    public UsersController(UsersTypeService usersTypeService, UsersService usersService) {
        this.usersTypeService = usersTypeService;
        this.usersService = usersService;
    }

    // Handles the GET request to view the registration page
    @GetMapping("/register")
    public String register(Model model) {
        List<UsersType> usersTypes = usersTypeService.getAll();
        model.addAttribute("getAllTypes", usersTypes);
        model.addAttribute("user", new Users());
        return "register";
    }

    // Handles the POST request for user registration
    @PostMapping("/register/new")
    public String userRegistration(@Valid Users users, Model model) {
        Optional<Users> optionalUsers = usersService.getUserByEmail(users.getEmail());
        if (optionalUsers.isPresent()) {
            model.addAttribute("error", "Email already registered,try to login or register with other email.");
            List<UsersType> usersTypes = usersTypeService.getAll();
            model.addAttribute("getAllTypes", usersTypes);
            model.addAttribute("user", new Users());
            return "register";
        }
        usersService.addNew(users);
        return "redirect:/dashboard/";
    }

    // Handles the GET request to view the login page
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Handles the GET request to logout
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return "redirect:/";
    }
}