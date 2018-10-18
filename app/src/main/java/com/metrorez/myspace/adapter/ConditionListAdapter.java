package com.metrorez.myspace.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.metrorez.myspace.R;
import com.metrorez.myspace.model.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConditionListAdapter extends RecyclerView.Adapter<ConditionListAdapter.ViewHolder> {

    private List<Inventory> inventoryList;
    private Context context;
    private OnItemCheckListener onItemCheckListener;
    private boolean clicked = false;

    public interface OnItemCheckListener {
        void onItemCheck(Inventory item);

        void onItemUncheck(Inventory item);
    }

    public void setOnItemCheckListener(final OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }

    public ConditionListAdapter(Context context, List<Inventory> inventoryList) {
        this.inventoryList = inventoryList;
        this.context = context;
    }

    public ConditionListAdapter(List<Inventory> inventoryList, OnItemCheckListener onItemCheckListener) {
        this.inventoryList = inventoryList;
        this.onItemCheckListener = onItemCheckListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_condition, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Inventory inventory = inventoryList.get(position);
        holder.txtName.setText(inventory.getItemName());
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtName;
        public Spinner dropdown;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.item_name);
            dropdown = itemView.findViewById(R.id.spinner_condition);

            final List<String> conditionList = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.condition)));
            final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, conditionList) {
                @Override
                public boolean isEnabled(int position) {
                    if (position == 0) {
                        return false;
                    } else {
                        return true;
                    }
                }

                @Override
                public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView textView = (TextView) view;
                    if (position == 0) {
                        textView.setTextColor(Color.GRAY);
                    } else {
                        textView.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };

            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dropdown.setAdapter(spinnerArrayAdapter);

            dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String item = adapterView.getItemAtPosition(i).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }

    }
}
