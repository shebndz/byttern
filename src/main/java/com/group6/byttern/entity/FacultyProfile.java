package com.group6.byttern.entity;

import java.beans.Transient;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

// Connects the FacultyProfile entity to the database and defines the table structure
@Entity
@Table(name ="faculty_profile")
public class FacultyProfile {
    @Id
    private int userAccountId;
    @OneToOne
    @JoinColumn(name = "user_account_id")
    @MapsId
    private Users userId;
    private String firstName;
    private String lastName;
    private String department;
    private String designation;

    @Version
    private Integer version;

    @OneToMany(mappedBy = "faculty")
    private List<JobSeekerProfile> students;


    @Column(nullable = true, length = 64)
    private String profilePhoto;

    //Constructor
    public FacultyProfile() {
    }
    //Getters and Setters
    public FacultyProfile(int userAccountId, Users userId, String firstName, String lastName, String department, String designation,
    String profilePhoto) {
        this.userAccountId = userAccountId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.designation = designation;
        this.profilePhoto = profilePhoto;
    }
    public Integer getVersion() {
        return version;
    }
    public void setVersion(Integer version) {
        this.version = version;
    }
    public List<JobSeekerProfile> getStudents() {
        return students;
    }
    public void setStudents(List<JobSeekerProfile> students) {
        this.students = students;
    }
    public FacultyProfile(Users users){
        this.userId = users;
    }
    public int getUserAccountId() {
        return userAccountId;
    }
    public void setUserAccountId(int userAccountId) {
        this.userAccountId = userAccountId;
    }
    public Users getUserId() {
        return userId;
    }
    public void setUserId(Users userId) {
        this.userId = userId;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public String getDesignation() {
        return designation;
    }
    public void setDesignation(String designation) {
        this.designation = designation;
    }
    public String getProfilePhoto() {
        return profilePhoto;
    }
    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
    @Transient
    public String getProfilePhotoPath() {
        if (profilePhoto == null || userAccountId == 0) return null;
        return "/photos/faculty/" + userAccountId + "/" + profilePhoto;
    }
    @Override
    public String toString() {
        return "FacultyProfile{" +
                "userAccountId=" + userAccountId +
                ", userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", department='" + department + '\'' +
                ", designation='" + designation + '\'' +
                ", profilePhoto='" + profilePhoto + '\'' +
                '}';
    }

}
