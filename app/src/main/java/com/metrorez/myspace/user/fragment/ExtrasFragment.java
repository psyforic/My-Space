package com.metrorez.myspace.user.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.adapter.MyExtrasListAdapter;
import com.metrorez.myspace.user.data.Constants;
import com.metrorez.myspace.user.model.Extra;

import java.util.ArrayList;
import java.util.List;

public class ExtrasFragment extends Fragment {
    FloatingActionButton addExtra;
    private RecyclerView recyclerView;
    private MyExtrasListAdapter mAdapter;
    private View view;
    private List<Extra> extras;
    private LinearLayout lyt_not_found;
    //private String id = "";
    private Fragment fragment = null;
    private FirebaseAuth mAuth;


    DatabaseReference extrasReference = FirebaseDatabase.getInstance().getReference().child("extras");

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
                fragment = new AddRequestFragment();
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
        return view;
    }

    private void setupUI() {
        extras = new ArrayList<>();
        lyt_not_found = view.findViewById(R.id.lyt_not_found);
        recyclerView = (RecyclerView) view.findViewById(R.id.extras_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        addExtra = (FloatingActionButton) view.findViewById(R.id.fab_add_extra);

        extrasReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                extras.clear();
                for (DataSnapshot extrasSnapShot : dataSnapshot.getChildren()) {
                    Extra extra = extrasSnapShot.getValue(Extra.class);
                    extras.add(extra);
                }
                mAdapter = new MyExtrasListAdapter(getActivity(), extras);
                recyclerView.setAdapter(mAdapter);

                if (extras.size() == 0) {
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
