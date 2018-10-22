package com.metrorez.myspace.user.model;

public class Role {
    private String userId;
    private String UserRole;

    public Role() {
    }

    public Role(String userId, String userRole) {
        this.userId = userId;
        UserRole = userRole;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserRole() {
        return UserRole;
    }
}
