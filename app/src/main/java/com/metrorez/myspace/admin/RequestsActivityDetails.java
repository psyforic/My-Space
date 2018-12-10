package com.metrorez.myspace.admin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
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
import com.google.firebase.database.Query;
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
    private List<Request> requests = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private SearchView search;
    private View parent_view;
    private LinearLayout lyt_not_found;
    private List<User> sendTo = new ArrayList<>();
    DatabaseReference requestsReference = FirebaseDatabase.getInstance().getReference().child("extras");
    DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AdminTheme);
        setContentView(R.layout.activity_requests_details);
        parent_view = findViewById(android.R.id.content);

        // animation transition
        ViewCompat.setTransitionName(parent_view, KEY_CITY);
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

        recyclerView = findViewById(R.id.recyclerView);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new AdminRequestsListAdapter(RequestsActivityDetails.this, requests, users);

    }

    private void populateAdapter() {
        Intent intent = getIntent();
        City city = (City) intent.getExtras().getSerializable(KEY_CITY);
        Query userQuery = usersReference.orderByChild("userCity").equalTo(city.getName());
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                requests.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    users.add(user);

                    requestsReference.child(user.getUserId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                                    Request request = requestSnapshot.getValue(Request.class);
                                    requests.add(request);
                                }
                                mAdapter = new AdminRequestsListAdapter(RequestsActivityDetails.this, requests, users);
                                recyclerView.setAdapter(mAdapter);
                            }
                            bindView();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void bindView() {
        try {
            mAdapter.setOnItemClickListener(new AdminRequestsListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(final View view, final Request obj, int position) {
                    usersReference.child(obj.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            sendTo.add(user);
                            int size = obj.getExtras().size();
                            List<String> items = new ArrayList<>();
                            for (Extra item : obj.getExtras()) {
                                items.add(item.getExtraName());
                            }
                            String request = obj.getCity() + "\n" + obj.getResidenceName() + "\n" + "Room: " + obj.getRoomNo() + "\n" + size + " Item(s)" + "\n" + items + "\n";
                            ResponseActivity.navigate(RequestsActivityDetails.this, view, sendTo.get(0), request, obj.getRequestDate());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            });
            if ((requests.size()) == 0 && (users.size() == 0)) {
                lyt_not_found.setVisibility(View.VISIBLE);
            } else {
                lyt_not_found.setVisibility(View.GONE);
            }
        } catch (Exception ex) {

        }
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
