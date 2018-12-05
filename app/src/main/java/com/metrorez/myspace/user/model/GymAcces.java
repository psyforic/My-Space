package com.metrorez.myspace.user.model;

import java.io.Serializable;

public class GymAcces implements Serializable{
    private String userId;
    private String userGender;
    private String Branch;
    private String accessId;
    private String contactEmail;
    private String city;

    public GymAcces() {
    }

    public GymAcces(String userId, String userGender, String branch, String accessId, String contactEmail, String city) {
        this.userId = userId;
        this.userGender = userGender;
        Branch = branch;
        this.accessId = accessId;
        this.contactEmail = contactEmail;
        this.city = city;
    }

    public String getUserId() {
        return userId;
    }

    public String getBranch() {
        return Branch;
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
}
