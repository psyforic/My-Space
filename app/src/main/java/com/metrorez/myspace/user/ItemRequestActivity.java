package com.metrorez.myspace.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.adapter.ExtraListAdapter;
import com.metrorez.myspace.user.data.Constants;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.Extra;
import com.metrorez.myspace.user.model.Notification;
import com.metrorez.myspace.user.model.Request;
import com.metrorez.myspace.user.model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ItemRequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExtraListAdapter mAdapter;
    private List<Extra> extras;
    private List<Extra> selected;
    private Button btnRequest;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private String userRoom = "";
    private String residenceName = "";
    private String userCity = "";
    private DatabaseReference extrasReference = FirebaseDatabase.getInstance().getReference().child("extras");
    private DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");
    private DatabaseReference notificationsReference;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_request);
        initToolbar();
        mAuth = FirebaseAuth.getInstance();
        notificationsReference = FirebaseDatabase.getInstance().getReference().child("notifications");
        getUserInfo();
        setData();
        setupUI();
        requestExtras();

        Tools.systemBarLolipop(this);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Request Item(s)");
    }

    private void setupUI() {
        selected = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        progressBar = findViewById(R.id.progressBar);
        mAdapter = new ExtraListAdapter(extras, new ExtraListAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(Extra item) {
                selected.add(item);
            }

            @Override
            public void onItemUncheck(Extra item) {
                selected.remove(item);
            }
        });
        recyclerView.setAdapter(mAdapter);
        btnRequest = findViewById(R.id.btn_request);
    }

    private void requestExtras() {

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateResidence() && validateRoomNo()) {
                    if (selected.size() != 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ItemRequestActivity.this);
                        // LayoutInflater inflater = getActivity().getLayoutInflater();

                        builder.setMessage(R.string.request_message)
                                .setTitle(R.string.request_title);
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                addRequest();
                                Toast.makeText(ItemRequestActivity.this, R.string.request_success, Toast.LENGTH_LONG).show();
                            }
                        });
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        Snackbar.make(view, getString(R.string.nothing_selected), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    showDialog();
                }
            }

        });

    }

    private void setData() {
        extras = new ArrayList<>();
        extras.add(new Extra("Couch", 0.00, false));
        extras.add(new Extra("Fridge", 650.00, false));
        extras.add(new Extra("Table", 0.00, false));
        extras.add(new Extra("Extra Lamp", 0.00, false));
        extras.add(new Extra("Ottoman", 99.00, false));
    }

    private void addRequest() {
        String userId = mAuth.getCurrentUser().getUid();
        String id = extrasReference.push().getKey();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String date = dateFormat.format(today);
        Request newRequest = new Request(id, date, userId, selected, userCity, residenceName, userRoom, userName);
        extrasReference.child(userId).child(id).setValue(newRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                sendNotification("Request", Constants.REQUEST_TYPE);
                progressBar.setVisibility(View.GONE);

                Intent intent = new Intent(ItemRequestActivity.this, SuccessActivity.class);
                intent.putExtra(Constants.STRING_EXTRA, getString(R.string.str_extra_message));
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void sendNotification(String content, String type) {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String date = dateFormat.format(today);
        String id = notificationsReference.push().getKey();
        String typeId = extrasReference.push().getKey();
        String userId = mAuth.getCurrentUser().getUid();
        Notification notification = new Notification(id, mAuth.getCurrentUser().getUid(), date, content, mAuth.getCurrentUser().getDisplayName(), type, typeId, userId, Constants.ADMIN_USER_ID,
                false);
        notificationsReference.child(id).setValue(notification);
    }

    private void getUserInfo() {
        usersReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                residenceName = user != null ? user.getUserResidence() : null;
                userRoom = user != null ? user.getUserRoom() : null;
                userCity = user != null ? user.getUserCity() : null;
                userName = user != null ? user.getUserFirstName() + " " + user.getUserLastName() : null;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean validateResidence() {
        if (residenceName == null) {
            return false;
        }
        return true;
    }

    private boolean validateRoomNo() {
        if (userRoom == null) {
            return false;
        }
        return true;
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setMessage(R.string.error_message)
                .setTitle(R.string.update_profile).setIcon(R.drawable.ic_error);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(ItemRequestActivity.this, ProfileActivity.class));
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
