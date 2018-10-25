package com.metrorez.myspace.user.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.adapter.ComplaintListAdapter;
import com.metrorez.myspace.user.data.GlobalVariable;
import com.metrorez.myspace.user.model.Complaint;

import java.util.ArrayList;
import java.util.List;

public class ComplaintFragment extends Fragment {

    FloatingActionButton addComplaint;
    private RecyclerView recyclerView;
    private ComplaintListAdapter mAdapter;
    private View view;
    private List<Complaint> complaints;
    private Fragment fragment = null;
    private ProgressBar progressBar;


    private GlobalVariable global;
    DatabaseReference complaintsReference = FirebaseDatabase.getInstance().getReference("complaints");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_complaint, container, false);
        global = (GlobalVariable) getActivity().getApplication();
        setupUI();

        addComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new AddComplaintFragment();
                Bundle bundle = new Bundle();
                fragment.setArguments(bundle);
                if (fragment != null) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_content, fragment);
                    fragmentTransaction.commit();

                }
            }
        });

        mAdapter.setOnDeleteButtonClickListener(new ComplaintListAdapter.OnDeleteButtonClickListener() {
            @Override
            public void onItemClick(View view, Complaint obj, int position) {

            }
        });
        return view;
    }

    private void setupUI() {
        complaints = new ArrayList<>();
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) view.findViewById(R.id.complaint_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new ComplaintListAdapter(getActivity(), complaints);
        addComplaint = (FloatingActionButton) view.findViewById(R.id.fab_add_complaint);

        complaintsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                complaints.clear();
                progressBar.setVisibility(View.VISIBLE);
                for (DataSnapshot complaintSnapShot : dataSnapshot.getChildren()) {
                    Complaint complaint = complaintSnapShot.getValue(Complaint.class);
                    complaints.add(complaint);
                }
                global.setComplaints(complaints);
                recyclerView.setAdapter(mAdapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
