package com.metrorez.myspace.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.adapter.AdminComplaintListAdapter;
import com.metrorez.myspace.admin.model.City;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.Complaint;
import com.metrorez.myspace.user.model.User;
import com.metrorez.myspace.user.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class ComplaintsDetailsActivity extends AppCompatActivity {
    public static String KEY_CITY = "com.app.sample.chatting.CITY";
    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private AdminComplaintListAdapter mAdapter;
    private List<Complaint> complaints;
    private List<User> users;
    private SearchView search;

    DatabaseReference complaintsReference = FirebaseDatabase.getInstance().getReference("complaints");
    DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints_details);
        populateAdapter();
        initToolbar();
        initComponent();

        Tools.systemBarLolipop(this);
    }

    public static void navigate(AppCompatActivity activity, View transitionImage, City obj) {
        Intent intent = new Intent(activity, ComplaintsDetailsActivity.class);
        intent.putExtra(KEY_CITY, obj);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, KEY_CITY);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    private void initComponent() {

        complaints = new ArrayList<>();
        users = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new AdminComplaintListAdapter(this, complaints, users);
        mAdapter.setOnItemClickListener(new AdminComplaintListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Complaint obj, int position) {

            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    private void populateAdapter() {

        complaintsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                complaints.clear();
                for (DataSnapshot complaintsSnapshot : dataSnapshot.getChildren()) {
                    Complaint complaint = complaintsSnapshot.getValue(Complaint.class);
                    complaints.add(complaint);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    users.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        recyclerView.setAdapter(mAdapter);
    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.str_complaints));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_complaints, menu);
        search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setIconified(false);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return true;
            }
        });
        search.onActionViewCollapsed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_search: {

                supportInvalidateOptionsMenu();
                return true;
            }
        }
        return false;
    }

}
