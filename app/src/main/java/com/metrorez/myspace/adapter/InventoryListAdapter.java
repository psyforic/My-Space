package com.metrorez.myspace.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.metrorez.myspace.R;
import com.metrorez.myspace.model.Inventory;

import java.util.List;

public class InventoryListAdapter extends RecyclerView.Adapter<InventoryListAdapter.ViewHolder> {

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

    public InventoryListAdapter(Context context, List<Inventory> inventoryList) {
        this.inventoryList = inventoryList;
        this.context = context;
    }

    public InventoryListAdapter(List<Inventory> inventoryList, OnItemCheckListener onItemCheckListener) {
        this.inventoryList = inventoryList;
        this.onItemCheckListener = onItemCheckListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_inventory_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Inventory inventory = inventoryList.get(position);
        holder.txtName.setText(inventory.getItemName());
        holder.checkBox.setChecked(false);

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.checkBox.setChecked(!holder.checkBox.isChecked());
                if (holder.checkBox.isChecked()) {
                    onItemCheckListener.onItemCheck(inventory);
                } else {
                    onItemCheckListener.onItemUncheck(inventory);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtName;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.item_name);
            checkBox = itemView.findViewById(R.id.checkbox);
            checkBox.setClickable(false);
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            itemView.setOnClickListener(onClickListener);
        }
    }
}
