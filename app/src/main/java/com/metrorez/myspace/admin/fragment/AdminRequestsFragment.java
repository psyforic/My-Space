package com.metrorez.myspace.admin.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.adapter.RequestsGridAdapter;
import com.metrorez.myspace.admin.model.City;
import com.metrorez.myspace.admin.model.Request;
import com.metrorez.myspace.user.data.Tools;

import java.util.ArrayList;
import java.util.List;

public class AdminRequestsFragment extends Fragment {
    RecyclerView recyclerView;
    public RequestsGridAdapter mAdapter;
    private ProgressBar progressBar;
    private View view;
    private LinearLayout lyt_not_found;
    private List<City> cities;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_admin_requests, container, false);
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

        if (cities.size() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {
            lyt_not_found.setVisibility(View.GONE);
        }

        // specify an adapter (see also next example)
        mAdapter = new RequestsGridAdapter(getActivity(), cities);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new RequestsGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, City obj, int position) {

            }
        });
    }


}
