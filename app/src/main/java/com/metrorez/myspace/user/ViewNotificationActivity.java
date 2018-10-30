package com.metrorez.myspace.user;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.adapter.NotificationListAdapter;
import com.metrorez.myspace.user.data.Constants;
import com.metrorez.myspace.user.model.Checkin;
import com.metrorez.myspace.user.model.Complaint;
import com.metrorez.myspace.user.model.Extra;
import com.metrorez.myspace.user.model.Request;

import java.util.List;

public class ViewNotificationActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private String type;
    private String userId;
    private List<Complaint> complaints;
    private List<Request> extras;
    private List<Checkin> checkins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notification);
        Intent intent = getIntent();
        userId = intent.getStringExtra(NotificationListAdapter.USER_ID);
        type = intent.getStringExtra(NotificationListAdapter.TYPE);
        loadData();
    }

    private void loadData() {

        switch (type) {
            case Constants.CHECKIN_TYPE:
                databaseReference = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("checkins").addValueEventListener(valueEventListener);
                break;
            case Constants.COMPLAINT_TYPE:
                databaseReference = FirebaseDatabase.getInstance().getReference().child("complaints");
            case Constants.REQUEST_TYPE:
                databaseReference = FirebaseDatabase.getInstance().getReference().child("request");
                break;
        }
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (databaseReference != null) {
                switch (type) {

                    case Constants.CHECKIN_TYPE:
                        valueEventListener = queryCheckins();
                        break;
                    case Constants.COMPLAINT_TYPE:
                        valueEventListener = queryComplainst();
                        break;
                    case Constants.REQUEST_TYPE:
                        valueEventListener = queryRequests();
                        break;
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener queryCheckins() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot checkinSnapShot : dataSnapshot.getChildren()) {

                    Checkin checkin = checkinSnapShot.getValue(Checkin.class);
                    checkins.add(checkin);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        return valueEventListener;
    }

    private ValueEventListener queryComplainst() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot checkinSnapShot : dataSnapshot.getChildren()) {

                    Complaint complaint = checkinSnapShot.getValue(Complaint.class);
                    complaints.add(complaint);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        return valueEventListener;
    }

    private ValueEventListener queryRequests() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot checkinSnapShot : dataSnapshot.getChildren()) {
                    Request request = checkinSnapShot.getValue(Request.class);
                    extras.add(request);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        return valueEventListener;
    }
}
