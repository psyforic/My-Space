package com.metrorez.myspace.admin.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.metrorez.myspace.R;

public class UsersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AdminTheme);
        setContentView(R.layout.activity_users);
    }
}
