package com.metrorez.myspace.admin.model;

import java.io.Serializable;

public class Image implements Serializable {
    private String imageUrl;

    public Image() {
    }

    public Image(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
