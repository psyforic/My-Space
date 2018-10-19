package com.metrorez.myspace.user.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.metrorez.myspace.user.adapter.ExtraListAdapter;
import com.metrorez.myspace.user.data.GlobalVariable;
import com.metrorez.myspace.user.model.Extra;

import java.util.ArrayList;
import java.util.List;

public class AddRequestFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExtraListAdapter mAdapter;
    private View view;
    private List<Extra> extras;
    private List<Extra> selected;
    private Button btnRequest;

    private GlobalVariable global;
    private DatabaseReference extrasReference = FirebaseDatabase.getInstance().getReference("extras");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_request, container, false);
        setData();
        setupUI();
        requestExtras();

        return view;
    }


    private void setupUI() {
        selected = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new ExtraListAdapter(extras, new ExtraListAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(Extra item) {
                selected.add(item);
            }

            @Override
            public void onItemUncheck(Extra item) {
                selected.add(item);
            }
        });
        recyclerView.setAdapter(mAdapter);
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
        extras = new ArrayList<>();
        extras.add(new Extra("Couch", 400.00, false));
        extras.add(new Extra("Fridge", 457.00, false));
        extras.add(new Extra("Table", 250.00, false));
        extras.add(new Extra("Extra Lamp", 50, false));
        extras.add(new Extra("Table", 456.00, false));
    }

    private void addRequest() {
        for (Extra extra : selected) {
            String extraName = extra.getExtraName();
            double extraPrice = extra.getExtraPrice();
            String id = extrasReference.push().getKey();
            //boolean added=true;
            Extra newExtra = new Extra(id, extraName, extraPrice, true);
            extrasReference.child(id).setValue(newExtra);

        }
    }

}
