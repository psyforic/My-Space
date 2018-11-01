package com.metrorez.myspace.admin.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.ComplaintsDetailsActivity;
import com.metrorez.myspace.admin.adapter.ComplaintsGridAdapter;
import com.metrorez.myspace.admin.model.City;
import com.metrorez.myspace.admin.data.Constants;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.Complaint;

import java.util.ArrayList;
import java.util.List;

public class AdminComplaintsFragment extends Fragment {

    RecyclerView recyclerView;
    public ComplaintsGridAdapter mAdapter;
    private ProgressBar progressBar;
    private View view;
    private LinearLayout lyt_not_found;
    private List<City> cities;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTheme(R.style.AdminTheme);
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_admin_complaints, container, false);
        setupUI();
        return view;
    }


    private void setupUI() {
        cities = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        lyt_not_found = (LinearLayout) view.findViewById(R.id.lyt_not_found);

        LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), Tools.getGridSpanCount(getActivity()));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        if (Constants.getCityData(getActivity()).size() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {
            lyt_not_found.setVisibility(View.GONE);
        }

        mAdapter = new ComplaintsGridAdapter(getActivity(), Constants.getCityData(getActivity()));
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ComplaintsGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, City obj, int position) {
                ComplaintsDetailsActivity.navigate((AppCompatActivity) getActivity(), view.findViewById(R.id.lyt_parent), obj);
            }
        });
    }

}
