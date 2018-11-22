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
import com.metrorez.myspace.user.model.MoveInItem;

import java.util.List;

public class MoveInItemAdapter extends RecyclerView.Adapter<MoveInItemAdapter.ViewHolder> {

    private List<MoveInItem> moveInItemList;
    private Context context;
    private boolean clicked = false;


    public MoveInItemAdapter(Context context, List<MoveInItem> moveInItemList) {
        this.moveInItemList = moveInItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_move_in_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final MoveInItem item = moveInItemList.get(position);
        holder.txtName.setText(item.getItemName());

    }

    @Override
    public int getItemCount() {
        return moveInItemList.size();
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
