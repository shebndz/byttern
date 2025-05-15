package com.group6.byttern.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group6.byttern.entity.FacultyProfile;
import com.group6.byttern.entity.JobSeekerProfile;

public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile, Integer>{

List<JobSeekerProfile> findByFacultyUserAccountId(Integer facultyUserAccountId);


    
    
}
