package com.group6.byttern.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.group6.byttern.entity.FacultyProfile;
import com.group6.byttern.entity.Users;
import com.group6.byttern.repository.FacultyProfileRepository;
import com.group6.byttern.repository.UsersRepository;

// FacultyProfileService is a service class that handles the business logic for managing faculty profiles
@Service
public class FacultyProfileService {

    private final FacultyProfileRepository facultyProfileRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public FacultyProfileService(FacultyProfileRepository facultyProfileRepository, UsersRepository usersRepository) {
        this.facultyProfileRepository = facultyProfileRepository;
        this.usersRepository = usersRepository;
    }

    public Optional<FacultyProfile> getOne(Integer id) {
        return facultyProfileRepository.findById(id);
    }

    public FacultyProfile addNew(FacultyProfile facultyProfile) {
        return facultyProfileRepository.save(facultyProfile);
    }

    public FacultyProfile getCurrentFacultyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users users = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            Optional<FacultyProfile> facultyProfile = getOne(users.getUserId());
            return facultyProfile.orElse(null);
        } else return null;
    }

    public List<FacultyProfile> getAll() {
        return facultyProfileRepository.findAll();
    }
}