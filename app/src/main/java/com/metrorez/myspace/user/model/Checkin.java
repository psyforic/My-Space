package com.metrorez.myspace.user.model;

import java.io.Serializable;

public class Checkin implements Serializable {

    private String id;
    private String date;
    private String snippet;
    private String url;
    private String city;

    public Checkin() {
    }

    public Checkin(String id, String date, String snippet, String url) {
        this.id = id;
        this.date = date;
        this.snippet = snippet;
        this.url = url;
    }

    public Checkin(String id, String date, String snippet, String url, String city) {
        this.id = id;
        this.date = date;
        this.snippet = snippet;
        this.url = url;
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getPhoto() {
        return url;
    }


    public String getCity() {
        return city;
    }

    public String getUrl() {
        return url;
    }
}
