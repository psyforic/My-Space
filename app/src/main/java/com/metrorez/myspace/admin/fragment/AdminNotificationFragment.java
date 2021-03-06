package com.metrorez.myspace.admin.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.activity.ResponseActivity;
import com.metrorez.myspace.admin.adapter.AdminNotificationListAdapter;
import com.metrorez.myspace.admin.data.Utils;
import com.metrorez.myspace.user.data.Constants;
import com.metrorez.myspace.user.model.MoveIn;
import com.metrorez.myspace.user.model.Complaint;
import com.metrorez.myspace.user.model.Extra;
import com.metrorez.myspace.user.model.MoveInItem;
import com.metrorez.myspace.user.model.Notification;
import com.metrorez.myspace.user.model.Request;
import com.metrorez.myspace.user.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminNotificationFragment extends Fragment {


    private RecyclerView recyclerView;
    private View view;
    public AdminNotificationListAdapter mAdapter;
    private List<Notification> notifications = new ArrayList<>();
    //private String userId;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    private Notification notification;
    private User fromUser;
    private StringBuilder snippet;
    private DatabaseReference noticationReference = FirebaseDatabase.getInstance().getReference().child("notifications");
    private DatabaseReference userRefence = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

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
        Collections.sort(notifications, new Utils.NotificationComparator());
        mAdapter = new AdminNotificationListAdapter(getActivity(), notifications);
        getNotifications();
        mAdapter.setOnItemClickListener(new AdminNotificationListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, final Notification obj, int position) {
                final List<User> user = new ArrayList<>();
                snippet = new StringBuilder();
                userRefence.child("users").child(obj.getFromUserId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        notification = obj;
                        User newUser = dataSnapshot.getValue(User.class);
                        user.add(newUser);
                        fromUser = newUser;
                        switch (obj.getType()) {
                            case Constants.COMPLAINT_TYPE:
                                reference.child("complaints").child(obj.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Complaint complaint = dataSnapshot.getValue(Complaint.class);
                                        snippet.append(complaint.getComplaintComment()).append("\n").append("PRIORITY: ").append(complaint.getComplaintCategory()).append("\n").append(complaint.getComplaintResidence()).append("\n").append("ROOM NO. : ").append(complaint.getComplaintRoom());
                                        ResponseActivity.navigate((AppCompatActivity) getActivity(), view.findViewById(R.id.lyt_parent), user.get(0), snippet.toString(), obj.getDate());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }

                                });

                                break;
                            case Constants.MOVEIN_TYPE:
                                reference.child("moveIns").child(obj.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        MoveIn moveIn = dataSnapshot.getValue(MoveIn.class);
                                        List<String> items = new ArrayList<>();
                                        for (MoveInItem item : moveIn.getItemList()) {
                                            items.add(item.getItemName());
                                        }
                                        snippet.append("ITEMS CHECKED IN " + "\n").append(items.toString());
                                        ResponseActivity.navigate((AppCompatActivity) getActivity(), view.findViewById(R.id.lyt_parent), user.get(0), snippet.toString(), obj.getDate());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                break;
                            case Constants.REQUEST_TYPE:
                                reference.child("extras").child(obj.getFromUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Request request = dataSnapshot.getValue(Request.class);
                                        int size = request.getExtras().size();
                                        List<String> items = new ArrayList<>();
                                        for (Extra item : request.getExtras()) {
                                            items.add(item.getExtraName());
                                        }
                                        snippet.append(request.getCity() + "\n" + size + " Items" + "\n" + items + "\n");
                                        ResponseActivity.navigate((AppCompatActivity) getActivity(), view.findViewById(R.id.lyt_parent), user.get(0), snippet.toString(), obj.getDate());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                //ResponseActivity.navigate((AppCompatActivity) getActivity(), view.findViewById(R.id.lyt_parent), user.get(0), snippet.toString(), obj.getDate());
                                break;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        return view;
    }

    public void getNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        noticationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notifications.clear();
                for (DataSnapshot notifSnapShot : dataSnapshot.getChildren()) {
                    //Iterable<DataSnapshot> children = notifSnapShot.getChildren();
                    Notification notification = notifSnapShot.getValue(Notification.class);
                    if (notification != null && (!notification.getFromUserId().equals(mAuth.getCurrentUser().getUid()))) {
                        notifications.add(notification);
                    }


                    Collections.sort(notifications, new Utils.NotificationComparator());
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
