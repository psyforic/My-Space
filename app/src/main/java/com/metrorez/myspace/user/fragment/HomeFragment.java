package com.metrorez.myspace.user.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.AddComplaintActivity;
import com.metrorez.myspace.user.GymAccessActivity;
import com.metrorez.myspace.user.ItemRequestActivity;
import com.metrorez.myspace.user.JobRequestActivity;
import com.metrorez.myspace.user.SleepoverRequestActivity;
import com.metrorez.myspace.user.model.Extra;
import com.metrorez.myspace.user.model.Request;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public static final String ADD_FRAGMENT = "ADD_FRAGMENT";
    RecyclerView recyclerView;
    //public HomeGridAdapter mAdater;
    private View view;
    private TextView txtComplaints, txtRequests;
    private FirebaseAuth mAuth;
    private Button requestBtn, complaintBtn;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Fragment fragment = null;
    private DatabaseReference complaintsReference = FirebaseDatabase.getInstance().getReference("complaints");
    private DatabaseReference extrasReference = FirebaseDatabase.getInstance().getReference("extras");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        mAuth = FirebaseAuth.getInstance();
        bindData();
        buttonListeners();
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

                if (isAdded()) {
                    txtRequests.setText(getString(R.string.items_requested, String.valueOf(extras.size())));
                    txtComplaints.setText(getString(R.string.filed_complaints, String.valueOf(complaintsCount[0])));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void buttonListeners() {
        complaintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddComplaintActivity.class));
            }
        });
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopup();
            }
        });
    }

    private void createPopup() {
        dialogBuilder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.request_popup, null);
        LinearLayout addReq, addGym, addSleepover, addWeekendJob;
        addReq = view.findViewById(R.id.linear_req_extra);
        addGym = view.findViewById(R.id.linear_req_gym);
        addSleepover = view.findViewById(R.id.linear_req_sleepover);
        addWeekendJob = view.findViewById(R.id.linear_req_job);
        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        addReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), ItemRequestActivity.class));
                dialog.dismiss();
            }
        });
        addGym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), GymAccessActivity.class));
                dialog.dismiss();
            }
        });
        addSleepover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SleepoverRequestActivity.class));
                dialog.dismiss();
            }
        });
        addWeekendJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), JobRequestActivity.class));
                dialog.dismiss();
            }
        });

    }

}
