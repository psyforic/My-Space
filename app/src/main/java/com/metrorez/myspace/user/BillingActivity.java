package com.metrorez.myspace.user;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Window;
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

public class BillingActivity extends AppCompatActivity {
    private Toolbar toolbar;

    private DatabaseReference extrasReference = FirebaseDatabase.getInstance().getReference().child("extras");
    private FirebaseAuth mAuth;
    private List<Extra> extraList;
    private TextView items, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Slide());
            getWindow().setExitTransition(new Fade());
        } else {
            // Swap without transition
        }
        setContentView(R.layout.activity_billing);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Billing Information");
        mAuth = FirebaseAuth.getInstance();
        getData();
    }

    private void getData() {
        extraList = new ArrayList<>();
        extrasReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot extraSnapshot : dataSnapshot.getChildren()) {
                    Request request = extraSnapshot.getValue(Request.class);

                    for (Extra extra : request.getExtras()) {
                        extraList.add(extra);
                    }

                }
                bindUI();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void bindUI() {
        items = findViewById(R.id.not_items);
        price = findViewById(R.id.price_amount);
        items.setText(String.valueOf(extraList.size()));
        double sum = 0;
        for (Extra extra : extraList) {
            sum += extra.getExtraPrice();
        }

        price.setText("R ".concat(String.valueOf(sum)));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
