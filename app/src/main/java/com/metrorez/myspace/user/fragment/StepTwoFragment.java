package com.metrorez.myspace.user.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.adapter.ConditionListAdapter;
import com.metrorez.myspace.user.adapter.MoveInItemsGridAdapter;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.Inventory;
import com.metrorez.myspace.user.model.MoveInItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StepTwoFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private List<Inventory> inventoryList = new ArrayList<>();
    private List<MoveInItem> items = new ArrayList<>();
    private FirebaseAuth mAuth;
    private MoveInItemsGridAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_step_two, container, false);
        mAuth = FirebaseAuth.getInstance();
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
    }

    public void setupRecycler(List<Inventory> inventoryList) {
        this.inventoryList = inventoryList;
        setupItems();
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void setupUI() {
        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), Tools.getGridSpanCount(getActivity()));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setupItems() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String date = dateFormat.format(today);
        for (Inventory inventory : inventoryList) {
            MoveInItem item = new MoveInItem(inventory.getItemName(), mAuth.getCurrentUser().getUid(), date);
            if (!items.contains(item.getItemName())) {
                items.add(item);
            }
        }
        Log.i("DATA_ITEMS", items.toString());
        mAdapter = new MoveInItemsGridAdapter(getActivity(), items);
        mAdapter.notifyDataSetChanged();
    }

}
