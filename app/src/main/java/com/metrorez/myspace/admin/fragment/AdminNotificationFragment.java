package com.metrorez.myspace.admin.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.adapter.AdminNotificationListAdapter;
import com.metrorez.myspace.user.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class AdminNotificationFragment extends Fragment {


    private RecyclerView recyclerView;
    private View view;
    private AdminNotificationListAdapter mAdapter;
    private List<Notification> notifications = new ArrayList<>();
    //private String userId;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    private DatabaseReference noticationReference = FirebaseDatabase.getInstance().getReference().child("notifications");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTheme(R.style.AdminTheme);
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_admin_notification, container, false);
        setHasOptionsMenu(true);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        //userId = mAuth.getCurrentUser().getUid();
        lyt_not_found = view.findViewById(R.id.lyt_not_found);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new AdminNotificationListAdapter(getActivity(), notifications);
        getNotifications();
        return view;
    }

    private void getNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        noticationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notifications.clear();
                for (DataSnapshot notifSnapShot : dataSnapshot.getChildren()) {
                    Iterable<DataSnapshot> children = notifSnapShot.getChildren();
                    for (DataSnapshot snapshot : children) {
                        Notification notification = snapshot.getValue(Notification.class);
                        notifications.add(notification);
                    }

                }
                recyclerView.setAdapter(mAdapter);
                progressBar.setVisibility(View.GONE);
                if (notifications.size() == 0) {
                    lyt_not_found.setVisibility(View.VISIBLE);
                } else {
                    lyt_not_found.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_notif, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                mAdapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
