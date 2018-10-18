package com.metrorez.myspace.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.metrorez.myspace.R;
import com.metrorez.myspace.adapter.ConditionListAdapter;
import com.metrorez.myspace.adapter.InventoryListAdapter;
import com.metrorez.myspace.model.Inventory;

import java.util.ArrayList;
import java.util.List;

public class StepTwoFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private ConditionListAdapter mAdapter;
    private List<Inventory> inventoryList;
    private List<Inventory> selected = new ArrayList<>();
    private Fragment fragment = null;
    private Button nextBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_step_two, container, false);
        inventoryList = new ArrayList<>();
        //setupUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
        //setupRecycler(inventoryList);
    }

    public void setupRecycler(List<Inventory> inventoryList) {
        mAdapter = new ConditionListAdapter(getActivity(), inventoryList);

        recyclerView.setAdapter(mAdapter);
    }

    private void setupUI() {

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


    }
}
