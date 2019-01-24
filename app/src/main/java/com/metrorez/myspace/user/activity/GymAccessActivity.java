package com.metrorez.myspace.user.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GymAccessActivity extends AppCompatActivity {

    private Spinner spinnerGender, spinnerBranch;
    private EditText editEmail, editIdNumber;
    private ProgressBar progressBar;
    private TextInputLayout inputLayoutEmail, inputLayoutIdNo;
    private Button btnRequest;
    private String userResidence, userRoom, userCity, userName;
    private DatabaseReference extrasReference = FirebaseDatabase.getInstance().getReference().child("extras");
    private DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");
    private DatabaseReference notificationsReference = FirebaseDatabase.getInstance().getReference().child("notifications");
    private FirebaseAuth mAuth;

    private static final String SENDGRID_USERNAME = "myspaceMailer";
    private static final String SENDGRID_PASSWORD = "#MySpace@2018";
    private static final String TAG = "GymAccess";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_access);
        initToolbar();

        mAuth = FirebaseAuth.getInstance();
        setupUI();
        setupDropdowns();
        getUserInfo();
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateEmail() && validateIdNumber()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GymAccessActivity.this);
                    // LayoutInflater inflater = getActivity().getLayoutInflater();

                    builder.setMessage(R.string.request_gym_message)
                            .setTitle(R.string.request_gym_title);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            requestAccess();
                            Toast.makeText(GymAccessActivity.this, R.string.request_success, Toast.LENGTH_LONG).show();
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
                    Snackbar.make(view, "Fill all the required information", Snackbar.LENGTH_LONG).show();
                }
            }
        });
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
        editEmail = findViewById(R.id.input_email);
        progressBar = findViewById(R.id.progressBar);
        editIdNumber = findViewById(R.id.input_idNo);
        spinnerBranch = findViewById(R.id.spinner_branch);
        spinnerGender = findViewById(R.id.spinner_gender);
        inputLayoutEmail = findViewById(R.id.input_layout_email);
        editEmail.addTextChangedListener(new MyTextWatcher(editEmail));
        inputLayoutIdNo = findViewById(R.id.input_layout_idNo);
        btnRequest = findViewById(R.id.btn_request);
    }

    private void requestAccess() {
        if (validateEmail() && validateIdNumber()) {
            String id = extrasReference.push().getKey();
            List<Extra> extra = new ArrayList<>();
            extra.add(new Extra("Gym Access", 1000, true));
            Request request = new Request(id, Constants.getToday(), mAuth.getCurrentUser().getUid(), extra, userCity, userResidence, userRoom, userName);

            // TODO: Fix later
            extrasReference.child(mAuth.getCurrentUser().getUid()).child(id).setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    sendNotification("Request", Constants.REQUEST_TYPE);
                    sendEmail();
                    Intent intent = new Intent(GymAccessActivity.this, SuccessActivity.class);
                    intent.putExtra(Constants.STRING_EXTRA, getString(R.string.str_extra_message));
                    startActivity(intent);
                }
            });

        }

    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static boolean isValidIdNumber(String idNo) {
        return !TextUtils.isEmpty(idNo) && idNo.length() >= 9;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateEmail() {
        String email = editEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(editEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateIdNumber() {
        String idNumber = editIdNumber.getText().toString().trim();

        if (idNumber.isEmpty() || !isValidIdNumber(idNumber)) {
            inputLayoutIdNo.setError(getString(R.string.err_msg_id_no));
            requestFocus(editIdNumber);
            return false;
        } else {
            inputLayoutIdNo.setErrorEnabled(false);
        }

        return true;
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_email:
                    validateEmail();
                    break;
            }
        }
    }

    private void setupDropdowns() {
        /**
         * gender dropdown
         */
        final List<String> genderList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.gender)));
        final ArrayAdapter<String> genderArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View spinnerGenderView = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) spinnerGenderView;
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return spinnerGenderView;
            }
        };
        genderArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerGender.setAdapter(genderArrayAdapter);


        /**
         * Branch Dropdown
         */
        final List<String> branchList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.branch)));
        final ArrayAdapter<String> branchArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, branchList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View spinnerbranchView = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) spinnerbranchView;
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return spinnerbranchView;
            }
        };
        genderArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerBranch.setAdapter(branchArrayAdapter);

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

    private void getUserInfo() {
        usersReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userResidence = user != null ? user.getUserResidence() : "";
                userRoom = user != null ? user.getUserRoom() : "";
                userCity = user != null ? user.getUserCity() : "";
                userName = user != null ? user.getUserFirstName() + " " + user.getUserLastName() : null;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendEmail() {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        if (validateEmail() && validateIdNumber()) {
            String toEmail = Constants.SENDGRID_TO_EMAIL;
            String fromMail = editEmail.getText().toString();
            String subject = Constants.EMAIL_SUBJECT;
            String gender = spinnerGender.getSelectedItem().toString();
            String idNumber = editIdNumber.getText().toString();
            String branch = spinnerBranch.getSelectedItem().toString();
            String date = dateFormat.format(today);
            String messageBody = getString(R.string.str_message_body, userName, gender,
                    idNumber, fromMail, branch, userCity, userResidence, userRoom, date);
            SendEmailAsyncTask task = new SendEmailAsyncTask(this, toEmail, fromMail, subject, messageBody);
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
