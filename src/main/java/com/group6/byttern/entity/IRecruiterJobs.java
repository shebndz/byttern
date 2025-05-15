package com.group6.byttern.entity;

//  Interface is used to define the structure of the RecruiterJobs entity
public interface IRecruiterJobs {

    Long getTotalCandidates();

    int getJob_post_id();

    String getJob_title();

    int getLocationId();

    String getCity();

    String getProvince();

    String getCountry();

    int getCompanyId();

    String getName();
}