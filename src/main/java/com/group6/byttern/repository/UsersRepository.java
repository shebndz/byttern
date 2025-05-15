package com.group6.byttern.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group6.byttern.entity.Users;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    
    Optional<Users> findByEmail(String email);
}
