package com.metrorez.myspace.admin.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.AdminActivity;
import com.metrorez.myspace.admin.adapter.AdminUserListAdapter;
import com.metrorez.myspace.admin.model.AdminUser;
import com.metrorez.myspace.user.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    public AdminUserListAdapter mAdapter;
    private ProgressBar progressBar;
    private View view;
    private List<AdminUser> users;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_users, container, false);
        setupUI();
        return view;
    }

    private void setupUI() {
        users = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        // specify an adapter (see also next example)
        mAdapter = new AdminUserListAdapter(getActivity(), users);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new AdminUserListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, AdminUser obj, int position) {

            }
        });
    }

    public void onRefreshLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        mAdapter.notifyDataSetChanged();
        super.onResume();
    }

}
