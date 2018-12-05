package com.metrorez.myspace.user.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.SuccessActivity;
import com.metrorez.myspace.user.data.Constants;
import com.metrorez.myspace.user.model.Extra;
import com.metrorez.myspace.user.model.Notification;
import com.metrorez.myspace.user.model.Request;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class GymAccessFragment extends Fragment {
    private View view;
    private Spinner spinnerGender, spinnerBranch;
    private EditText editEmail, editIdNumber;
    private List<Extra> extra;
    private TextInputLayout inputLayoutEmail, inputLayoutIdNo;
    private Button btnRequest;
    private DatabaseReference extrasReference = FirebaseDatabase.getInstance().getReference().child("extras");
    private DatabaseReference notificationsReference = FirebaseDatabase.getInstance().getReference().child("notifications");
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_gym_access, container, false);
        mAuth = FirebaseAuth.getInstance();
        setupUI();
        setupDropdowns();
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateEmail() && validateIdNumber()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    // LayoutInflater inflater = getActivity().getLayoutInflater();

                    builder.setMessage(R.string.request_message)
                            .setTitle(R.string.request_title);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            requestAccess();
                            Toast.makeText(getContext(), R.string.request_success, Toast.LENGTH_LONG).show();
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
        return view;
    }

    private void setupUI() {
        editEmail = view.findViewById(R.id.input_email);
        editIdNumber = view.findViewById(R.id.input_idNo);
        spinnerBranch = view.findViewById(R.id.spinner_branch);
        spinnerGender = view.findViewById(R.id.spinner_gender);
        inputLayoutEmail = view.findViewById(R.id.input_layout_email);
        editEmail.addTextChangedListener(new MyTextWatcher(editEmail));
        inputLayoutIdNo = view.findViewById(R.id.input_layout_idNo);
        btnRequest = view.findViewById(R.id.btn_request);
    }

    private void requestAccess() {
        if (validateEmail() && validateIdNumber()) {
            String id = extrasReference.push().getKey();
            extra = new ArrayList<>();
            extra.add(new Extra("Gym Access", 0.00, true));
            Request request = new Request(id, Constants.getToday(), mAuth.getCurrentUser().getUid(), extra, Constants.getUserCity(), Constants.getUserRoomNo());

            // TODO: Fix later
            extrasReference.child(mAuth.getCurrentUser().getUid()).child(id).setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    sendNotification("Request", Constants.REQUEST_TYPE);

                    Intent intent = new Intent(getActivity(), SuccessActivity.class);
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
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
        final List<String> genderList = new ArrayList<>(Arrays.asList(getActivity().getResources().getStringArray(R.array.gender)));
        final ArrayAdapter<String> genderArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, genderList) {
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
        final List<String> branchList = new ArrayList<>(Arrays.asList(getActivity().getResources().getStringArray(R.array.branch)));
        final ArrayAdapter<String> branchArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, branchList) {
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
        Notification notification = new Notification(userId, id, mAuth.getCurrentUser().getUid(), date, content, mAuth.getCurrentUser().getDisplayName(), type, typeId, false);
        notificationsReference.child(userId).child(id).setValue(notification);
    }
}
