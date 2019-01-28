package com.metrorez.myspace.user.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.data.Constants;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.Extra;
import com.metrorez.myspace.user.model.Notification;
import com.metrorez.myspace.user.model.Request;
import com.metrorez.myspace.user.model.User;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class JobRequestActivity extends AppCompatActivity {
    private Button submitButton;
    private EditText jobDescription;
    private Spinner jobType;
    private View parentView;
    private String userResidence, userRoom, userCity, userName, userEmail;
    private FirebaseAuth mAuth;
    private DatabaseReference extrasReference = FirebaseDatabase.getInstance().getReference().child("extras");
    private DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");
    private DatabaseReference notificationsReference = FirebaseDatabase.getInstance().getReference().child("notifications");

    private static final String SENDGRID_USERNAME = "myspaceMailer";
    private static final String SENDGRID_PASSWORD = "#MySpace@2018";
    private static final String TAG = "JobRequest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_request);
        initToolbar();
        mAuth = FirebaseAuth.getInstance();
        getUserInfo();
        setupUI();
        submit();
        Tools.systemBarLolipop(this);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Job Request");
    }

    private void setupUI() {
        jobDescription = findViewById(R.id.input_comment);
        jobType = findViewById(R.id.jobSpinner);
        submitButton = findViewById(R.id.btn_request);
        parentView = findViewById(android.R.id.content);
    }

    private void requestJob() {
        if (validateDescription() && validateSpinner()) {
            String id = extrasReference.push().getKey();
            List<Extra> extra = new ArrayList<>();
            extra.add(new Extra("Job Request", 0.0, true));
            Request request = new Request(id, Constants.getToday(), mAuth.getCurrentUser().getUid(), extra, userCity, userResidence, userRoom, userName);

            extrasReference.child(mAuth.getCurrentUser().getUid()).child(id).setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    sendNotification("Job Request..." + "\n" +
                            jobDescription.getText().toString(), Constants.REQUEST_TYPE);
                    sendEmail();
                    Intent intent = new Intent(JobRequestActivity.this, SuccessActivity.class);
                    intent.putExtra(Constants.STRING_EXTRA, getString(R.string.str_extra_message));
                    startActivity(intent);
                }
            });

        }

    }

    private void sendNotification(String content, String type) {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String date = dateFormat.format(today);
        String id = notificationsReference.push().getKey();
        String typeId = extrasReference.push().getKey();
        String userId = mAuth.getCurrentUser().getUid();
        Notification notification = new Notification(id, mAuth.getCurrentUser().getUid(), date, content, mAuth.getCurrentUser().getDisplayName(), type, typeId, userId, Constants.ADMIN_USER_ID, false);
        notificationsReference.child(id).setValue(notification);
    }

    private boolean validateSpinner() {
        if (!jobDescription.getText().toString().isEmpty()) {
            return true;
        } else {
            jobDescription.setError("Job Description Cannot Be Empty");
            return false;
        }
    }

    private boolean validateDescription() {

        if (jobType.getSelectedItemPosition() != 0) {
            return true;
        } else {
            Snackbar.make(parentView, "Please select a valid job type", Snackbar.LENGTH_SHORT).show();
            return false;
        }

    }

    private void submit() {

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateDescription() && validateSpinner()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JobRequestActivity.this);
                    // LayoutInflater inflater = getActivity().getLayoutInflater();

                    builder.setMessage(R.string.request_job_request_message)
                            .setTitle(R.string.request_gym_title);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            requestJob();
                            Toast.makeText(JobRequestActivity.this, R.string.request_success, Toast.LENGTH_LONG).show();
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
        });
    }

    private void getUserInfo() {
        usersReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userResidence = user != null ? user.getUserResidence() : "";
                userRoom = user != null ? user.getUserRoom() : "";
                userCity = user != null ? user.getUserCity() : "";
                userName = user != null ? user.getUserFirstName() + " " + user.getUserLastName() : null;
                userEmail = user != null ? user.getUserEmail() : null;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendEmail() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        if (validateDescription() && validateSpinner()) {
            String toEmail = Constants.SENDGRID_TO_EMAIL;
            String subject = Constants.JOB_EMAIL_SUBJECT;
            String type = jobType.getSelectedItem().toString();
            String date = dateFormat.format(today);
            String messageBody = getString(R.string.str_job_request_msg_body, userName, type, userCity, userResidence,
                    userRoom, date);
            SendEmailAsyncTask task = new SendEmailAsyncTask(this, toEmail, userEmail, subject, messageBody);
            task.execute();
        }
    }

    private static class SendEmailAsyncTask extends AsyncTask<Void, Void, Void> {
        Context mContext;
        private String mMsgResponse;

        private String mTo;
        private String mFrom;
        private String mSubject;
        private String mBody;

        public SendEmailAsyncTask(Context context, String to, String from, String subject, String body) {
            mContext = context.getApplicationContext();
            mTo = to;
            mFrom = from;
            mSubject = subject;
            mBody = body;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                SendGrid sendGrid = new SendGrid(SENDGRID_USERNAME, SENDGRID_PASSWORD);
                SendGrid.Email email = new SendGrid.Email();

                email.addTo(mTo);
                email.setFrom(mFrom);
                email.setSubject(mSubject);
                email.setText(mBody);

                SendGrid.Response response = sendGrid.send(email);
                mMsgResponse = response.getMessage();

                Log.d(TAG, mMsgResponse);
            } catch (SendGridException e) {

                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
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
