package com.metrorez.myspace.admin.data;

import com.metrorez.myspace.user.model.Complaint;
import com.metrorez.myspace.user.model.MoveIn;
import com.metrorez.myspace.user.model.Notification;
import com.metrorez.myspace.user.model.Request;

import java.util.Comparator;

public class Utils {
    public static String userID;
    public static String Role;


    public static class NotificationComparator implements Comparator<Notification> {

        @Override
        public int compare(Notification notification, Notification t1) {
            return -(notification.getRealDate().compareTo(t1.getRealDate()));
        }
    }

    public static class ComplaintComparator implements Comparator<Complaint> {

        @Override
        public int compare(Complaint complaint, Complaint t1) {
            return -(complaint.getRealDate().compareTo(t1.getRealDate()));
        }
    }

    public static class RequestComparator implements Comparator<Request> {

        @Override
        public int compare(Request request, Request t1) {
            return -(request.getRealDate().compareTo(t1.getRealDate()));
        }
    }

    public static class MoveInComparator implements Comparator<MoveIn> {

        @Override
        public int compare(MoveIn moveIn, MoveIn t1) {
            return -(moveIn.getRealDate().compareTo(t1.getRealDate()));
        }
    }
}
