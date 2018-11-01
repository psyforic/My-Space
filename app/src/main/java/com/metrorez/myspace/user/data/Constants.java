package com.metrorez.myspace.user.data;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.user.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Constants {
    public static final String USER_ID = "USER_ID";
    public static final String USER_KEY = "USER_KEY";
    public static final String COMPLAINT_TYPE = "COMPLAINT";
    public static final String ADMIN_COMPLAINT_RESPONSE = "ADMIN_COMPLAINT_RESPONSE";
    public static final String REQUEST_TYPE = "REQUEST";
    public static final String CHECKIN_TYPE = "CHECKIN";
    public static final String STRING_EXTRA = "EXTRA";
    public static final String COMPLAINT_EXTRA = "COMPLAINT_EXTRA";
    private static User[] users;

    private static FirebaseAuth mAuth;

    private static DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users");
    public static final String CHECKIN_EXTRA = "CHECKIN_EXTRA";

    public Constants() {

        mAuth = FirebaseAuth.getInstance();
    }


    public static User getUser() {

        userReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
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
    public static String formatTime(long time){
        // income time
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(time);

        // current time
        Calendar curDate = Calendar.getInstance();
        curDate.setTimeInMillis(System.currentTimeMillis());

        SimpleDateFormat dateFormat = null;
        if(date.get(Calendar.YEAR)==curDate.get(Calendar.YEAR)){
            if(date.get(Calendar.DAY_OF_YEAR) == curDate.get(Calendar.DAY_OF_YEAR) ){
                dateFormat = new SimpleDateFormat("h:mm a", Locale.US);
            }
            else{
                dateFormat = new SimpleDateFormat("MMM d", Locale.US);
            }
        }
        else{
            dateFormat = new SimpleDateFormat("MMM yyyy", Locale.US);
        }
        return dateFormat.format(time);
    }
}
