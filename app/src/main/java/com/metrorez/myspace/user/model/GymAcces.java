package com.metrorez.myspace.user.model;

import java.io.Serializable;

public class GymAcces implements Serializable {
    private String userId;
    private String userGender;
    private String branch;
    private String accessId;
    private String contactEmail;
    private String city;
    private String date;
    private String userName;

    public GymAcces() {
    }

    public GymAcces(String userId, String userGender, String branch, String accessId, String contactEmail, String city, String date, String userName) {
        this.userId = userId;
        this.userGender = userGender;
        this.branch = branch;
        this.accessId = accessId;
        this.contactEmail = contactEmail;
        this.city = city;
        this.date = date;
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getBranch() {
        return branch;
    }

    public String getAccessId() {
        return accessId;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getCity() {
        return city;
    }

    public String getUserGender() {
        return userGender;
    }

    public String getDate() {
        return date;
    }

    public String getUserName() {
        return userName;
    }
}
