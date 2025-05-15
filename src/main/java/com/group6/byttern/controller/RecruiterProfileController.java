package com.group6.byttern.controller;

import java.util.Objects;
import java.util.Optional;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.group6.byttern.entity.RecruiterProfile;
import com.group6.byttern.entity.Users;
import com.group6.byttern.repository.UsersRepository;
import com.group6.byttern.services.RecruiterProfileService;
import com.group6.byttern.util.FileUploadUtil;

// Handles the recruiter profile management
@Controller
@RequestMapping("/recruiter-profile")
public class RecruiterProfileController {

    private final UsersRepository usersRepository;
    private final RecruiterProfileService recruiterProfileService;

    // Constructor
    public RecruiterProfileController(UsersRepository usersRepository, RecruiterProfileService recruiterProfileService) {
        this.usersRepository = usersRepository;
        this.recruiterProfileService = recruiterProfileService;
    }

// Handles the GET request to view the recruiter profile
@GetMapping("/")
public String recruiterProfile(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUsername = authentication.getName();
        Users users = usersRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Could not find user"));

        Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getOne(users.getUserId());

        // Check if the user has a profile
        RecruiterProfile profileToUse = recruiterProfile.orElseGet(() -> {
            RecruiterProfile newProfile = new RecruiterProfile();
            newProfile.setUserId(users);
            newProfile.setUserAccountId(users.getUserId());
            return newProfile;
        });

        model.addAttribute("profile", profileToUse); 
    }

    return "recruiter_profile";
}

    // Handles the POST request to add a new recruiter profile
    @PostMapping("/addNew")
    public String addNew(RecruiterProfile recruiterProfile, @RequestParam("image") MultipartFile multipartFile, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users users = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("Could not " + "found user"));
            recruiterProfile.setUserId(users);
            recruiterProfile.setUserAccountId(users.getUserId());
        }
        model.addAttribute("profile", recruiterProfile);
        String fileName = "";
        if (!multipartFile.getOriginalFilename().equals("")) {
            fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            recruiterProfile.setProfilePhoto(fileName);
        }
        RecruiterProfile savedUser = recruiterProfileService.addNew(recruiterProfile);

        String uploadDir = "photos/recruiter/" + savedUser.getUserAccountId();
        try {
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "redirect:/dashboard/";
    }
}







