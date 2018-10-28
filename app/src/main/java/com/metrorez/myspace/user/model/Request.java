package com.metrorez.myspace.user.model;

import java.io.Serializable;
import java.util.List;

public class Request implements Serializable {
    private String id;
    private String requestDate;
    private String userId;
    private List<Extra> extras;
    private String city;
    private String roomNo;

    public Request() {
    }

    public Request(String id, String requestDate, String userId, List<Extra> extras, String city, String roomNo) {
        this.id = id;
        this.requestDate = requestDate;
        this.userId = userId;
        this.extras = extras;
        this.city = city;
        this.roomNo = roomNo;
    }

    public String getId() {
        return id;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public String getUserId() {
        return userId;
    }

    public List<Extra> getExtras() {
        return extras;
    }

    public String getCity() {
        return city;
    }

    public String getRoomNo() {
        return roomNo;
    }
}
