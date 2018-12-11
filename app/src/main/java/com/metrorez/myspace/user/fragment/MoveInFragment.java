package com.metrorez.myspace.user.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.metrorez.myspace.user.ProfileActivity;
import com.metrorez.myspace.user.ViewMoveInActivity;
import com.metrorez.myspace.user.adapter.MoveInListAdapter;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.MoveIn;
import com.metrorez.myspace.user.model.User;

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
    private String userRoom, residenceName;
    private DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment__move_in, container, false);
        mAuth = FirebaseAuth.getInstance();
        moveInReference = FirebaseDatabase.getInstance().getReference().child("moveIns");
        setupUI();
        getUserInfo();

        addMoveIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validateRoomNo() && validateResidence()) {
                    Intent intent = new Intent(getActivity(), MoveInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    showDialog();
                }
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
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    private boolean validateResidence() {
        if (residenceName == null) {
            return false;
        }
        return true;
    }
    private boolean validateRoomNo() {
        if (userRoom == null) {
            return false;
        }
        return true;
    }
    private void getUserInfo() {
        usersReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                residenceName = user != null ? user.getUserResidence() : null;
                userRoom = user != null ? user.getUserRoom() : null;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.movein_error_message)
                .setTitle(R.string.update_profile).setIcon(R.drawable.ic_error);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(getActivity(), ProfileActivity.class));
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
