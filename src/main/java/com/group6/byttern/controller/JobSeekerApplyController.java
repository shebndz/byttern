package com.group6.byttern.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.group6.byttern.entity.JobPostActivity;
import com.group6.byttern.entity.JobSeekerApply;
import com.group6.byttern.entity.JobSeekerProfile;
import com.group6.byttern.entity.JobSeekerSave;
import com.group6.byttern.entity.RecruiterProfile;
import com.group6.byttern.entity.Users;
import com.group6.byttern.services.JobPostActivityService;
import com.group6.byttern.services.JobSeekerApplyService;
import com.group6.byttern.services.JobSeekerProfileService;
import com.group6.byttern.services.JobSeekerSaveService;
import com.group6.byttern.services.RecruiterProfileService;
import com.group6.byttern.services.UsersService;

// Handles the job application process
@Controller
public class JobSeekerApplyController {
    @Autowired
    private final JobPostActivityService jobPostActivityService;
    private final UsersService usersService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final JobSeekerSaveService jobSeekerSaveService;
    private final RecruiterProfileService recruiterProfileService;
    private final JobSeekerProfileService jobSeekerProfileService;

    // Constructor
    public JobSeekerApplyController(JobPostActivityService jobPostActivityService, UsersService usersService, JobSeekerApplyService jobSeekerApplyService, JobSeekerSaveService jobSeekerSaveService, RecruiterProfileService recruiterProfileService, JobSeekerProfileService jobSeekerProfileService) {
        this.jobPostActivityService = jobPostActivityService;
        this.usersService = usersService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobSeekerSaveService = jobSeekerSaveService;
        this.recruiterProfileService = recruiterProfileService;
        this.jobSeekerProfileService = jobSeekerProfileService;
    }
    // Handles the GET request to view job details and applications
    @GetMapping("job-details-apply/{id}")
    public String display(@PathVariable("id") int id, Model model) {
        JobPostActivity jobDetails = jobPostActivityService.getOne(id);
        List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyService.getJobCandidates(jobDetails);
        List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getJobCandidates(jobDetails);
        // Check if the user is authenticated and has the appropriate role
        // If the user is a recruiter and faculty, show the list of applicants
        // If the user is a job seeker, check if they have already applied or saved the job
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
                RecruiterProfile user = recruiterProfileService.getCurrentRecruiterProfile();
                if (user != null) {
                    model.addAttribute("applyList", jobSeekerApplyList);
                }
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Faculty"))) {
                model.addAttribute("applyList", jobSeekerApplyList);
            } else {
                JobSeekerProfile user = jobSeekerProfileService.getCurrentSeekerProfile();
                if (user != null) {
                    boolean exists = false;
                    boolean saved = false;
                    for (JobSeekerApply jobSeekerApply : jobSeekerApplyList) {
                        if (jobSeekerApply.getUserId().getUserAccountId().equals(user.getUserAccountId())) {
                            exists = true;
                            break;
                        }
                    }
                    for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
                        if (jobSeekerSave.getUserId().getUserAccountId().equals(user.getUserAccountId())) {
                            saved = true;
                            break;
                        }
                    }
                    model.addAttribute("alreadyApplied", exists);
                    model.addAttribute("alreadySaved", saved);
                }
            }
        }

        JobSeekerApply jobSeekerApply = new JobSeekerApply();
        List<JobSeekerApply> applyList = jobSeekerApplyService.getJobCandidates(jobDetails);
        model.addAttribute("applyJob", jobSeekerApply);
        model.addAttribute("applyList", applyList);
        model.addAttribute("jobDetails", jobDetails);
        model.addAttribute("user", usersService.getCurrentUserProfile());
        return "job-details";
    }
    // Handles the POST request to apply for a job
    @PostMapping("job-details/apply/{id}")
    public String apply(@PathVariable("id") int id, JobSeekerApply jobSeekerApply) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users user = usersService.findByEmail(currentUsername);
            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(user.getUserId());
            JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
            if (seekerProfile.isPresent() && jobPostActivity != null) {
                jobSeekerApply = new JobSeekerApply();
                jobSeekerApply.setUserId(seekerProfile.get());
                jobSeekerApply.setJob(jobPostActivity);
                jobSeekerApply.setApplyDate(new Date());
                jobSeekerApply.setStatus("Applied");
            } else {
                throw new RuntimeException("User not found");
            }
            jobSeekerApplyService.addNew(jobSeekerApply);
        }

        return "redirect:/dashboard/";
    }

    // Handles the GET request to view all applications for a specific job
    @PostMapping("/job-details/accept/{applyId}")
    public String acceptApplication(@PathVariable Integer applyId) {
        Optional<JobSeekerApply> optionalApplication = jobSeekerApplyService.getOne(applyId);
        if (optionalApplication.isPresent()) {
            JobSeekerApply application = optionalApplication.get();
            application.setStatus("Accepted");
            jobSeekerApplyService.save(application);
            return "redirect:/job-details-apply/" + application.getJob().getJobPostId();
        } else {
            // Handle not found (redirect or show error)
            return "redirect:/error";
        }
    }
}