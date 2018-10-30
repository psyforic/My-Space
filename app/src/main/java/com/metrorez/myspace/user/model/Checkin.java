package com.metrorez.myspace.user.model;

import java.io.Serializable;
import java.util.List;

public class Checkin implements Serializable {

    private String id;
    private String date;
    private List<String> urls;
    private String city;
    private String userId;
    private List<Inventory> inventoryList;

    public Checkin() {
    }

    public Checkin(String userId, String id, String date, List<String> urls, List<Inventory> inventoryList) {
        this.id = id;
        this.date = date;
        this.urls = urls;
        this.userId = userId;
        this.inventoryList = inventoryList;
    }

    public Checkin(String userId, String id, String date, List<String> urls, String city, List<Inventory> inventoryList) {
        this.id = id;
        this.date = date;
        this.urls = urls;
        this.city = city;
        this.userId = userId;
        this.inventoryList = inventoryList;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getCity() {
        return city;
    }

    public String getUserId() {
        return userId;
    }

    public List<Inventory> getInventoryList() {
        return inventoryList;
    }

    public List<String> getUrls() {
        return urls;
    }
}
