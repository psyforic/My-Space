package com.metrorez.myspace.user.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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
import com.metrorez.myspace.user.MoveInActivity;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.ViewMoveInActivity;
import com.metrorez.myspace.user.adapter.MoveInListAdapter;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.MoveIn;

import java.util.ArrayList;
import java.util.List;

public class MoveInFragment extends Fragment {

    FloatingActionButton addMoveIn;
    private RecyclerView recyclerView;
    public MoveInListAdapter mAdapter;
    private ProgressBar progressBar;
    private View view;
    private List<MoveIn> moveIns;
    private LinearLayout lyt_not_found;
    private FirebaseAuth mAuth;

    private DatabaseReference moveInReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment__move_in, container, false);
        mAuth = FirebaseAuth.getInstance();
        moveInReference = FirebaseDatabase.getInstance().getReference().child("moveIns");
        //getMoveIns();
        setupUI();

        addMoveIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MoveInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        mAdapter.setOnItemClickListener(new MoveInListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, MoveIn obj, int position) {
                ViewMoveInActivity.navigate((AppCompatActivity) getActivity(), view, obj);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //getMoveIns();
    }

    private void setupUI() {
        moveIns = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        lyt_not_found = (LinearLayout) view.findViewById(R.id.lyt_not_found);
        addMoveIn = view.findViewById(R.id.fab_add_move_in);
        mAdapter = new MoveInListAdapter(getActivity(), moveIns);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), Tools.getGridSpanCount(getActivity()));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        getMoveIns();
    }

    private void getMoveIns() {

        if (moveInReference != null) {
            progressBar.setVisibility(View.VISIBLE);
            moveInReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    moveIns.clear();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot moveInSnapShot : dataSnapshot.getChildren()) {
                            MoveIn moveIn = moveInSnapShot.getValue(MoveIn.class);
                            moveIns.add(moveIn);
                        }
                        recyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();

                        if (moveIns.size() == 0) {
                            lyt_not_found.setVisibility(View.VISIBLE);
                        } else {
                            lyt_not_found.setVisibility(View.GONE);
                        }

                    } else {
                        if (moveIns.size() == 0) {
                            lyt_not_found.setVisibility(View.VISIBLE);
                        } else {
                            lyt_not_found.setVisibility(View.GONE);
                        }
                    }
                    mAdapter.setOnItemClickListener(new MoveInListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, MoveIn obj, int position) {
                            ViewMoveInActivity.navigate((AppCompatActivity) getActivity(), view, obj);
                        }
                    });

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressBar.setVisibility(View.GONE);
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
        //getMoveIns();
        super.onStart();

    }
}
