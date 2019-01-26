package com.metrorez.myspace.user.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Request implements Serializable {
    private String id;
    private String requestDate;
    private String userId;
    private List<Extra> extras = new ArrayList<>();
    private String city;
    private String roomNo;
    private String residenceName;
    private String userName;

    private Date realDate = new Date();

    public Request() {
    }

    public Request(String id, String requestDate, String userId, List<Extra> extras, String city, String residenceName, String roomNo, String userName) {
        this.id = id;
        this.requestDate = requestDate;
        this.userId = userId;
        this.extras = extras;
        this.city = city;
        this.roomNo = roomNo;
        this.residenceName = residenceName;
        this.userName = userName;
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

    public String getResidenceName() {
        return residenceName;
    }

    public String getUserName() {
        return userName;
    }

    public Date getRealDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            if (getRequestDate() != null) realDate = sdf.parse((this.getRequestDate()));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return realDate;
    }
}
