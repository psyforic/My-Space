package com.metrorez.myspace.admin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.adapter.AdminComplaintListAdapter;
import com.metrorez.myspace.admin.data.Utils;
import com.metrorez.myspace.admin.model.City;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.Complaint;
import com.metrorez.myspace.user.model.User;
import com.metrorez.myspace.user.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComplaintsDetailsActivity extends AppCompatActivity {
    public static String KEY_CITY = "CITY";
    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private AdminComplaintListAdapter mAdapter;
    private SearchView search;
    private List<Complaint> complaints = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private View parent_view;
    private ProgressBar progressBar;
    private List<User> sendTo = new ArrayList<>();
    private LinearLayout lyt_not_found;
    DatabaseReference complaintsReference = FirebaseDatabase.getInstance().getReference().child("complaints");
    DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AdminTheme);
        setContentView(R.layout.activity_complaints_details);
        parent_view = findViewById(android.R.id.content);

        // animation transition
        ViewCompat.setTransitionName(parent_view, KEY_CITY);
        initToolbar();
        initComponent();
        getData();
        Tools.adminSystemBarLollipop(this);
    }

    public static void navigate(AppCompatActivity activity, View transitionImage, City obj) {
        Intent intent = new Intent(activity, ComplaintsDetailsActivity.class);
        intent.putExtra(KEY_CITY, obj);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, KEY_CITY);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        City city = (City) intent.getExtras().getSerializable(KEY_CITY);
        Query userQuery = usersReference.orderByChild("userCity").equalTo(city.getName());

        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                complaints.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    users.add(user);
                    complaintsReference.child(user.getUserId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot complaintSnapShot : dataSnapshot.getChildren()) {
                                    Complaint complaint = complaintSnapShot.getValue(Complaint.class);
                                    complaints.add(complaint);
                                }
                                Collections.sort(complaints, new Utils.ComplaintComparator());
                                mAdapter = new AdminComplaintListAdapter(ComplaintsDetailsActivity.this, complaints, users);
                                recyclerView.setAdapter(mAdapter);
                            }
                            bindView();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void initComponent() {
        lyt_not_found = findViewById(R.id.lyt_not_found);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        Collections.sort(complaints, new Utils.ComplaintComparator());
        mAdapter = new AdminComplaintListAdapter(ComplaintsDetailsActivity.this, complaints, users);
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

    public void bindView() {
        try {
            mAdapter.setOnItemClickListener(new AdminComplaintListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(final View view, final Complaint obj, final User userObj, final int position) {

                    usersReference.child(obj.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            User user = dataSnapshot.getValue(User.class);
                            sendTo.add(user);
                            String complaint = obj.getComplaintComment() + "\n" + "PRIORITY: " + obj.getComplaintCategory() + "\n" + obj.getComplaintResidence() + "\n"
                                    + "ROOM NO. : " + obj.getComplaintRoom();
                            if (obj.getImagePath() == null) {
                                ResponseActivity.navigate(ComplaintsDetailsActivity.this, view, userObj, complaint, obj.getComplaintDate());
                            } else {
                                ResponseActivity.navigate(ComplaintsDetailsActivity.this, view, userObj, complaint, obj.getComplaintDate(), obj.getImagePath());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });

            if ((complaints.size() == 0) && (users.size() == 0)) {
                lyt_not_found.setVisibility(View.VISIBLE);
            } else {
                lyt_not_found.setVisibility(View.GONE);
            }


        } catch (Exception e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
