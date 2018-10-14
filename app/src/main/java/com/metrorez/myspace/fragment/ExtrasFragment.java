package com.metrorez.myspace.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.LoginActivity;
import com.metrorez.myspace.R;
import com.metrorez.myspace.adapter.ExtraListAdapter;
import com.metrorez.myspace.data.Constants;
import com.metrorez.myspace.model.Extra;

import java.util.ArrayList;
import java.util.List;

public class ExtrasFragment extends Fragment {
    FloatingActionButton addExtra;
    private RecyclerView recyclerView;
    private ExtraListAdapter mAdapter;
    private View view;
    private List<Extra> extras;
    private String id = "";
    private Fragment fragment = null;


    DatabaseReference extrasReference = FirebaseDatabase.getInstance().getReference("extras").child(id);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_extras, container, false);
        retrieveUserId();
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
        recyclerView = (RecyclerView) view.findViewById(R.id.extras_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        addExtra = (FloatingActionButton) view.findViewById(R.id.fab_add_extra);

        extrasReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                extras.clear();
                for (DataSnapshot extrasSnapShot : dataSnapshot.getChildren()) {
                    Extra extra = extrasSnapShot.getValue(Extra.class);
                    extras.add(extra);
                }
                mAdapter = new ExtraListAdapter(getActivity(), extras);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void retrieveUserId() {
        try {
            SharedPreferences prefs = getActivity().getSharedPreferences(Constants.USER_ID, Context.MODE_PRIVATE);
            String restoredText = prefs.getString("text", null);
            {
                if (restoredText == null) {
                    id = prefs.getString(Constants.USER_KEY, "");
                    Log.i("USER_ID", id);
                }
            }
        } catch (Exception ex) {
            Log.d("PREF_ERROR", ex.getMessage());
        }
    }


}
