package com.metrorez.myspace.user.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.metrorez.myspace.R;

import java.util.HashMap;
import java.util.Map;

public class StepThreeFragment extends Fragment {
    private View view;
    private ProgressBar progressBar;
    private FirebaseAuth  mAuth = FirebaseAuth.getInstance();
    private ActionBar actionBar;
    private Toolbar toolbar;
    private EditText comments;
    private DatabaseReference moveInReference = FirebaseDatabase.getInstance().getReference("moveIns").child(mAuth.getCurrentUser().getUid());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_step_three, container, false);

        initToolbar();
        setupUI();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupUI() {
        progressBar = view.findViewById(R.id.progressBar);
        comments = view.findViewById(R.id.input_comment);
    }

    private void initToolbar() {
        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(getString(R.string.str_extra_comments));
    }

    public void addExtraComments() {
        Map<String, Object> extraComments = new HashMap<>();
        if (comments.getText() != null) {
            extraComments.put("extraComments",comments.getText().toString());
            moveInReference.child(moveInReference.getKey()).updateChildren(extraComments).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }
    }


}
