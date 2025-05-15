package com.group6.byttern.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    // This method is called when authentication is successful
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        System.out.println("The username " + username + " is logged in.");
        boolean hasJobSeekerRole = authentication.getAuthorities().stream().anyMatch(r->r.getAuthority().equals("Student"));
        boolean hasRecruiterRole = authentication.getAuthorities().stream().anyMatch(r->r.getAuthority().equals("Recruiter"));
        boolean hasFacultyRole = authentication.getAuthorities().stream().anyMatch(r->r.getAuthority().equals("Faculty"));


        if (hasRecruiterRole || hasJobSeekerRole || hasFacultyRole) {
            response.sendRedirect("/dashboard/");
        }
    }
}