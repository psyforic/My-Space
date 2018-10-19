package com.metrorez.myspace.user.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.model.Complaint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddComplaintFragment extends Fragment {

    DatabaseReference databaseReference;
    private Spinner residences, cities, priority;
    private EditText roomNo, editTextComplaint;
    private Button fileComplaint;
    private CheckBox attachImage;
    private ImageButton cameraButton;
    private boolean checked = false;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_complaint, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("complaints");
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
        cities = (Spinner) view.findViewById(R.id.spinner_city);
        roomNo = (EditText) view.findViewById(R.id.txtRoomName);
        editTextComplaint = (EditText) view.findViewById(R.id.txtComment);
        priority = (Spinner) view.findViewById(R.id.spinner_priority);
        fileComplaint = (Button) view.findViewById(R.id.btn_send_comment);
        attachImage = (CheckBox) view.findViewById(R.id.checkbox_attach);
        cameraButton = (ImageButton) view.findViewById(R.id.camera_btn);
    }

    private void addComplaint() {
        String residence = residences.getSelectedItem().toString();
        String city = cities.getSelectedItem().toString();
        String room = roomNo.getText().toString().trim();
        String category = priority.getSelectedItem().toString();
        String complainTxt = editTextComplaint.getText().toString().trim();
        String id = databaseReference.push().getKey();


        if (validateRoom() && validateComplaint()) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date today = Calendar.getInstance().getTime();
            String date = dateFormat.format(today);
            Complaint complaint = new Complaint(id, category, complainTxt, date, city, residence, room);

            databaseReference.child(id).setValue(complaint);

            Toast.makeText(getActivity(), "Complaint Filed Successfully", Toast.LENGTH_LONG).show();
        } else {
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

}
