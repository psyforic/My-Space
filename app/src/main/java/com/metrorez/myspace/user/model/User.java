package com.metrorez.myspace.user.model;

import java.io.Serializable;

public class User implements Serializable{
    private String userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userStudentNo;
    private String userCity;
    private String userResidence;
    private String userRoom;
    private String userCellphone;
    private int photo;

    public User() {
    }

    public User(String userId, String userFirstName, String userLastName, String userEmail, String userStudentNo, String userCity) {
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userEmail = userEmail;
        this.userStudentNo = userStudentNo;
        this.userCity = userCity;
    }

    public User(String userId, String userFirstName, String userLastName, String userEmail, String userStudentNo, String userCity, String userResidence, String userRoom) {
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userEmail = userEmail;
        this.userStudentNo = userStudentNo;
        this.userCity = userCity;
        this.userResidence = userResidence;
        this.userRoom = userRoom;
    }

    public User(String userId, String userFirstName, String userLastName, String userEmail, String userStudentNo, String userCity, String userResidence, String userRoom, int photo) {
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userEmail = userEmail;
        this.userStudentNo = userStudentNo;
        this.userCity = userCity;
        this.userResidence = userResidence;
        this.userRoom = userRoom;
        this.photo = photo;
    }

    public User(String userFirstName, String userLastName, String userEmail, String userStudentNo, String userCity, String userResidence, String userRoom, int photo) {
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userEmail = userEmail;
        this.userStudentNo = userStudentNo;
        this.userCity = userCity;
        this.userResidence = userResidence;
        this.userRoom = userRoom;
        this.photo = photo;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserStudentNo() {
        return userStudentNo;
    }

    public String getUserCity() {
        return userCity;
    }

    public String getUserResidence() {
        return userResidence;
    }

    public String getUserRoom() {
        return userRoom;
    }

    public int getPhoto() {
        return photo;
    }

    public String getUserCellphone() {
        return userCellphone;
    }
}
