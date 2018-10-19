package com.metrorez.myspace.user.data;

import android.app.Application;

import com.metrorez.myspace.user.model.Complaint;
import com.metrorez.myspace.user.model.Extra;
import com.metrorez.myspace.user.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class GlobalVariable extends Application {

    private List<Complaint> complaints = new ArrayList<>();
    private List<Extra> extras = new ArrayList<>();
    private List<Notification> notifications = new ArrayList<>();

    public List<Complaint> getComplaints() {
        return complaints;
    }

    public List<Extra> getExtras() {
        return extras;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setComplaints(List<Complaint> complaints) {
        this.complaints = complaints;
    }

    public void setExtras(List<Extra> extras) {
        this.extras = extras;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
