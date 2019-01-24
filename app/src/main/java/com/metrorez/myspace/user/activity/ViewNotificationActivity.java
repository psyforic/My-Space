package com.metrorez.myspace.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.adapter.NotificationListAdapter;
import com.metrorez.myspace.user.data.Constants;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.Complaint;
import com.metrorez.myspace.user.model.MoveIn;
import com.metrorez.myspace.user.model.Request;

import java.util.List;

public class ViewNotificationActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private String type;
    private String userId;
    private List<Complaint> complaints;
    private List<Request> extras;
    private List<MoveIn> moveIns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notification);
        Intent intent = getIntent();
        userId = intent.getStringExtra(NotificationListAdapter.USER_ID);
        type = intent.getStringExtra(NotificationListAdapter.TYPE);
        loadData();
        Tools.systemBarLolipop(this);
    }

    private void loadData() {

        /*switch (type) {
            case Constants.MOVEIN_TYPE:
                databaseReference = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("moveIns").addValueEventListener(valueEventListener);
                break;
            case Constants.COMPLAINT_TYPE:
                databaseReference = FirebaseDatabase.getInstance().getReference().child("complaints");
            case Constants.REQUEST_TYPE:
                databaseReference = FirebaseDatabase.getInstance().getReference().child("request");
                break;
        }*/

      /*  switch (type) {
            case Constants.MOVEIN_TYPE:
                startActivity(new Intent(ViewNotificationActivity.this, MainActivity.class));
                break;
            case Constants.COMPLAINT_TYPE:
                startActivity(new Intent(ViewNotificationActivity.this, MainActivity.class));
                break;
            case Constants.REQUEST_TYPE:
                startActivity(new Intent(ViewNotificationActivity.this, MainActivity.class));
                break;
            default:
                startActivity(new Intent(ViewNotificationActivity.this, AdminActivity.class));
                break;
        }*/
        startActivity(new Intent(ViewNotificationActivity.this, MainActivity.class));
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (databaseReference != null) {
                switch (type) {

                    case Constants.MOVEIN_TYPE:
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

                    MoveIn moveIn = checkinSnapShot.getValue(MoveIn.class);
                    moveIns.add(moveIn);
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
