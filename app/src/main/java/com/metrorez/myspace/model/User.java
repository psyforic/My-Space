package com.metrorez.myspace.model;

public class User {
    private String userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userStudentNo;
    private String userCity;
    private String userResidence;
    private String userRoom;

    public User() {
    }

    public User(String userId, String userFirstName, String userLastName, String userEmail, String userStudentNo) {
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userEmail = userEmail;
        this.userStudentNo = userStudentNo;
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
}
