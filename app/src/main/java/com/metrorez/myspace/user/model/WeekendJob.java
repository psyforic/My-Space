package com.metrorez.myspace.user.model;

import java.io.Serializable;

public class WeekendJob implements Serializable{
    private String userId;
    private String residence;
    private String gender;
    private String city;
    private String jobDescription;
    private String date;

    public WeekendJob() {
    }

    public WeekendJob(String userId, String residence, String gender, String city, String jobDescription) {
        this.userId = userId;
        this.residence = residence;
        this.gender = gender;
        this.city = city;
        this.jobDescription = jobDescription;
    }

    public WeekendJob(String userId, String residence, String gender, String city, String jobDescription, String date) {
        this.userId = userId;
        this.residence = residence;
        this.gender = gender;
        this.city = city;
        this.jobDescription = jobDescription;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getUserId() {
        return userId;
    }

    public String getResidence() {
        return residence;
    }

    public String getGender() {
        return gender;
    }

    public String getCity() {
        return city;
    }

    public String getJobDescription() {
        return jobDescription;
    }
}
