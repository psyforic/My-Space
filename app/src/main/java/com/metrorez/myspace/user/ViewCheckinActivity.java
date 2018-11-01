package com.metrorez.myspace.user;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.metrorez.myspace.user.adapter.CheckinItemAdapter;
import com.metrorez.myspace.user.adapter.ComplaintListAdapter;
import com.metrorez.myspace.user.data.Constants;
import com.metrorez.myspace.user.model.Checkin;
import com.metrorez.myspace.user.model.Complaint;
import com.metrorez.myspace.user.model.Inventory;

import java.util.ArrayList;
import java.util.List;

public class ViewCheckinActivity extends AppCompatActivity {

    String checkinId;
    private TextView date;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private List<Inventory> items = new ArrayList<>();
    private RecyclerView recyclerView;
    private CheckinItemAdapter mAdapter;
    private DatabaseReference checkinsReference = FirebaseDatabase.getInstance().getReference("checkins");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_checkin);
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        Checkin checkin = (Checkin) intent.getSerializableExtra(Constants.CHECKIN_EXTRA);
        checkinId = checkin.getId();
        setupUi();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("View Check-in");
    }

    private void setupUi() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        date = findViewById(R.id.date);
        getData();
    }

    private void getData() {
        checkinsReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot checkinSnapshot : dataSnapshot.getChildren()) {
                        Checkin checkin = checkinSnapshot.getValue(Checkin.class);
                        items.addAll(checkin.getInventoryList());
                        date.setText(checkin.getDate());
                        mAdapter = new CheckinItemAdapter(ViewCheckinActivity.this, items);
                        recyclerView.setAdapter(mAdapter);
                    }
                }
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

    public static void navigate(AppCompatActivity activity, View transitionImage, Checkin obj) {
        Intent intent = new Intent(activity, ViewComplaintActivity.class);
        intent.putExtra(Constants.CHECKIN_EXTRA, obj);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, Constants.CHECKIN_EXTRA);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }
}
