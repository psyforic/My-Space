package com.metrorez.myspace.admin.model;

import com.metrorez.myspace.user.model.User;

import java.io.Serializable;

public class ResponseDetails implements Serializable {

    private long id;
    private String date;
    private User user;
    private String content;
    private boolean fromMe;
    private String imageUrl;

    public ResponseDetails(long id, String date, User user, String content, boolean fromMe) {
        this.id = id;
        this.date = date;
        this.user = user;
        this.content = content;
        this.fromMe = fromMe;
    }

    public ResponseDetails(long id, String date, User user, String content, String imageUrl, boolean fromMe) {
        this.id = id;
        this.date = date;
        this.user = user;
        this.content = content;
        this.fromMe = fromMe;
        this.imageUrl = imageUrl;
    }

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public User getuser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public boolean isFromMe() {
        return fromMe;
    }

    public User getUser() {
        return user;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
