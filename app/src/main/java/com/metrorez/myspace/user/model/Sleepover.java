package com.metrorez.myspace.user.model;

import java.io.Serializable;

public class Sleepover implements Serializable {
    private String roomNo;
    private String userId;
    private String friendName;
    private String friendLastName;
    private String friendGender;
    private String residenceName;
    private String city;
    private String requestDate;

    public Sleepover() {
    }

    public Sleepover(String roomNo, String userId, String friendName, String friendLastName, String friendGender, String residenceName, String city, String requestDate) {
        this.roomNo = roomNo;
        this.userId = userId;
        this.friendName = friendName;
        this.friendLastName = friendLastName;
        this.friendGender = friendGender;
        this.residenceName = residenceName;
        this.city = city;
        this.requestDate = requestDate;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public String getUserId() {
        return userId;
    }

    public String getFriendName() {
        return friendName;
    }

    public String getFriendLastName() {
        return friendLastName;
    }

    public String getFriendGender() {
        return friendGender;
    }

    public String getResidenceName() {
        return residenceName;
    }

    public String getCity() {
        return city;
    }
}
