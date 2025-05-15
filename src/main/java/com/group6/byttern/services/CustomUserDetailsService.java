package com.group6.byttern.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.group6.byttern.entity.Users;
import com.group6.byttern.repository.UsersRepository;
import com.group6.byttern.util.CustomUserDetails;

// CustomUserDetailsService is a service that implements UserDetailsService to load user-specific data
@Service
public class CustomUserDetailsService implements UserDetailsService{
    private final UsersRepository userRepository;
    @Autowired
    public CustomUserDetailsService(UsersRepository userRepository) {
        this.userRepository = userRepository;
        }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("Could not found User"));   // retrieve user from the database
        return new CustomUserDetails(user);
}
}
