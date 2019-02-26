package com.metrorez.myspace.user.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.data.Utils;
import com.metrorez.myspace.user.adapter.NotificationListAdapter;
import com.metrorez.myspace.user.data.GlobalVariable;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.fragment.BillingFragment;
import com.metrorez.myspace.user.fragment.HomeFragment;
import com.metrorez.myspace.user.fragment.MoveInFragment;
import com.metrorez.myspace.user.fragment.ComplaintFragment;
import com.metrorez.myspace.user.fragment.ExtrasFragment;
import com.metrorez.myspace.user.fragment.NotificationsFragment;
import com.metrorez.myspace.user.fragment.ProfileFragment;
import com.metrorez.myspace.user.fragment.SettingFragment;
import com.metrorez.myspace.user.model.Notification;
import com.metrorez.myspace.user.model.User;
import com.metrorez.myspace.user.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    public ActionBar actionBar;
    private NavigationView navigationView;
    private GlobalVariable global;
    FirebaseAuth mAuth;
    private List<Notification> notifications = new ArrayList<>();
    private int count;
    FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users");
    private DatabaseReference notificationsReference = FirebaseDatabase.getInstance().getReference().child("notifications");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        global = (GlobalVariable) getApplication();
        initToolbar();
        initDrawerMenu();
        mAuth = FirebaseAuth.getInstance();
        // set home view
        actionBar.setTitle(getString(R.string.str_nav_home));
        getNotifications();
        displayContentView(R.id.nav_home);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {

                }
            }
        };
        loadUserInfo();
        // for system bar in lollipop
        Tools.systemBarLolipop(this);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void initDrawerMenu() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                updateDrawerCounter();
                hideKeyboard();
                super.onDrawerOpened(drawerView);
            }
        };
        //drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawer.closeDrawers();
                if (menuItem.getItemId() != R.id.nav_logout) {
                    actionBar.setTitle(menuItem.getTitle());
                }
                if (menuItem.getItemId() == R.id.nav_logout) {
                    logout();
                } else {
                    if (menuItem.getItemId() != R.id.nav_logout)
                        displayContentView(menuItem.getItemId());
                }
                return true;
            }
        });
        onHeaderClicked();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            doExitApp();
        }
    }

    @Override
    protected void onResume() {
        updateDrawerCounter();
        super.onResume();
    }

    private void updateDrawerCounter() {
        //setMenuAdvCounter(R.id.nav_complaint, global.getComplaints().size());
        setMenuAdvCounter(R.id.nav_notifications, count);
    }

    //set counter in drawer
    private void setMenuAdvCounter(@IdRes int itemId, int count) {
        TextView view = (TextView) navigationView.getMenu().findItem(itemId).getActionView().findViewById(R.id.counter);
        view.setText(count > 0 ? String.valueOf(count) : "0");
    }

    private void getNotifications() {

        Query query = notificationsReference.orderByChild("toUserId").equalTo(mAuth.getCurrentUser().getUid());
        query.addValueEventListener(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            notifications.clear();

            for (DataSnapshot notifSnapShot : dataSnapshot.getChildren()) {
                Notification notification = notifSnapShot.getValue(Notification.class);
                if (!notification.isRead())
                    notifications.add(notification);
            }

            count = notifications.size();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //set counter in drawer
    private void setMenuStdCounter(@IdRes int itemId, int count) {
        TextView view = (TextView) navigationView.getMenu().findItem(itemId).getActionView().findViewById(R.id.counter);
        view.setText(count > 0 ? String.valueOf(count) : "0");
    }

    private void onHeaderClicked() {
        View headerView = navigationView.getHeaderView(0);
        LinearLayout header = headerView.findViewById(R.id.profile_image);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void displayContentView(int id) {
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        switch (id) {
            case R.id.nav_complaint:
                fragment = new ComplaintFragment();
                break;
            case R.id.nav_extras:
                fragment = new ExtrasFragment();
                break;
            case R.id.nav_notifications:
                fragment = new NotificationsFragment();
                break;
            case R.id.nav_profile:
                fragment = new ProfileFragment();
                break;
            case R.id.nav_setting:
                fragment = new SettingFragment();
                break;
            case R.id.nav_checkin:
                fragment = new MoveInFragment();
                break;
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_billing:
                fragment = new BillingFragment();
                break;
        }
        fragment.setArguments(bundle);
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_content, fragment);
            fragmentTransaction.commit();
        }
    }

    private void loadUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        final View headerView = navigationView.getHeaderView(0);
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                String photoUrl = user.getPhotoUrl().toString();
                ImageView profileImage = headerView.findViewById(R.id.image_header);
                Picasso.with(this).load(photoUrl)
                        .placeholder(R.drawable.unknown_avatar)
                        .resize(200, 200)
                        .transform(new CircleTransform())
                        .into(profileImage);
                //Glide.with(this).load(photoUrl).into(profileImage);
            }
            if (user.getDisplayName() != null) {
                String displayName = user.getDisplayName();
                TextView textView = headerView.findViewById(R.id.header_title_name);
                textView.setText(displayName);
            } else {
                userReference.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        String displayName = user.getUserFirstName() + " " + user.getUserLastName();
                        TextView textView = headerView.findViewById(R.id.header_title_name);
                        textView.setText(displayName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.nav_logout) {
            mAuth.signOut();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.logout_message);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
