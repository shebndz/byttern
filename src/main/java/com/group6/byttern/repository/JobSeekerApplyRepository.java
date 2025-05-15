package com.group6.byttern.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group6.byttern.entity.JobPostActivity;
import com.group6.byttern.entity.JobSeekerApply;
import com.group6.byttern.entity.JobSeekerProfile;

// The purpose of this repository is to manage the JobSeekerApply entity.
@Repository
public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Integer> {

    List<JobSeekerApply> findByUserId(JobSeekerProfile userId);

    List<JobSeekerApply> findByJob(JobPostActivity job);

    

}
