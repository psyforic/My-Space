package com.metrorez.myspace.user.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.model.Extra;
import com.metrorez.myspace.user.model.Request;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public static  final String ADD_FRAGMENT="ADD_FRAGMENT";
    RecyclerView recyclerView;
    //public HomeGridAdapter mAdater;
    private View view;
    private TextView txtComplaints, txtRequests;
    private FirebaseAuth mAuth;
    private Button requestBtn, complaintBtn;
    private DatabaseReference complaintsReference = FirebaseDatabase.getInstance().getReference("complaints");
    private DatabaseReference extrasReference = FirebaseDatabase.getInstance().getReference("extras");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        mAuth = FirebaseAuth.getInstance();
        bindData();
        complaintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddComplaintFragment fragment = new AddComplaintFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_content, fragment, ADD_FRAGMENT)
                        .addToBackStack(ADD_FRAGMENT)
                        .commit();
            }
        });
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return view;
    }

    private void bindData() {
        txtComplaints = view.findViewById(R.id.no_complaints);
        txtRequests = view.findViewById(R.id.no_requests);
        complaintBtn = view.findViewById(R.id.btn_file_complaint);
        requestBtn = view.findViewById(R.id.btn_request);
        final List<Extra> extras = new ArrayList<>();
        final int[] complaintsCount = new int[1];
        //final int[] requestsCount = new int[1];
        complaintsReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                complaintsCount[0] = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        extrasReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Request request = snapshot.getValue(Request.class);
                    extras.addAll(request != null ? request.getExtras() : null);
                }

                txtRequests.setText(getString(R.string.items_requested, String.valueOf(extras.size())));
                txtComplaints.setText(getString(R.string.filed_complaints, String.valueOf(complaintsCount[0])));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
