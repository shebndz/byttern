package com.group6.byttern.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.group6.byttern.entity.JobPostActivity;
import com.group6.byttern.entity.JobSeekerProfile;
import com.group6.byttern.entity.JobSeekerSave;
import com.group6.byttern.repository.JobSeekerSaveRepository;

// This class is responsible for managing job saves made by job seekers.
@Service
public class JobSeekerSaveService {

    private final JobSeekerSaveRepository jobSeekerSaveRepository;

    public JobSeekerSaveService(JobSeekerSaveRepository jobSeekerSaveRepository) {
        this.jobSeekerSaveRepository = jobSeekerSaveRepository;
    }

    public List<JobSeekerSave> getCandidatesJob(JobSeekerProfile userAccountId) {
        return jobSeekerSaveRepository.findByUserId(userAccountId);
    }

    public List<JobSeekerSave> getJobCandidates(JobPostActivity job) {
        return jobSeekerSaveRepository.findByJob(job);
    }

    public void addNew(JobSeekerSave jobSeekerSave) {
        jobSeekerSaveRepository.save(jobSeekerSave);
    }

    public boolean existsByUserAndJob(JobSeekerProfile user, JobPostActivity job) {
        return jobSeekerSaveRepository.existsByUserIdAndJob(user, job);
    }
    
}