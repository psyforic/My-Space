package com.metrorez.myspace.user.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.metrorez.myspace.R;
import com.metrorez.myspace.user.model.Inventory;

import java.util.List;

public class CheckinItemAdapter extends RecyclerView.Adapter<CheckinItemAdapter.ViewHolder> {

    private List<Inventory> inventoryList;
    private Context context;
    private boolean clicked = false;


    public CheckinItemAdapter(Context context, List<Inventory> inventoryList) {
        this.inventoryList = inventoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_checkin_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Inventory inventory = inventoryList.get(position);
        holder.txtName.setText(inventory.getItemName());

    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtName;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.item_name);

        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            itemView.setOnClickListener(onClickListener);
        }
    }
}
