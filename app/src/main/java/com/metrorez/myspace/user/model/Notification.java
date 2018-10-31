package com.metrorez.myspace.user.model;

import java.io.Serializable;

public class Notification implements Serializable {

    private String notif_id;
    private String fromUserId;
    private String date;
    private String content;
    private String userName;
    private String type;
    private String typeId;
    private String userId;

    public Notification() {
    }

    public Notification(String userId, String notif_id, String fromUserId, String userName, String date, String content) {
        this.notif_id = notif_id;
        this.fromUserId = fromUserId;
        this.date = date;
        this.content = content;
        this.userName = userName;
        this.userId = userId;
    }

    public Notification(String userId, String notif_id, String fromUserId, String date, String content, String userName, String type, String typeId) {
        this.notif_id = notif_id;
        this.fromUserId = fromUserId;
        this.date = date;
        this.content = content;
        this.userName = userName;
        this.type = type;
        this.typeId = typeId;
        this.userId = userId;
    }

    public String getNotif_id() {
        return notif_id;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content + " from " + userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getType() {
        return type;
    }

    public String getTypeId() {
        return typeId;
    }

    public String getUserId() {
        return userId;
    }
}
