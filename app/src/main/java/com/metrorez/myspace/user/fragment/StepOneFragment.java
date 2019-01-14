package com.metrorez.myspace.user.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.metrorez.myspace.R;
import com.metrorez.myspace.user.MoveInActivity;
import com.metrorez.myspace.user.adapter.InventoryListAdapter;
import com.metrorez.myspace.user.model.Inventory;

import java.util.ArrayList;
import java.util.List;

public class StepOneFragment extends BaseFragment {

    private View view;
    private RecyclerView recyclerView;
    private InventoryListAdapter mAdapter;
    private List<Inventory> inventoryList;
    private List<Inventory> selected;
    public ActionBar actionBar;
    private Toolbar toolbar;
    private OnInventoryDataListener mOnInventoryDataListener;
    private Fragment fragment = null;
    private Button nextBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_step_one, container, false);
        initToolbar();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
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

    private void initToolbar() {
        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(getString(R.string.move_in_checkinList));
    }

    private void passData() {
        mOnInventoryDataListener.onInventoryDataReceived((ArrayList<Inventory>) selected);
        this.selected.clear();
    }

    private void populateList() {
        inventoryList.add(new Inventory("Plugs/light fittings"));
        inventoryList.add(new Inventory("Carpets"));
        inventoryList.add(new Inventory("Door Locks"));
        inventoryList.add(new Inventory("Wardrobe"));
        inventoryList.add(new Inventory("Bed"));
        inventoryList.add(new Inventory("Waste Bin"));
        inventoryList.add(new Inventory("Chair"));
        inventoryList.add(new Inventory("Study Table"));
        inventoryList.add(new Inventory("Wardrobe"));
        inventoryList.add(new Inventory("Keys"));
        inventoryList.add(new Inventory("Mirror"));
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

    @Override
    public boolean onBackPressed() {
        getActivity().startActivity(new Intent(getActivity(), MoveInActivity.class));
        getActivity().finish();
        return true;
    }

}
