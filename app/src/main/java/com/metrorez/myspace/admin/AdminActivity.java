package com.metrorez.myspace.admin;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.adapter.FragmentAdapter;
import com.metrorez.myspace.admin.fragment.AdminCheckinsFragment;
import com.metrorez.myspace.admin.fragment.AdminComplaintsFragment;
import com.metrorez.myspace.admin.fragment.AdminNotificationFragment;
import com.metrorez.myspace.admin.fragment.AdminRequestsFragment;
import com.metrorez.myspace.admin.fragment.UsersFragment;
import com.metrorez.myspace.user.LoginActivity;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import static android.view.View.GONE;

public class AdminActivity extends AppCompatActivity {


    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    public FloatingActionButton fab;
    private Toolbar searchToolbar;
    private ViewPager viewPager;
    private AdminNotificationFragment f_notifications;
    private NavigationView view;
    private boolean isSearch = false;
    private AdminComplaintsFragment f_complaints;
    private AdminRequestsFragment f_requests;
    private AdminCheckinsFragment f_checkins;
    private View parent_view;
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AdminTheme);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Slide());
            getWindow().setExitTransition(new Fade());
        } else {
            // Swap without transition
        }
        setContentView(R.layout.activity_admin);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {
                    //startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
            }
        };
        parent_view = findViewById(R.id.main_content);
        mAuth = FirebaseAuth.getInstance();
        setupDrawerLayout();
        initComponent();
        prepareActionBar(toolbar);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        loadUserInfo();
        tabLayout.setupWithViewPager(viewPager);
        initAction();
        setupTabIcons();
        // for system bar in lollipop
        Tools.systemBarLolipop(this);
    }


    private void initAction() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (viewPager.getCurrentItem()) {
                    case 0:
                        Snackbar.make(parent_view, "Add Check-in City Clicked", Snackbar.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Intent i = new Intent(getApplicationContext(), UsersActivity.class);
                        startActivity(i);
                        break;
                    case 2:
                        Snackbar.make(parent_view, "Add Requests City Clicked", Snackbar.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                closeSearch();
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        fab.setVisibility(GONE);
                        break;
                    case 1:
                        fab.setVisibility(GONE);
                        break;
                    case 2:
                        fab.setVisibility(GONE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        viewPager.setCurrentItem(1);
    }


    private void initComponent() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        toolbar = (Toolbar) findViewById(R.id.toolbar_viewpager);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        searchToolbar = (Toolbar) findViewById(R.id.toolbar_search);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());

        if (f_checkins == null) {
            f_checkins = new AdminCheckinsFragment();
        }
        if (f_complaints == null) {
            f_complaints = new AdminComplaintsFragment();
        }
        if (f_requests == null) {
            f_requests = new AdminRequestsFragment();
        }

        if (f_notifications == null) {
            f_notifications = new AdminNotificationFragment();
        }
        adapter.addFragment(f_checkins, getString(R.string.tab_checkins));
        adapter.addFragment(f_complaints, getString(R.string.tab_complaints));
        adapter.addFragment(f_requests, getString(R.string.tab_requests));
        adapter.addFragment(f_notifications, "");


        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {

        tabLayout.getTabAt(3).setIcon(R.drawable.ic_notification);
    }

    private void prepareActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        if (!isSearch) {
            settingDrawer();
        }
    }

    public void setVisibilityAppBar(boolean visible) {
        CoordinatorLayout.LayoutParams layout_visible = new CoordinatorLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        CoordinatorLayout.LayoutParams layout_invisible = new CoordinatorLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        if (visible) {
            appBarLayout.setLayoutParams(layout_visible);
            fab.show();
        } else {
            appBarLayout.setLayoutParams(layout_invisible);
            fab.hide();
        }
    }

    private void settingDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void setupDrawerLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        view = (NavigationView) findViewById(R.id.nav_view);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                if (menuItem.getItemId() == R.id.nav_logout) {
                    logout();
                    finish();
                } else {
                    Snackbar.make(parent_view, menuItem.getTitle() + " Clicked ", Snackbar.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!isSearch) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(isSearch ? R.menu.menu_search_toolbar : R.menu.menu_main, menu);
        if (isSearch) {
            final SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
            search.setIconified(false);
            switch (viewPager.getCurrentItem()) {
                case 0:
                    search.setQueryHint("Search users...");
                    break;
                case 1:
                    search.setQueryHint("Search complaints...");
                    break;
                case 2:
                    search.setQueryHint("Search requests...");
                    break;
            }
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    switch (viewPager.getCurrentItem()) {
                        case 0:
                            f_complaints.mAdapter.getFilter().filter(s);
                            break;
                        case 1:
                            f_requests.mAdapter.getFilter().filter(s);
                            break;
                        case 2:
                            f_checkins.mAdapter.getFilter().filter(s);
                            break;
                    }
                    return true;
                }
            });
            search.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    closeSearch();
                    return true;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search: {
                isSearch = true;
                searchToolbar.setVisibility(View.VISIBLE);
                prepareActionBar(searchToolbar);
                supportInvalidateOptionsMenu();
                return true;
            }
            case android.R.id.home:
                closeSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void closeSearch() {
        if (isSearch) {
            isSearch = false;
            if (viewPager.getCurrentItem() == 0) {
                //f_message.mAdapter.getFilter().filter("");
            } else {
                //f_contact.mAdapter.getFilter().filter("");
            }
            prepareActionBar(toolbar);
            searchToolbar.setVisibility(GONE);
            supportInvalidateOptionsMenu();
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
    public void onBackPressed() {
        doExitApp();
    }


    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    private void loadUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        View headerView = view.getHeaderView(0);
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                String photoUrl = user.getPhotoUrl().toString();
                ImageView profileImage = headerView.findViewById(R.id.avatar);
                Picasso.with(this).load(photoUrl)
                        .placeholder(R.drawable.unknown_avatar)
                        .resize(200, 200)
                        .transform(new CircleTransform())
                        .into(profileImage);
            }
            if (user.getDisplayName() != null) {
                String displayName = user.getDisplayName();
                TextView textView = headerView.findViewById(R.id.username);
                textView.setText(displayName);
            }
            if (user.getEmail() != null) {
                String email = user.getEmail();
                TextView textView = headerView.findViewById(R.id.email);
                textView.setText(email);
            }
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
}

