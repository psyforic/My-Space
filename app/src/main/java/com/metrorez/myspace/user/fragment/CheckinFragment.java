package com.metrorez.myspace.user.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.metrorez.myspace.user.CheckinActivity;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.adapter.CheckinListAdapter;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.Checkin;

import java.util.ArrayList;
import java.util.List;

public class CheckinFragment extends Fragment {

    FloatingActionButton addCheckin;
    private RecyclerView recyclerView;
    public CheckinListAdapter mAdapter;
    private ProgressBar progressBar;
    private View view;
    private List<Checkin> checkins;
    private LinearLayout lyt_not_found;
    private FirebaseAuth mAuth;

    private DatabaseReference checkinReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_checkin, container, false);
        mAuth = FirebaseAuth.getInstance();
        checkinReference = FirebaseDatabase.getInstance().getReference().child("checkins");
        getCheckins();
        setupUI();

        addCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CheckinActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //getCheckins();
    }

    private void setupUI() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        lyt_not_found = (LinearLayout) view.findViewById(R.id.lyt_not_found);
        addCheckin = view.findViewById(R.id.fab_add_checkin);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), Tools.getGridSpanCount(getActivity()));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);


    }

    private void getCheckins() {
        checkins = new ArrayList<>();
        if (checkinReference != null) {
            checkinReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    checkins.clear();
                    for (DataSnapshot checkinSnapShot : dataSnapshot.getChildren()) {
                        Checkin checkin = checkinSnapShot.getValue(Checkin.class);
                        checkins.add(checkin);
                    }

                    mAdapter = new CheckinListAdapter(getActivity(), checkins);
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();


                    if (checkins.size() == 0) {
                        lyt_not_found.setVisibility(View.VISIBLE);
                    } else {
                        lyt_not_found.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onResume() {
        // mAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onStart() {
        //getCheckins();
        super.onStart();

    }
}
