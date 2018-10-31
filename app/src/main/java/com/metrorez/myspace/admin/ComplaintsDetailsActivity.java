package com.metrorez.myspace.admin;

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
import android.util.Log;
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
import com.metrorez.myspace.admin.model.City;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.Complaint;
import com.metrorez.myspace.user.model.User;
import com.metrorez.myspace.user.widget.DividerItemDecoration;

import java.util.ArrayList;
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
    private LinearLayout lyt_not_found;
    DatabaseReference complaintsReference = FirebaseDatabase.getInstance().getReference().child("complaints");
    DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints_details);
        parent_view = findViewById(android.R.id.content);

        // animation transition
        ViewCompat.setTransitionName(parent_view, KEY_CITY);
        initToolbar();
        initComponent();
        getData();
        Tools.systemBarLolipop(this);
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
        usersReference.addValueEventListener(new ValueEventListener() {
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
//                            users.clear();
//                            complaints.clear();
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot complaintSnapShot : dataSnapshot.getChildren()) {
                                    Complaint complaint = complaintSnapShot.getValue(Complaint.class);
                                    complaints.add(complaint);
                                }
                                mAdapter = new AdminComplaintListAdapter(ComplaintsDetailsActivity.this, complaints, users);
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

    private void initComponent() {
        lyt_not_found = findViewById(R.id.lyt_not_found);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
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

    private void bindView() {
        try {
            mAdapter.setOnItemClickListener(new AdminComplaintListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, Complaint obj, int position) {

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
                    ResponseActivity.navigate(ComplaintsDetailsActivity.this, view, user[0], obj.getComplaintComment(), obj.getComplaintDate());
                }
            });

            Log.d("DATA_ITEMS", String.valueOf(recyclerView.getAdapter().getItemCount()));
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
        getData();
        initComponent();

    }
}
