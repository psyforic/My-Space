package com.metrorez.myspace.user;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.data.Tools;

public class JobRequestActivity extends AppCompatActivity {
    private Button submitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_request);
        initToolbar();
        Tools.systemBarLolipop(this);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Request Gym Access");
    }

    private void setupUI() {

    }
}
