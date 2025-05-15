package com.group6.byttern.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group6.byttern.entity.JobPostActivity;
import com.group6.byttern.entity.JobSeekerProfile;
import com.group6.byttern.entity.JobSeekerSave;

@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave, Integer> {
    boolean existsByUserIdAndJob(JobSeekerProfile userId, JobPostActivity job);

    public List<JobSeekerSave> findByUserId(JobSeekerProfile userAccountId);

    List<JobSeekerSave> findByJob(JobPostActivity job);


        
    

}