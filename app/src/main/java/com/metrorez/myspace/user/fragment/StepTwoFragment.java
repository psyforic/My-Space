package com.metrorez.myspace.user.fragment;

import android.content.Context;
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
import com.metrorez.myspace.user.adapter.ConditionListAdapter;
import com.metrorez.myspace.user.model.Inventory;

import java.util.ArrayList;
import java.util.List;

public class StepTwoFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private ConditionListAdapter mAdapter;
    private List<Inventory> inventoryList = new ArrayList<>();
    private List<Inventory> selected = new ArrayList<>();
    private Fragment fragment = null;
    private Button nextBtn;
    private OnInventoryDataSender mOnInventoryDataSender;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_step_two, container, false);
        //inventoryList = new ArrayList<>();
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
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnInventoryDataSender.onInventoryDataSent((ArrayList<Inventory>) inventoryList);
            }
        });
    }

    public void setupRecycler(List<Inventory> inventoryList) {
        this.inventoryList = inventoryList;
        mAdapter = new ConditionListAdapter(getActivity(), inventoryList);
        recyclerView.setAdapter(mAdapter);
    }

    public interface OnInventoryDataSender {
        void onInventoryDataSent(ArrayList<Inventory> inventoryData);
    }

    private void setupUI() {

        nextBtn = view.findViewById(R.id.btn_next);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnInventoryDataSender = (OnInventoryDataSender) getActivity();
        } catch (ClassCastException ex) {
        }
    }

}
