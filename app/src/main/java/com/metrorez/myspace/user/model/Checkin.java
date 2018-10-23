package com.metrorez.myspace.user.model;

import java.io.Serializable;

public class Checkin implements Serializable {

    private long id;
    private String date;
    private String snippet;
    private int photo;
    private String city;

    public Checkin() {
    }
    public Checkin(long id, String date, String snippet, int photo) {
        this.id = id;
        this.date = date;
        this.snippet = snippet;
        this.photo = photo;
    }

    public Checkin(long id, String date, String snippet, int photo, String city) {
        this.id = id;
        this.date = date;
        this.snippet = snippet;
        this.photo = photo;
        this.city = city;
    }

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getSnippet() {
        return snippet;
    }

    public int getPhoto() {
        return photo;
    }



    public String getCity() {
        return city;
    }
}