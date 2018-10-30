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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
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

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

public class ComplaintsDetailsActivity extends AppCompatActivity {
    public static String KEY_CITY = "com.app.sample.chatting.CITY";
    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private AdminComplaintListAdapter mAdapter;
    private SearchView search;

    //private ListUsers

    private List<Complaint> complaints;
    private List<User> users;

    DatabaseReference complaintsReference = FirebaseDatabase.getInstance().getReference("complaints");
    DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints_details);
        initComponent();
        initToolbar();

        mAdapter.setOnItemClickListener(new AdminComplaintListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Complaint obj, int position) {

            }
        });
        Tools.systemBarLolipop(this);
    }

    public static void navigate(AppCompatActivity activity, View transitionImage, City obj) {
        Intent intent = new Intent(activity, ComplaintsDetailsActivity.class);
        intent.putExtra(KEY_CITY, obj);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, KEY_CITY);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    private void initComponent() {

        recyclerView = findViewById(R.id.recyclerView);
        // use a linear layout manager
        users = new ArrayList<>();
        complaints = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new AdminComplaintListAdapter(ComplaintsDetailsActivity.this, complaints, users);
        complaintsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot complaintsSnapShot : dataSnapshot.getChildren()) {
                    Iterable<DataSnapshot> children = complaintsSnapShot.getChildren();
                    for (DataSnapshot snapshot : children) {
                        Complaint complaint = snapshot.getValue(Complaint.class);
                        complaints.add(complaint);
                    }

                }
                Log.i("COMPLAINTS", String.valueOf(complaints.size()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("RETRIEVEFAILER", databaseError.getMessage());
            }
        });
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                    User user = userSnapshot.getValue(User.class);
                    users.add(user);
                }
                recyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("RETRIEVEFAILER", databaseError.getMessage());
            }
        });
        mAdapter.notifyDataSetChanged();
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

    @Override
    protected void onResume() {
        super.onResume();
    }
}
