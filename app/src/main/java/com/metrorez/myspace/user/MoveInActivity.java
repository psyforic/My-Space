package com.metrorez.myspace.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.metrorez.myspace.R;
import com.metrorez.myspace.user.adapter.InventoryListAdapter;
import com.metrorez.myspace.user.adapter.PageFragmentAdapter;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.fragment.StepOneFragment;
import com.metrorez.myspace.user.fragment.StepTwoFragment;
import com.metrorez.myspace.user.model.Inventory;

import java.util.ArrayList;
import java.util.List;

public class MoveInActivity extends AppCompatActivity implements StepOneFragment.OnInventoryDataListener {

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private int[] layouts;
    public ActionBar actionBar;
    private Toolbar toolbar;
    private TextView[] dots;
    private Button btnSkip, btnNext;
    private PageFragmentAdapter adapter;
    private View parentView;
    List<Inventory> tempList;
    private StepOneFragment f_stepOne;
    private StepTwoFragment f_stepTwo;

    private InventoryListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_in);
        initToolbar();
        setupUI();

        onClickListeners();

        Tools.systemBarLolipop(this);
    }

    private void onClickListeners() {
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length && tempList.size() != 0) {
                    // move to next screen
                    viewPager.setCurrentItem(current);

                } else {
                    //launchHomeScreen();
                }
            }
        });
    }

    private void launchHomeScreen() {
        onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void setupUI() {
        tempList = new ArrayList<>();
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        setupViewPager(viewPager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);

        btnSkip.setVisibility(View.GONE);
        btnNext = (Button) findViewById(R.id.btn_next);

        parentView = findViewById(android.R.id.content);
        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.fragment_step_one,
                R.layout.fragment_step_two};
        // adding bottom dots
        addBottomDots(0);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new PageFragmentAdapter(getSupportFragmentManager());
        if (f_stepOne == null) {
            f_stepOne = new StepOneFragment();

        }
        if (f_stepTwo == null) {
            f_stepTwo = new StepTwoFragment();
        }
        adapter.addFragment(f_stepOne, getString(R.string.str_check_items));
        adapter.addFragment(f_stepTwo, getString(R.string.str_inventory_condition));
        viewPager.setAdapter(adapter);
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            if (position == layouts.length - 1) {
                // last page. make button text to Submit
                btnNext.setText(getString(R.string.submit));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onInventoryDataReceived(ArrayList<Inventory> inventoryData) {
        String tag = "android:switcher:" + R.id.view_pager + ":" + 1;
        StepTwoFragment fragment = (StepTwoFragment) getSupportFragmentManager().findFragmentByTag(tag);
        tempList = inventoryData;
        fragment.setupRecycler(inventoryData);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
