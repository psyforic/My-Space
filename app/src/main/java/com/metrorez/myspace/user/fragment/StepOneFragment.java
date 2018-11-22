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
import com.metrorez.myspace.user.adapter.InventoryListAdapter;
import com.metrorez.myspace.user.model.Inventory;

import java.util.ArrayList;
import java.util.List;

public class StepOneFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private InventoryListAdapter mAdapter;
    private List<Inventory> inventoryList;
    private List<Inventory> selected;
    private OnInventoryDataListener mOnInventoryDataListener;
    private Fragment fragment = null;
    private Button nextBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_step_one, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
       /* nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnInventoryDataListener.onInventoryDataReceived((ArrayList<Inventory>) selected);
            }
        });*/
    }

    private void setupUI() {
        inventoryList = new ArrayList<>();
        selected = new ArrayList<>();
        populateList();
        nextBtn = view.findViewById(R.id.btn_next);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new InventoryListAdapter(inventoryList, new InventoryListAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(Inventory item) {
                selected.add(item);
                passData();
            }

            @Override
            public void onItemUncheck(Inventory item) {
                selected.remove(item);
                passData();
            }
        });

        recyclerView.setAdapter(mAdapter);
    }

    private void passData() {
        mOnInventoryDataListener.onInventoryDataReceived((ArrayList<Inventory>) selected);
        this.selected.clear();
    }

    private void populateList() {
        inventoryList.add(new Inventory("Bed"));
        inventoryList.add(new Inventory("Carpet"));
        inventoryList.add(new Inventory("Table"));
        inventoryList.add(new Inventory("Wardrobe"));
        inventoryList.add(new Inventory("Lamp"));
    }

    public interface OnInventoryDataListener {
        void onInventoryDataReceived(ArrayList<Inventory> inventoryData);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnInventoryDataListener = (OnInventoryDataListener) getActivity();
        } catch (ClassCastException ex) {
        }
    }
}
