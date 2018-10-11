package com.metrorez.myspace.model;

public class User {
    private String userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userStudentNo;

    public User() {
    }

    public User(String userId, String userFirstName, String userLastName, String userEmail, String userStudentNo) {
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userEmail = userEmail;
        this.userStudentNo = userStudentNo;
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
}
