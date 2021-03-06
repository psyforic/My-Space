package com.metrorez.myspace.user.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.activity.MainActivity;
import com.metrorez.myspace.user.activity.ProfileActivity;
import com.metrorez.myspace.user.model.User;

public class ProfileFragment extends Fragment {
    private TextView name, lastname, email, studentNo, residence, residenceRoom, userCell;
    private View view;
    private Button editButton;
    private TextView editTxt;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        setupUI();
        loadExtraInfo();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileActivity.navigate((MainActivity) getActivity());
            }
        });
        editTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileActivity.navigate((MainActivity) getActivity());
            }
        });
        return view;
    }

    private void setupUI() {
        editButton = view.findViewById(R.id.btn_edit);
        name = view.findViewById(R.id.name);
        lastname = view.findViewById(R.id.surname);
        email = view.findViewById(R.id.email);
        editTxt = view.findViewById(R.id.edit_txt);
        studentNo = view.findViewById(R.id.description2);
        residence = view.findViewById(R.id.residence);
        residenceRoom = view.findViewById(R.id.residence_room);
        userCell = view.findViewById(R.id.userCell);
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
                residence.setText(user.getUserResidence());
                residenceRoom.setText(user.getUserRoom());
                if (user.getUserCellphone() != null) {
                    userCell.setText(user.getUserCellphone());
                } else {
                    userCell.setText(" ");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
