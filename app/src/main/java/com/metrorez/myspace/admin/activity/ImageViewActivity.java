package com.metrorez.myspace.admin.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.data.Tools;
import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppCompatActivity {
    private ImageView image;
    private Toolbar mToolbar;
    private String imgUrl;
    private static final String IMG_URL = "IMG_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AdminTheme);
        setContentView(R.layout.activity_image_view);
        initToolbar();
        Intent intent = getIntent();
        imgUrl = intent.getStringExtra(IMG_URL);
        setupImage();
        Tools.adminSystemBarLollipop(this);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("View Image");
    }

    private void setupImage() {
        image = findViewById(R.id.image);
        image.setOnTouchListener(new ImageMatrixTouchHandler(this));
        Picasso.with(this).load(imgUrl)
                .placeholder(R.drawable.ic_loader)
                .fit()
                .into(image);
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
