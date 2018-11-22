package com.metrorez.myspace.user.model;

import java.io.Serializable;
import java.util.List;

public class MoveIn implements Serializable {

    private String id;
    private String date;
    private List<String> urls;
    private String city;
    private String userId;
    private List<MoveInItem> itemList;

    public MoveIn() {
    }

    public MoveIn(String userId, String id, String date, List<String> urls, List<MoveInItem> itemList) {
        this.id = id;
        this.date = date;
        this.urls = urls;
        this.userId = userId;
        this.itemList = itemList;
    }

    public MoveIn(String userId, String id, String date, List<String> urls, String city, List<MoveInItem> itemList) {
        this.id = id;
        this.date = date;
        this.urls = urls;
        this.city = city;
        this.userId = userId;
        this.itemList = itemList;
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

//    public List<MoveInItem> getInventoryList() {
//        return itemList;
//    }

    public List<MoveInItem> getItemList() {
        return itemList;
    }

    public List<String> getUrls() {
        return urls;
    }
}
