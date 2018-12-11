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
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.adapter.AdminCheckinListAdapter;
import com.metrorez.myspace.admin.model.City;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.MoveIn;
import com.metrorez.myspace.user.model.Inventory;
import com.metrorez.myspace.user.model.MoveInItem;
import com.metrorez.myspace.user.model.User;
import com.metrorez.myspace.user.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class CheckinDetailsActivity extends AppCompatActivity {
    public static String KEY_CITY = "CITY";
    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private AdminCheckinListAdapter mAdapter;
    private List<MoveIn> moveIns = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private LinearLayout lyt_not_found;
    private SearchView search;
    private View parent_view;
    private ProgressBar progressBar;
    private List<User> sendTo = new ArrayList<>();

    DatabaseReference checkinReference = FirebaseDatabase.getInstance().getReference().child("moveIns");
    DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AdminTheme);
        setContentView(R.layout.activity_checkin_details);
        parent_view = findViewById(android.R.id.content);
        // animation transition
        ViewCompat.setTransitionName(parent_view, KEY_CITY);
        initToolbar();
        initComponent();
        populateAdapter();
        Tools.adminSystemBarLollipop(this);
    }

    public static void navigate(AppCompatActivity activity, View transitionImage, City obj) {
        Intent intent = new Intent(activity, CheckinDetailsActivity.class);
        intent.putExtra(KEY_CITY, obj);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, KEY_CITY);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    private void initComponent() {

        recyclerView = findViewById(R.id.recyclerView);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        progressBar = findViewById(R.id.progressBar);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new AdminCheckinListAdapter(this, moveIns, users);

    }

    private void populateAdapter() {
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        City city = (City) intent.getExtras().getSerializable(KEY_CITY);
        Query userQuery = usersReference.orderByChild("userCity").equalTo(city.getName());
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                moveIns.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    users.add(user);
                    checkinReference.child(user.getUserId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot checkinSnapshot : dataSnapshot.getChildren()) {
                                    MoveIn moveIn = checkinSnapshot.getValue(MoveIn.class);
                                    moveIns.add(moveIn);
                                }

                                mAdapter = new AdminCheckinListAdapter(CheckinDetailsActivity.this, moveIns, users);
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

    private void bindView() {
        try {
            mAdapter.setOnItemClickListener(new AdminCheckinListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(final View view, final MoveIn obj, int position) {
                    usersReference.child(obj.getUserId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            User user = dataSnapshot.getValue(User.class);
                            sendTo.add(user);

                            List<String> items = new ArrayList<>();
                            for (MoveInItem item : obj.getItemList()) {
                                items.add(item.getItemName());
                            }
                            String checkin = obj.getCity()+ "\n" + obj.getUserResidence() + "\n" + obj.getUserRoom() + "\n" + "ITEMS CHECKED IN " + "\n" + items.toString();
                            ResponseActivity.navigate(CheckinDetailsActivity.this, view, sendTo.get(0), checkin, obj.getDate());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
            if (moveIns.size() == 0 && users.size() == 0) {
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
        getSupportActionBar().setTitle(getString(R.string.str_nav_checkins));
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
