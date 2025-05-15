package com.group6.byttern.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.group6.byttern.entity.FacultyProfile;
import com.group6.byttern.entity.JobPostActivity;
import com.group6.byttern.entity.JobSeekerApply;
import com.group6.byttern.entity.JobSeekerProfile;
import com.group6.byttern.entity.JobSeekerSave;
import com.group6.byttern.entity.RecruiterJobsDto;
import com.group6.byttern.entity.RecruiterProfile;
import com.group6.byttern.entity.Users;
import com.group6.byttern.services.FacultyProfileService;
import com.group6.byttern.services.JobPostActivityService;
import com.group6.byttern.services.JobSeekerApplyService;
import com.group6.byttern.services.JobSeekerProfileService;
import com.group6.byttern.services.JobSeekerSaveService;
import com.group6.byttern.services.RecruiterProfileService;
import com.group6.byttern.services.UsersService;

// Handles the job post activity
@Controller
public class JobPostActivityController {
    @Autowired
    private final UsersService usersService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final JobSeekerSaveService jobSeekerSaveService;
    private final RecruiterProfileService recruiterProfileService;
    private final FacultyProfileService facultyProfileService;
    private final JobSeekerProfileService jobSeekerProfileService;

    // Constructor
    public JobPostActivityController(UsersService usersService, JobPostActivityService jobPostActivityService, JobSeekerApplyService jobSeekerApplyService, JobSeekerSaveService jobSeekerSaveService, RecruiterProfileService recruiterProfileService, FacultyProfileService facultyProfileService, JobSeekerProfileService jobSeekerProfileService) {
        this.usersService = usersService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobSeekerSaveService = jobSeekerSaveService;
        this.recruiterProfileService = recruiterProfileService;
        this.facultyProfileService = facultyProfileService;
        this.jobSeekerProfileService = jobSeekerProfileService;
    }
    // Handles the GET request to view the dashboard
    @GetMapping("/dashboard/")
    // RequestParam is used for the filter and search functionality
    public String searchJobs(Model model,
                            @RequestParam(value = "job", required = false) String job,
                            @RequestParam(value = "location", required = false) String location,

                            @RequestParam(value = "paid", required = false) String paid,
                            @RequestParam(value = "unpaid", required = false) String unpaid,

                            @RequestParam(value = "remoteOnly", required = false) String remoteOnly,
                            @RequestParam(value = "onSite", required = false) String onSite,
                            @RequestParam(value = "hybrid", required = false) String hybrid,

                            @RequestParam(value = "today", required = false) Boolean today,
                            @RequestParam(value = "days7", required = false) Boolean days7,
                            @RequestParam(value = "days30", required = false) Boolean days30) {

        model.addAttribute("paid", Objects.equals(paid, "Paid"));
        model.addAttribute("unpaid", Objects.equals(unpaid, "Unpaid"));
        model.addAttribute("remoteOnly", Objects.equals(remoteOnly, "Remote-Only"));
        model.addAttribute("onSite", Objects.equals(onSite, "On-Site"));
        model.addAttribute("hybrid", Objects.equals(hybrid, "Hybrid"));

        model.addAttribute("today", Boolean.TRUE.equals(today));
        model.addAttribute("days7", Boolean.TRUE.equals(days7));
        model.addAttribute("days30", Boolean.TRUE.equals(days30));

        model.addAttribute("job", job);
        model.addAttribute("location", location);

        LocalDate searchDate = null;
        if (Boolean.TRUE.equals(days30)) {
            searchDate = LocalDate.now().minusDays(30);
        } else if (Boolean.TRUE.equals(days7)) {
            searchDate = LocalDate.now().minusDays(7);
        } else if (Boolean.TRUE.equals(today)) {
            searchDate = LocalDate.now();
        }

        boolean noSearchFilters = !StringUtils.hasText(job)
                && !StringUtils.hasText(location)
                && paid == null && unpaid == null
                && remoteOnly == null && onSite == null && hybrid == null
                && !Boolean.TRUE.equals(today)
                && !Boolean.TRUE.equals(days7)
                && !Boolean.TRUE.equals(days30);

        List<JobPostActivity> jobPost;
        if (noSearchFilters) {
            jobPost = jobPostActivityService.getAll();
        } else {
            jobPost = jobPostActivityService.search(
                    job,
                    location,
                    Arrays.asList(paid, unpaid),
                    Arrays.asList(remoteOnly, onSite, hybrid),
                    searchDate
            );
        }
        // Add the job post activity to the model
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            model.addAttribute("username", currentUsername);

            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
                RecruiterProfile recruiter = recruiterProfileService.getCurrentRecruiterProfile();
                model.addAttribute("user", recruiter);
                List<RecruiterJobsDto> recruiterJobs = jobPostActivityService.getRecruiterJobs(recruiter.getUserAccountId());
                model.addAttribute("jobPost", recruiterJobs);

            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Faculty"))) {
                FacultyProfile faculty = facultyProfileService.getCurrentFacultyProfile();
                model.addAttribute("user", faculty);
                model.addAttribute("jobPost", jobPost);

            } else {
                JobSeekerProfile seeker = jobSeekerProfileService.getCurrentSeekerProfile();
                model.addAttribute("user", seeker);
                List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyService.getCandidatesJobs(seeker);
                List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getCandidatesJob(seeker);

                boolean exist;
                boolean saved;

                for (JobPostActivity jobActivity : jobPost) {
                    exist = false;
                    saved = false;
                    for (JobSeekerApply jobSeekerApply : jobSeekerApplyList) {
                        if (Objects.equals(jobActivity.getJobPostId(), jobSeekerApply.getJob().getJobPostId())) {
                            jobActivity.setIsActive(true);
                            exist = true;
                            break;
                        }
                    }

                    for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
                        if (Objects.equals(jobActivity.getJobPostId(), jobSeekerSave.getJob().getJobPostId())) {
                            jobActivity.setIsSaved(true);
                            saved = true;
                            break;
                        }
                    }

                    if (!exist) {
                        jobActivity.setIsActive(false);
                    }
                    if (!saved) {
                        jobActivity.setIsSaved(false);
                    }
                }

                model.addAttribute("jobPost", jobPost);
            }
        } else {
            model.addAttribute("jobPost", jobPost);
        }

        return "dashboard";
    }

    // Handles the GET request to view the job post activity
    @GetMapping("/dashboard/add")
    public String addJobs(Model model) {
        model.addAttribute("jobPostActivity", new JobPostActivity());
        model.addAttribute("user", usersService.getCurrentUserProfile());
        return "add-jobs";
    }
    // Handles the POST request to add a new job post activity
    @PostMapping("/dashboard/addNew")
    public String addNew(JobPostActivity jobPostActivity, Model model) {

        Users user = usersService.getCurrentUser();
        if (user != null) {
            jobPostActivity.setPostedById(user);
        }
        jobPostActivity.setPostedDate(new Date());
        model.addAttribute("jobPostActivity", jobPostActivity);
        JobPostActivity saved = jobPostActivityService.addNew(jobPostActivity);
        return "redirect:/dashboard/";
    }
    // Handles the GET request to edit a job post activity
    @PostMapping("/dashboard/edit/{id}")
    public String editJob(@PathVariable("id") int id, Model model) {

        JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
        model.addAttribute("jobPostActivity", jobPostActivity);
        model.addAttribute("user", usersService.getCurrentUserProfile());
        return "add-jobs";
    }
    // Handles the POST request to update a job post activity
    @PostMapping("/dashboard/deleteJob/{id}")
    public String deleteJob(@PathVariable("id") int id) {
        try {
            jobPostActivityService.delete(id);
        } catch (ObjectOptimisticLockingFailureException e) {
            return "redirect:/error"; // Redirect to an error page
        }
        return "redirect:/dashboard/";
    }
}






