package com.metrorez.myspace.user.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.metrorez.myspace.R;
import com.metrorez.myspace.user.model.Extra;

import java.util.List;

public class ExtraListAdapter extends RecyclerView.Adapter<ExtraListAdapter.ViewHolder> implements Filterable {
    private List<Extra> extras_list;

    private Context context;
    private OnItemCheckListener onItemCheckListener;
    private boolean clicked = false;

    public interface OnItemCheckListener {
        void onItemCheck(Extra item);

        void onItemUncheck(Extra item);
    }

    public void setOnItemCheckListener(final OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }

    public ExtraListAdapter(Context context, List<Extra> extras) {
        extras_list = extras;
        this.context = context;
    }

    public ExtraListAdapter(List<Extra> extras_list, OnItemCheckListener onItemCheckListener) {
        this.extras_list = extras_list;
        this.onItemCheckListener = onItemCheckListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_extra, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Extra extra = extras_list.get(position);
        holder.txtExtraName.setText(extra.getExtraName());
        holder.txtExtraPrice.setText(String.valueOf(extra.getExtraPrice()));
        holder.isAdded.setChecked(false);

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.isAdded.setChecked(!holder.isAdded.isChecked());
                if (holder.isAdded.isChecked()) {
                    onItemCheckListener.onItemCheck(extra);
                } else {
                    onItemCheckListener.onItemUncheck(extra);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return extras_list.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtExtraName;
        private TextView txtExtraPrice;
        //public LinearLayout lyt_parent;
        private CheckBox isAdded;

        public ViewHolder(View itemView) {
            super(itemView);
            txtExtraName = (TextView) itemView.findViewById(R.id.extra_name);
            txtExtraPrice = (TextView) itemView.findViewById(R.id.extra_price);
            //lyt_parent = (LinearLayout) itemView.findViewById(R.id.lyt_parent);
            isAdded = (CheckBox) itemView.findViewById(R.id.checkbox);
            isAdded.setClickable(false);
        }
        public void setOnClickListener(View.OnClickListener onClickListener) {
            itemView.setOnClickListener(onClickListener);
        }
    }
}
