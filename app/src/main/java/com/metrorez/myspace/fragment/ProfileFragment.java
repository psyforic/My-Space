package com.metrorez.myspace.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.model.User;

public class ProfileFragment extends Fragment {
    private TextView name, lastname, email, studentNo;
    private View view;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        setupUI();
        loadExtraInfo();
        return view;
    }

    private void setupUI() {
        name = view.findViewById(R.id.name);
        lastname = view.findViewById(R.id.surname);
        email = view.findViewById(R.id.email);
        studentNo = view.findViewById(R.id.description2);
    }

    private void loadExtraInfo() {

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getUserFirstName());
                lastname.setText(user.getUserLastName());
                email.setText(user.getUserEmail());
                studentNo.setText(user.getUserStudentNo());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
