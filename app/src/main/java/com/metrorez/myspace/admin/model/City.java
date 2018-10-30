package com.metrorez.myspace.admin.model;

import java.io.Serializable;

public class City implements Serializable {

    private String id;
    private String name;
    private String snippet;
    private int photo;

    public City() {
    }

    public City(String id, String name, String snippet, int photo) {
        this.id = id;
        this.name = name;
        this.snippet = snippet;
        this.photo = photo;
    }

    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public String getSnippet() {
        return snippet;
    }

    public int getPhoto() {
        return photo;
    }

}
