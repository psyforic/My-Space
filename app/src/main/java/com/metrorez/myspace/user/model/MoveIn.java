package com.metrorez.myspace.user.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MoveIn implements Serializable {

    private String id;
    private String date;
    private List<String> urls;
    private String city;
    private String userId;
    private List<MoveInItem> itemList = new ArrayList<>();
    private String extraComments;
    private String userResidence;
    private String userRoom;
    private String userName;
    private Date realDate = new Date();

    public MoveIn() {
    }

    public MoveIn(String userId, String id, String date, String userResidence, String userRoom, List<String> urls, List<MoveInItem> itemList, String userName) {
        this.id = id;
        this.date = date;
        this.urls = urls;
        this.userId = userId;
        this.itemList = itemList;
        this.userResidence = userResidence;
        this.userRoom = userRoom;
        this.userName = userName;
    }

    public MoveIn(String userId, String id, String date, List<String> urls, String city, String userResidence, String userRoom, List<MoveInItem> itemList, String userName) {
        this.id = id;
        this.date = date;
        this.urls = urls;
        this.city = city;
        this.userId = userId;
        this.itemList = itemList;
        this.userRoom = userRoom;
        this.userResidence = userResidence;
        this.userName = userName;
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

    public String getExtraComments() {
        return extraComments;
    }

    public String getUserResidence() {
        return userResidence;
    }

    public String getUserRoom() {
        return userRoom;
    }

    public String getUserName() {
        return userName;
    }

    public Date getRealDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            if (getDate() != null) realDate = sdf.parse((this.getDate()));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return realDate;
    }
}