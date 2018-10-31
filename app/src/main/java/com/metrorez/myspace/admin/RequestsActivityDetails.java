package com.metrorez.myspace.admin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.adapter.AdminComplaintListAdapter;
import com.metrorez.myspace.admin.adapter.AdminRequestsListAdapter;
import com.metrorez.myspace.admin.model.City;
import com.metrorez.myspace.user.model.Request;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.Complaint;
import com.metrorez.myspace.user.model.Extra;
import com.metrorez.myspace.user.model.User;
import com.metrorez.myspace.user.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class RequestsActivityDetails extends AppCompatActivity {
    public static String KEY_CITY = "CITY";
    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private AdminRequestsListAdapter mAdapter;
    private List<Request> requests;
    private List<User> users;
    private SearchView search;
    private LinearLayout lyt_not_found;

    DatabaseReference requestsReference = FirebaseDatabase.getInstance().getReference().child("requests");
    DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_details);
        initToolbar();
        initComponent();
        populateAdapter();
        Tools.systemBarLolipop(this);
    }

    public static void navigate(AppCompatActivity activity, View transitionImage, City obj) {
        Intent intent = new Intent(activity, RequestsActivityDetails.class);
        intent.putExtra(KEY_CITY, obj);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, KEY_CITY);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    private void initComponent() {

        requests = new ArrayList<>();
        users = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new AdminRequestsListAdapter(this, requests, users);
        mAdapter.setOnItemClickListener(new AdminRequestsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Request obj, int position) {

                final User[] user = new User[1];
                usersReference.child(obj.getUserId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        user[0] = dataSnapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                int size = obj.getExtras().size();
                List<String> items = new ArrayList<>();
                for (Extra item : obj.getExtras()) {
                    items.add(item.getExtraName());
                }
                String request = obj.getCity() + "\n" + size + " Items" + "\n" + items;
                ResponseActivity.navigate(RequestsActivityDetails.this, view, user[0], request, obj.getRequestDate());

            }
        });
    }

    private void populateAdapter() {

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    users.add(user);

                    requestsReference.child(user.getUserId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                                Request request = requestSnapshot.getValue(Request.class);
                                requests.add(request);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                recyclerView.setAdapter(mAdapter);

                if (requests.size() == 0&& users.size() == 0) {
                    lyt_not_found.setVisibility(View.VISIBLE);
                } else {
                    lyt_not_found.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
        getSupportActionBar().setTitle(getString(R.string.str_request));
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
