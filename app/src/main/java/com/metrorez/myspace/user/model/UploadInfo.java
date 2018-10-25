package com.metrorez.myspace.user.model;

import java.io.Serializable;

public class UploadInfo implements Serializable {

    private String name;
    private String url;
    private String userId;
    private String date;

    public UploadInfo() {
    }

    public UploadInfo(String name, String url, String userId, String date) {
        this.name = name;
        this.url = url;
        this.userId = userId;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getUserId() {
        return userId;
    }

    public String getDate() {
        return date;
    }
}
