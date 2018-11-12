package com.metrorez.myspace.user.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.adapter.NotificationListAdapter;
import com.metrorez.myspace.user.data.Constants;
import com.metrorez.myspace.user.model.Notification;

import java.util.ArrayList;
import java.util.List;


public class NotificationsFragment extends Fragment {


    private RecyclerView recyclerView;
    private View view;
    private NotificationListAdapter mAdapter;
    private List<Notification> notifications = new ArrayList<>();
    private String userId;
    private FirebaseAuth mAuth;
    private LinearLayout lyt_not_found;
    private DatabaseReference noticationReference = FirebaseDatabase.getInstance().getReference("notifications");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notifications, container, false);
        setHasOptionsMenu(true);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        lyt_not_found = view.findViewById(R.id.lyt_not_found);
        getNotifications();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new NotificationListAdapter(getActivity(), notifications);
        recyclerView.setAdapter(mAdapter);
        //set data and list adapter

        return view;
    }

    private void getNotifications() {

        Query query = noticationReference.orderByChild("fromUserId").equalTo(userId);
        query.addValueEventListener(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            notifications.clear();
            for (DataSnapshot notifSnapShot : dataSnapshot.getChildren()) {
                Notification notification = notifSnapShot.getValue(Notification.class);
                notifications.add(notification);
            }
            mAdapter.notifyDataSetChanged();

            if (notifications.size() == 0) {
                lyt_not_found.setVisibility(View.VISIBLE);
            } else {
                lyt_not_found.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_notif, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                recyclerView.refreshDrawableState();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
