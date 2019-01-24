package com.metrorez.myspace.user.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.activity.GymAccessActivity;
import com.metrorez.myspace.user.activity.ItemRequestActivity;
import com.metrorez.myspace.user.activity.JobRequestActivity;
import com.metrorez.myspace.user.activity.SleepoverRequestActivity;
import com.metrorez.myspace.user.adapter.MyExtrasListAdapter;
import com.metrorez.myspace.user.model.Extra;
import com.metrorez.myspace.user.model.Request;

import java.util.ArrayList;
import java.util.List;

public class ExtrasFragment extends Fragment {
    FloatingActionButton addExtra;
    private RecyclerView recyclerView;
    private MyExtrasListAdapter mAdapter;
    private View view;
    private List<Extra> extras;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    private Fragment fragment = null;
    private FirebaseAuth mAuth;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private DatabaseReference extrasReference = FirebaseDatabase.getInstance().getReference().child("extras");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_extras, container, false);
        mAuth = FirebaseAuth.getInstance();
        setupUI();

        addExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopup();
            }
        });
        return view;
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

    private void setupUI() {
        extras = new ArrayList<>();
        progressBar = view.findViewById(R.id.progressBar);
        lyt_not_found = view.findViewById(R.id.lyt_not_found);
        recyclerView = (RecyclerView) view.findViewById(R.id.extras_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        addExtra = (FloatingActionButton) view.findViewById(R.id.fab_add_extra);

        progressBar.setVisibility(View.VISIBLE);
        extrasReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                extras.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot extrasSnapShot : dataSnapshot.getChildren()) {
                        Request request = extrasSnapShot.getValue(Request.class);

                        for (Extra extra : request.getExtras()) {
                            extras.add(extra);
                        }
                    }
                    mAdapter = new MyExtrasListAdapter(getActivity(), extras);
                    recyclerView.setAdapter(mAdapter);
                    progressBar.setVisibility(View.GONE);
                    if (extras.size() == 0) {
                        lyt_not_found.setVisibility(View.VISIBLE);
                    } else {
                        lyt_not_found.setVisibility(View.GONE);
                    }
                } else {
                    if (extras.size() == 0) {
                        lyt_not_found.setVisibility(View.VISIBLE);
                    } else {
                        lyt_not_found.setVisibility(View.GONE);
                    }
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}
