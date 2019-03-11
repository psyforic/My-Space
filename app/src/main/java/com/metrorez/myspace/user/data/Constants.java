package com.metrorez.myspace.user.data;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.user.model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Constants {
    public static final String USER_ID = "USER_ID";
    public static final String USER_KEY = "USER_KEY";
    public static final String COMPLAINT_TYPE = "COMPLAINT";
    public static final String ADMIN_COMPLAINT_RESPONSE = "ADMIN RESPONSE";
    public static final String REQUEST_TYPE = "REQUEST";
    public static final String MOVEIN_TYPE = "MOVEIN";
    public static final String STRING_EXTRA = "EXTRA";
    public static final String COMPLAINT_EXTRA = "COMPLAINT_EXTRA";
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String NORMAL_USER = "NORMAL_USER";
    public static final String ADMIN_USER_ID = "vqvgYhDSvsX25fWWCrQD2Kgmz5B3";
    public static final String SENDGRID_TO_EMAIL = "info@metrorez.co.za";
    public static final String EMAIL_SUBJECT = "New Gym Access Request *";
    public static final String SLEEPOVER_EMAIL_SUBJECT = "New Sleepover Request *";
    public static final String JOB_EMAIL_SUBJECT = "New Job Request *";
    private static User[] users;

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users");
    public static final String MOVEIN_EXTRA = "MOVEIN_EXTRA";

    public Constants() {

        //mAuth = FirebaseAuth.getInstance();
    }


    public static User getUser() {

        userReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    users[0] = dataSnapshot.getValue(User.class);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return users[0];
    }

    public static String formatTime(long time) {
        // income time
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(time);

        // current time
        Calendar curDate = Calendar.getInstance();
        curDate.setTimeInMillis(System.currentTimeMillis());

        SimpleDateFormat dateFormat = null;
        if (date.get(Calendar.YEAR) == curDate.get(Calendar.YEAR)) {
            if (date.get(Calendar.DAY_OF_YEAR) == curDate.get(Calendar.DAY_OF_YEAR)) {
                dateFormat = new SimpleDateFormat("h:mm a", Locale.US);
            } else {
                dateFormat = new SimpleDateFormat("MMM d", Locale.US);
            }
        } else {
            dateFormat = new SimpleDateFormat("MMM yyyy", Locale.US);
        }
        return dateFormat.format(time);
    }

    public static String getToday() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String date = dateFormat.format(today);

        return date;
    }

    public static String getUserCity() {
        final String[] userCity = new String[1];
        userReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userCity[0] = user.getUserCity();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return userCity[0];
    }

    public static String getUserResidence() {
        final String[] userResidence = new String[1];
        userReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userResidence[0] = user.getUserResidence();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return userResidence[0];
    }

    public static String getUserRoomNo() {
        final String[] userRoomNo = new String[1];
        userReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userRoomNo[0] = user.getUserRoom();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return userRoomNo[0];
    }
}
