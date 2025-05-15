package com.group6.byttern.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.group6.byttern.entity.JobPostActivity;
import com.group6.byttern.entity.JobSeekerProfile;
import com.group6.byttern.entity.JobSeekerSave;
import com.group6.byttern.entity.Users;
import com.group6.byttern.services.JobPostActivityService;
import com.group6.byttern.services.JobSeekerProfileService;
import com.group6.byttern.services.JobSeekerSaveService;
import com.group6.byttern.services.UsersService;

// Handles the job seeker save functionality
@Controller
public class JobSeekerSaveController {

    private final UsersService usersService;
    private final JobSeekerProfileService jobSeekerProfileService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerSaveService jobSeekerSaveService;

    // Constructor
    public JobSeekerSaveController(UsersService usersService, JobSeekerProfileService jobSeekerProfileService, JobPostActivityService jobPostActivityService, JobSeekerSaveService jobSeekerSaveService) {
        this.usersService = usersService;
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerSaveService = jobSeekerSaveService;
    }

    // Handles the GET request to view job details and save functionality
    @PostMapping("job-details/save/{id}")
    public String save(@PathVariable("id") int jobId) {
    
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users user = usersService.findByEmail(currentUsername);
            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(user.getUserId());
            JobPostActivity jobPostActivity = jobPostActivityService.getOne(jobId);
    
            if (seekerProfile.isPresent() && jobPostActivity != null) {
                // Check if this save already exists to prevent duplicates
                boolean alreadySaved = jobSeekerSaveService.existsByUserAndJob(seekerProfile.get(), jobPostActivity);
    
                if (!alreadySaved) {
                    JobSeekerSave jobSeekerSave = new JobSeekerSave();
                    jobSeekerSave.setJob(jobPostActivity);
                    jobSeekerSave.setUserId(seekerProfile.get());
                    jobSeekerSaveService.addNew(jobSeekerSave);
                }
            } else {
                throw new RuntimeException("User or job post not found");
            }
        }
    
        return "redirect:/dashboard/";
    }
    
    // Handles the GET request to view saved jobs
    @GetMapping("saved-jobs/")
    public String savedJobs(Model model) {

        List<JobPostActivity> jobPost = new ArrayList<>();
        Object currentUserProfile = usersService.getCurrentUserProfile();

        List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getCandidatesJob((JobSeekerProfile) currentUserProfile);
        for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
            jobPost.add(jobSeekerSave.getJob());
        }

        model.addAttribute("jobPost", jobPost);
        model.addAttribute("user", currentUserProfile);

        return "saved-jobs";
    }
}

