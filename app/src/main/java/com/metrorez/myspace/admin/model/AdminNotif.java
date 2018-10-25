package com.metrorez.myspace.admin.model;

public class AdminNotif {

    private String notif_id;
    private String fromUserId;
    private String date;
    private String content;
    private String toUserId;
    private String userName;

    public AdminNotif() {
    }

    public AdminNotif(String notif_id, String fromUserId, String userName, String date, String content, String toUserId) {
        this.notif_id = notif_id;
        this.fromUserId = fromUserId;
        this.date = date;
        this.content = content;
        this.toUserId = toUserId;
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
        return "<b>" + "New " + content + " from" + userName + "</b>";
    }

    public String getUserName() {
        return userName;
    }
}
