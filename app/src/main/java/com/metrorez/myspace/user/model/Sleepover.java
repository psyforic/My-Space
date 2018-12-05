package com.metrorez.myspace.user.model;

import java.io.Serializable;

public class Sleepover implements Serializable{
    private String roomNo;
    private String userId;
    private String friendName;
    private String friendLastName;
    private String friendGender;
    private String residenceName;
    private String city;

    public Sleepover() {
    }

    public Sleepover(String roomNo, String userId, String friendName, String friendLastName, String friendGender, String residenceName, String city) {
        this.roomNo = roomNo;
        this.userId = userId;
        this.friendName = friendName;
        this.friendLastName = friendLastName;
        this.friendGender = friendGender;
        this.residenceName = residenceName;
        this.city = city;
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
