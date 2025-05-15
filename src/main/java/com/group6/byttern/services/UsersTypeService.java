package com.group6.byttern.services;



import java.util.List;

import org.springframework.stereotype.Service;

import com.group6.byttern.entity.UsersType;
import com.group6.byttern.repository.UsersTypeRepository;

// This class is responsible for managing user types in the application.
// It provides methods to retrieve all user types from the database.
@Service
public class UsersTypeService {

    private final UsersTypeRepository usersTypeRepository;

    public UsersTypeService(UsersTypeRepository usersTypeRepository){
        this.usersTypeRepository = usersTypeRepository;
    }

    public List<UsersType> getAll() {
        return usersTypeRepository.findAll();
    }
    
}
