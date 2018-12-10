package com.metrorez.myspace.user.model;

import java.io.Serializable;

public class Role implements Serializable {
    private String userId;
    private String userRole;

    public Role() {
    }

    public Role(String userId, String userRole) {
        this.userId = userId;
        this.userRole = userRole;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserRole() {
        return userRole;
    }
}
