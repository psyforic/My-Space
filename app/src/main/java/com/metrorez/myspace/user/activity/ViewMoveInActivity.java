package com.metrorez.myspace.user.activity;

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
import com.metrorez.myspace.user.adapter.MoveInItemAdapter;
import com.metrorez.myspace.user.data.Constants;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.MoveIn;
import com.metrorez.myspace.user.model.MoveInItem;

import java.util.ArrayList;
import java.util.List;

public class ViewMoveInActivity extends AppCompatActivity {

    String checkinId;
    private TextView date;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private List<MoveInItem> items = new ArrayList<>();
    private RecyclerView recyclerView;
    private MoveInItemAdapter mAdapter;
    private DatabaseReference checkinsReference = FirebaseDatabase.getInstance().getReference("moveIns");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_move_in);
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        MoveIn moveIn = (MoveIn) intent.getSerializableExtra(Constants.MOVEIN_EXTRA);
        checkinId = moveIn.getId();
        setupUi();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("View Move-in");
        Tools.systemBarLolipop(this);
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
        checkinsReference.child(mAuth.getCurrentUser().getUid()).child(checkinId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    MoveIn moveIn = dataSnapshot.getValue(MoveIn.class);
                    items.addAll(moveIn.getItemList());
                    date.setText(moveIn.getDate());
                    mAdapter = new MoveInItemAdapter(ViewMoveInActivity.this, items);
                    recyclerView.setAdapter(mAdapter);
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

    public static void navigate(AppCompatActivity activity, View transitionImage, MoveIn obj) {
        Intent intent = new Intent(activity, ViewMoveInActivity.class);
        intent.putExtra(Constants.MOVEIN_EXTRA, obj);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, Constants.MOVEIN_EXTRA);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }
}
