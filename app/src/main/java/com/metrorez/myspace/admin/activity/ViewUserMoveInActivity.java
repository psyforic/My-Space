package com.metrorez.myspace.admin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.adapter.MoveInImageAdapter;
import com.metrorez.myspace.admin.model.Image;
import com.metrorez.myspace.user.adapter.MoveInItemAdapter;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.MoveIn;
import com.metrorez.myspace.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class ViewUserMoveInActivity extends AppCompatActivity {
    private ImageView profileImage;
    private RecyclerView recyclerView, itemRecycler;
    private MoveInImageAdapter mAdapter;
    private MoveInItemAdapter itemsAdapter;
    private TextView textCity, textResidence, textRoomNo, textDate;
    private User user;
    MoveIn moveIn;
    Toolbar mToolbar;
    ActionBar actionBar;

    public static final String KEY_USER = "USER";
    public static final String KEY_DATA = "DATA";
    public static final String KEY_DATE = "DATE";

    public static void navigate(AppCompatActivity activity, View transitionImage, User obj, MoveIn moveIn) {
        Intent intent = new Intent(activity, ViewUserMoveInActivity.class);
        intent.putExtra(KEY_USER, obj);
        intent.putExtra(KEY_DATA, moveIn);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, KEY_USER);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AdminTheme);
        setContentView(R.layout.activity_view_user_move_in);
        Intent intent = getIntent();
        user = (User) intent.getExtras().getSerializable(KEY_USER);
        moveIn = (MoveIn) intent.getExtras().getSerializable(KEY_DATA);
        initToolbar();
        setupUI();
        bindData();
        Tools.adminSystemBarLollipop(this);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setSubtitle(user.getUserCity());
        actionBar.setTitle(user.getUserFirstName() + " " + user.getUserLastName());
    }

    private void setupUI() {
        profileImage = findViewById(R.id.img_profile);
        recyclerView = findViewById(R.id.recyclerView);
        itemRecycler = findViewById(R.id.recyclerItems);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setPadding(2, 2, 2, 2);
        recyclerView.setHasFixedSize(true);
        textCity = findViewById(R.id.text_city_name);
        textResidence = findViewById(R.id.text_residence_name);
        textRoomNo = findViewById(R.id.text_room_no);
        textDate = findViewById(R.id.date);
    }

    private void bindData() {
        //actionBar.setTitle(user.getUserFirstName() + " " + user.getUserLastName());
        textCity.setText(user.getUserCity());
        textResidence.setText(user.getUserResidence());
        textRoomNo.setText(user.getUserRoom());
        textDate.setText(moveIn.getDate());
        itemRecycler.setLayoutManager(new GridLayoutManager(this, Tools.getGridMoveInCount(this)));
        itemRecycler.setHasFixedSize(true);
        itemRecycler.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new MoveInImageAdapter(this, getImages());
        recyclerView.setAdapter(mAdapter);

        itemsAdapter = new MoveInItemAdapter(this, moveIn.getItemList());
        itemRecycler.setAdapter(itemsAdapter);
    }

    private List<Image> getImages() {
        List<Image> images = new ArrayList<>();
        for (int i = 0; i < moveIn.getUrls().size(); i++) {
            Image image = new Image(moveIn.getUrls().get(i));
            images.add(image);
        }
        return images;
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

    @Override
    protected void onStop() {
        super.onStop();
    }
}
