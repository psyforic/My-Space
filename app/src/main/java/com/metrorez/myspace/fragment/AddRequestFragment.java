package com.metrorez.myspace.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.metrorez.myspace.R;
import com.metrorez.myspace.adapter.ExtraListAdapter;
import com.metrorez.myspace.data.GlobalVariable;
import com.metrorez.myspace.model.Extra;

import java.util.ArrayList;
import java.util.List;

public class AddRequestFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExtraListAdapter mAdapter;
    private View view;
    private List<Extra> extras;
    private Button btnRequest;

    private GlobalVariable global;
    private DatabaseReference extrasReference = FirebaseDatabase.getInstance().getReference("extras");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_request, container, false);
        setupUI();
        setData();
        requestExtras();
        return view;
    }

    private void setupUI() {
        extras = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new ExtraListAdapter(getActivity(), extras);
        btnRequest = view.findViewById(R.id.btn_request);
    }

    private void requestExtras() {
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                // LayoutInflater inflater = getActivity().getLayoutInflater();

                builder.setMessage(R.string.request_message)
                        .setTitle(R.string.request_title);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addRequest();
                        Toast.makeText(getContext(), R.string.request_success, Toast.LENGTH_LONG).show();
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
        });
    }

    private void setData() {
        extras.add(new Extra("Table", 250.00, true));
        extras.add(new Extra("Table", 250.00, true));
        extras.add(new Extra("Table", 250.00, false));
        extras.add(new Extra("Table", 250.00, true));
        extras.add(new Extra("Table", 250.00, false));
        mAdapter = new ExtraListAdapter(getActivity(), extras);
        recyclerView.setAdapter(mAdapter);
    }

    private void addRequest() {
        for (Extra extra : extras) {
            if (extra.isAdded()) {
                String extraName = extra.getExtraName();
                double extraPrice = extra.getExtraPrice();
                String id = extrasReference.push().getKey();
                Extra newExtra = new Extra(id, extraName, extraPrice, extra.isAdded());
                extrasReference.child(id).setValue(newExtra);
            }
        }
    }

}
