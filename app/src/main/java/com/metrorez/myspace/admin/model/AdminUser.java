package com.metrorez.myspace.admin.model;

import java.io.Serializable;

public class AdminUser implements Serializable{

    private String adminUserId;
    private String adminUserName;
    private String admin_userRole;
    private String admin_userLastName;
    private String adminEmail;
    private String adminLocation;
    private String adminResidence;
    private int photo;

    public AdminUser() {
    }

    public AdminUser(String adminUserId, String adminUserName, String admin_userRole, String admin_userLastName, String adminEmail, String adminLocation, String adminResidence, int photo) {
        this.adminUserId = adminUserId;
        this.adminUserName = adminUserName;
        this.admin_userRole = admin_userRole;
        this.admin_userLastName = admin_userLastName;
        this.adminEmail = adminEmail;
        this.adminLocation = adminLocation;
        this.adminResidence = adminResidence;
        this.photo = photo;
    }

    public AdminUser(String adminUserName, String admin_userRole, String admin_userLastName, String adminEmail, String adminLocation, String adminResidence, int photo) {
        this.adminUserName = adminUserName;
        this.admin_userRole = admin_userRole;
        this.admin_userLastName = admin_userLastName;
        this.adminEmail = adminEmail;
        this.adminLocation = adminLocation;
        this.adminResidence = adminResidence;
        this.photo = photo;
    }

    public String getAdmin_userLastName() {
        return admin_userLastName;
    }

    public String getAdminUserId() {
        return adminUserId;
    }

    public String getAdminUserName() {
        return adminUserName;
    }

    public String getAdmin_userRole() {
        return admin_userRole;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public String getAdminLocation() {
        return adminLocation;
    }

    public String getAdminResidence() {
        return adminResidence;
    }

    public int getPhoto() {
        return photo;
    }
}

