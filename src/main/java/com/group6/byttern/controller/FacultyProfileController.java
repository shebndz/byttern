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

import com.group6.byttern.entity.FacultyProfile;
import com.group6.byttern.entity.Users;
import com.group6.byttern.repository.UsersRepository;
import com.group6.byttern.services.FacultyProfileService;
import com.group6.byttern.util.FileUploadUtil;

// Handles the faculty profile management
@Controller
@RequestMapping("/faculty-profile")
public class FacultyProfileController {

    private final FacultyProfileService facultyProfileService;
    private final UsersRepository usersRepository;
    private final FacultyProfileService FacultyProfileService;
    // Constrcutor
    public FacultyProfileController(UsersRepository usersRepository, FacultyProfileService facultyProfileService, FacultyProfileService FacultyProfileService) {
        this.usersRepository = usersRepository;
        this.facultyProfileService = facultyProfileService;
        this.FacultyProfileService = FacultyProfileService;
    }

// Handles the GET request to view the faculty profile
@GetMapping("/")
public String FacultyProfile(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof AnonymousAuthenticationToken)) {
        Users user = usersRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
        Optional<FacultyProfile> facultyProfileOpt = FacultyProfileService.getOne(user.getUserId());

        FacultyProfile profileToDisplay = facultyProfileOpt.orElseGet(() -> {
            FacultyProfile newProfile = new FacultyProfile();
            newProfile.setUserId(user);  // Optional: preload
            newProfile.setUserAccountId(user.getUserId());
            return newProfile;
        });

        model.addAttribute("profile", profileToDisplay);
    }

    return "faculty-profile";
}

// Handles the POST request to add or update the faculty profile    
@PostMapping("/addNew")
public String addNew(FacultyProfile facultyProfile, @RequestParam("image") MultipartFile multipartFile, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUsername = authentication.getName();
        Users users = usersRepository.findByEmail(currentUsername)
            .orElseThrow(() -> new UsernameNotFoundException("Could not found user"));

        // Check if profile exists
        Optional<FacultyProfile> existingProfileOpt = facultyProfileService.getOne(users.getUserId());
        FacultyProfile profileToSave;
        if (existingProfileOpt.isPresent()) {
            profileToSave = existingProfileOpt.get();
            // Update existing profile
            profileToSave.setFirstName(facultyProfile.getFirstName());
            profileToSave.setLastName(facultyProfile.getLastName());
            profileToSave.setDepartment(facultyProfile.getDepartment());
            profileToSave.setDesignation(facultyProfile.getDesignation());
        } else {
            profileToSave = facultyProfile;
            profileToSave.setUserId(users);
            profileToSave.setUserAccountId(users.getUserId());
        }
        //File upload
        String fileName = "";
        if (!multipartFile.getOriginalFilename().equals("")) {
            fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            profileToSave.setProfilePhoto(fileName);
        }
        FacultyProfile savedUser = facultyProfileService.addNew(profileToSave);

        String uploadDir = "photos/faculty/" + savedUser.getUserAccountId();
        try {
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    return "redirect:/dashboard/";
}

}

