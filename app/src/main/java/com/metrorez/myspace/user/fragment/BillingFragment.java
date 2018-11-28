package com.metrorez.myspace.user.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.model.Extra;
import com.metrorez.myspace.user.model.Request;

import java.util.ArrayList;
import java.util.List;

public class BillingFragment extends Fragment {
    private View view;
    private Toolbar toolbar;

    private DatabaseReference extrasReference = FirebaseDatabase.getInstance().getReference().child("extras");
    private FirebaseAuth mAuth;
    private List<Extra> extraList;
    private TextView items, price;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_billing, container, false);
        //initToolbar();
        mAuth = FirebaseAuth.getInstance();
        getData();
        return view;
    }

   /* private void initToolbar() {
        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Billing Information");
        mAuth = FirebaseAuth.getInstance();
    }*/
    private void getData() {
        extraList = new ArrayList<>();
        extrasReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot extraSnapshot : dataSnapshot.getChildren()) {
                    Request request = extraSnapshot.getValue(Request.class);
                    extraList.addAll(request != null ? request.getExtras() : null);
                }
                bindUI();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void bindUI() {
        items = view.findViewById(R.id.not_items);
        price = view.findViewById(R.id.price_amount);
        items.setText(String.valueOf(extraList.size()));
        double sum = 0;
        for (Extra extra : extraList) {
            sum += extra.getExtraPrice();
        }
        price.setText("R ".concat(String.valueOf(sum)));
    }
}
