package com.group6.byttern.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.group6.byttern.entity.FacultyProfile;
import com.group6.byttern.entity.JobSeekerApply;
import com.group6.byttern.entity.JobSeekerProfile;
import com.group6.byttern.entity.Users;
import com.group6.byttern.repository.JobSeekerApplyRepository;
import com.group6.byttern.repository.JobSeekerProfileRepository;
import com.group6.byttern.repository.UsersRepository;
import com.group6.byttern.services.FacultyProfileService;

@Controller
public class CourseFacultyController {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private FacultyProfileService facultyProfileService;
    @Autowired
    private JobSeekerApplyRepository jobSeekerApplyRepository;
    @Autowired
    private JobSeekerProfileRepository jobSeekerProfileRepository;
    // Handles the GET request to view students for a specific course
    @GetMapping("/courses-faculty")
    public String viewStudents(Model model, Principal principal) {
        Users user = usersRepository.findByEmail(principal.getName()).orElseThrow();
        FacultyProfile faculty = facultyProfileService.getOne(user.getUserId()).orElseThrow();
        List<JobSeekerProfile> students = jobSeekerProfileRepository.findByFacultyUserAccountId(faculty.getUserAccountId());

        Map<JobSeekerProfile, List<JobSeekerApply>> studentApplications = new HashMap<>();
        for (JobSeekerProfile student : students) {
            List<JobSeekerApply> applications = jobSeekerApplyRepository.findByUserId(student);
            studentApplications.put(student, applications);
        }

        model.addAttribute("studentApplications", studentApplications);
        model.addAttribute("profile", faculty);
        return "courses-faculty";
    }
}