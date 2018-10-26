package com.metrorez.myspace.user.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import com.metrorez.myspace.user.model.Complaint;
import com.metrorez.myspace.user.model.Notification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddComplaintFragment extends Fragment {

    DatabaseReference databaseReference;
    private Spinner residences, cities, priority;
    private EditText roomNo, editTextComplaint;
    private Button fileComplaint;
    private CheckBox attachImage;
    private ImageButton cameraButton;
    private boolean checked = false;
    private FirebaseAuth mAuth;
    private View view;
    private ProgressBar progressBar;
    private DatabaseReference notificationsReference;
    private DatabaseReference roleReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_complaint, container, false);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("complaints");
        notificationsReference = FirebaseDatabase.getInstance().getReference().child("notifications");
        roleReference = FirebaseDatabase.getInstance().getReference().child("roles");
        setupUI();

        fileComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComplaint();
            }
        });
        attachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraButton.setVisibility(View.GONE);
                checked = true;
                if (checked) {
                    cameraButton.setVisibility(View.VISIBLE);
                    checked = false;
                }


            }
        });
        attachImage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    cameraButton.setVisibility(View.VISIBLE);
                }
                // cameraButton.setVisibility(View.GONE);
            }
        });
        return view;

    }

    private void setupUI() {
        residences = (Spinner) view.findViewById(R.id.spinner_residence);


        final List<String> residenceList = new ArrayList<>(Arrays.asList(getActivity().getResources().getStringArray(R.array.residences)));
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, residenceList) {
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
                View spinnerView = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) spinnerView;
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return spinnerView;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        residences.setAdapter(spinnerArrayAdapter);

        cities = (Spinner) view.findViewById(R.id.spinner_city);

        final List<String> cityList = new ArrayList<>(Arrays.asList(getActivity().getResources().getStringArray(R.array.city)));
        final ArrayAdapter<String> cityArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cityList) {
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
                View spinnerCityView = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) spinnerCityView;
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return spinnerCityView;
            }
        };

        cityArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cities.setAdapter(cityArrayAdapter);


        roomNo = (EditText) view.findViewById(R.id.txtRoomName);
        editTextComplaint = (EditText) view.findViewById(R.id.txtComment);
        priority = (Spinner) view.findViewById(R.id.spinner_priority);


        final List<String> priorityList = new ArrayList<>(Arrays.asList(getActivity().getResources().getStringArray(R.array.priorities)));
        final ArrayAdapter<String> priorityArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, priorityList) {
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
                View spinnerPriorityView = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) spinnerPriorityView;
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return spinnerPriorityView;
            }
        };
        priorityArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priority.setAdapter(priorityArrayAdapter);


        fileComplaint = (Button) view.findViewById(R.id.btn_send_comment);
        attachImage = (CheckBox) view.findViewById(R.id.checkbox_attach);
        cameraButton = (ImageButton) view.findViewById(R.id.camera_btn);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void addComplaint() {
        String residence = residences.getSelectedItem().toString();
        String city = cities.getSelectedItem().toString();
        String room = roomNo.getText().toString().trim();
        String category = priority.getSelectedItem().toString();
        String complainTxt = editTextComplaint.getText().toString().trim();
        String id = databaseReference.push().getKey();
        String userId = mAuth.getCurrentUser().getUid();


        if (validateRoom() && validateComplaint()) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date today = Calendar.getInstance().getTime();
            String date = dateFormat.format(today);
            Complaint complaint = new Complaint(id, category, complainTxt, date, city, residence, room);
            progressBar.setVisibility(View.VISIBLE);
            databaseReference.child(userId).child(id).setValue(complaint).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressBar.setVisibility(View.GONE);
                    sendNotification("Complaint", Constants.COMPLAINT_TYPE);
                    progressBar.setVisibility(View.GONE);

                    Intent intent = new Intent(getActivity(), SuccessActivity.class);
                    intent.putExtra(Constants.STRING_EXTRA, getString(R.string.str_complaint_message));
                    startActivity(intent);
                }
            });


        } else {
            progressBar.setVisibility(View.GONE);
            Snackbar.make(view, "Please Review the Errors", Snackbar.LENGTH_LONG).show();
        }
    }

    private boolean validateRoom() {
        if (roomNo.getText().toString().trim().isEmpty()) {
            //inputLayoutName.setError(getString(R.string.err_msg_name));
            Snackbar.make(view, getString(R.string.err_msg_room), Snackbar.LENGTH_SHORT).show();
            requestFocus(roomNo);
            roomNo.setError("this field cannot be empty");
            return false;
        }
        return true;
    }

    private boolean validateComplaint() {
        if (editTextComplaint.getText().toString().trim().isEmpty()) {
            //inputLayoutName.setError(getString(R.string.err_msg_name));
            Snackbar.make(view, getString(R.string.err_msg_complaint), Snackbar.LENGTH_SHORT).show();
            requestFocus(editTextComplaint);
            editTextComplaint.setError("this field cannot be empty");
            return false;
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void sendNotification(String content, String type) {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String date = dateFormat.format(today);
        String id = notificationsReference.push().getKey();
        String typeId = databaseReference.push().getKey();
        String userId = mAuth.getCurrentUser().getUid();
        Notification notification = new Notification(userId, id, mAuth.getCurrentUser().getUid(), date, content, mAuth.getCurrentUser().getDisplayName(), type, typeId);
        notificationsReference.child(userId).child(id).setValue(notification);
    }

}
