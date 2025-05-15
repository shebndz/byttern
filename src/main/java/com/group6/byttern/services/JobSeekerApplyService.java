package com.group6.byttern.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.group6.byttern.entity.JobPostActivity;
import com.group6.byttern.entity.JobSeekerApply;
import com.group6.byttern.entity.JobSeekerProfile;
import com.group6.byttern.repository.JobSeekerApplyRepository;

// This class is responsible for managing job applications made by job seekers.
// It provides methods to retrieve job applications for a specific job seeker and job post,
@Service
public class JobSeekerApplyService {

    private final JobSeekerApplyRepository jobSeekerApplyRepository;

    
    public JobSeekerApplyService(JobSeekerApplyRepository jobSeekerApplyRepository) {
        this.jobSeekerApplyRepository = jobSeekerApplyRepository;
    }

    public List<JobSeekerApply> getCandidatesJobs(JobSeekerProfile userAccountId) {
        return jobSeekerApplyRepository.findByUserId(userAccountId);
    }

    public List<JobSeekerApply> getJobCandidates(JobPostActivity job) {
        return jobSeekerApplyRepository.findByJob(job);
    }

    public void addNew(JobSeekerApply jobSeekerApply) {
        jobSeekerApplyRepository.save(jobSeekerApply);
    }
    public Optional<JobSeekerApply> getOne(Integer id) {
        return jobSeekerApplyRepository.findById(id);
    }

    public void save(JobSeekerApply application) {
        jobSeekerApplyRepository.save(application);
    }
    

}