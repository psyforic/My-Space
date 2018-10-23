package com.metrorez.myspace.user.model;

import java.io.Serializable;

public class UserAdmin implements Serializable {
    private String adminId;
    private String adminName;
    private String adminLastName;
    private String adminEmail;
    private String adminRole;

    public UserAdmin() {
    }

    public UserAdmin(String adminId, String adminName, String adminLastName, String adminEmail, String adminRole) {
        this.adminId = adminId;
        this.adminName = adminName;
        this.adminLastName = adminLastName;
        this.adminEmail = adminEmail;
        this.adminRole = adminRole;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public String getAdminLastName() {
        return adminLastName;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public String getAdminRole() {
        return adminRole;
    }
}
