package com.metrorez.myspace.user;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.ComplaintsDetailsActivity;
import com.metrorez.myspace.user.data.Constants;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.Complaint;

public class ViewComplaintActivity extends AppCompatActivity {

    String complaintId;
    private TextView complaint, date;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private DatabaseReference complaintsReference = FirebaseDatabase.getInstance().getReference("complaints");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_complaint);

        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        Complaint complaint = (Complaint) intent.getSerializableExtra(Constants.COMPLAINT_EXTRA);
        complaintId = complaint.getComplaintId();
        setupUi();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("View Complaint");
        Tools.systemBarLolipop(this);
    }

    private void setupUi() {
        toolbar = findViewById(R.id.toolbar);
        complaint = findViewById(R.id.complaint);
        date = findViewById(R.id.date);
        getData();

    }

    private void getData() {
        complaintsReference.child(mAuth.getCurrentUser().getUid()).child(complaintId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Complaint newComplaint = dataSnapshot.getValue(Complaint.class);
                complaint.setText(newComplaint.getComplaintComment());
                date.setText(newComplaint.getComplaintDate());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static void navigate(AppCompatActivity activity, View transitionImage, Complaint obj) {
        Intent intent = new Intent(activity, ViewComplaintActivity.class);
        intent.putExtra(Constants.COMPLAINT_EXTRA, obj);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, Constants.COMPLAINT_EXTRA);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }
}
