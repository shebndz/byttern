package com.group6.byttern.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

//Connects the InternshipApplication entity to the database and defines the table structure
@Entity
@Table(name = "internship_application") // You can rename this table to "job_application" if needed
public class InternshipApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String status;
    private String coverLetter;
    private String resume;

    private LocalDate dateApplied;
    private LocalDate dateUpdated;
    private LocalDate dateAccepted;
    private LocalDate dateRejected;

    @ManyToOne
    @JoinColumn(name = "job_seeker_id", nullable = false)
    private JobSeekerProfile jobSeekerProfile;

    @ManyToOne
    @JoinColumn(name = "job_post_id", nullable = false) // Updated to reference JobPostActivity
    private JobPostActivity jobPostActivity; // Use JobPostActivity instead of RecruiterJobsDto

    // Default Constructor
    public InternshipApplication() {}

    // Constructor without ID and date fields
    public InternshipApplication(String status, String coverLetter, String resume, JobSeekerProfile jobSeekerProfile, JobPostActivity jobPostActivity) {
        this.status = status;
        this.coverLetter = coverLetter;
        this.resume = resume;
        this.jobSeekerProfile = jobSeekerProfile;
        this.jobPostActivity = jobPostActivity;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public LocalDate getDateApplied() {
        return dateApplied;
    }

    public void setDateApplied(LocalDate dateApplied) {
        this.dateApplied = dateApplied;
    }

    public LocalDate getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDate dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public LocalDate getDateAccepted() {
        return dateAccepted;
    }

    public void setDateAccepted(LocalDate dateAccepted) {
        this.dateAccepted = dateAccepted;
    }

    public LocalDate getDateRejected() {
        return dateRejected;
    }

    public void setDateRejected(LocalDate dateRejected) {
        this.dateRejected = dateRejected;
    }

    public JobSeekerProfile getJobSeekerProfile() {
        return jobSeekerProfile;
    }

    public void setJobSeekerProfile(JobSeekerProfile jobSeekerProfile) {
        this.jobSeekerProfile = jobSeekerProfile;
    }

    public JobPostActivity getJobPostActivity() {
        return jobPostActivity;
    }

    public void setJobPostActivity(JobPostActivity jobPostActivity) {
        this.jobPostActivity = jobPostActivity;
    }

    @Override
    public String toString() {
        return "InternshipApplication{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", coverLetter='" + coverLetter + '\'' +
                ", resume='" + resume + '\'' +
                ", dateApplied=" + dateApplied +
                ", dateUpdated=" + dateUpdated +
                ", dateAccepted=" + dateAccepted +
                ", dateRejected=" + dateRejected +
                ", jobSeekerProfileId=" + (jobSeekerProfile != null ? jobSeekerProfile.getUserId() : null) +
                ", jobPostActivityId=" + (jobPostActivity != null ? jobPostActivity.getJobPostId() : null) +
                '}';
    }
}