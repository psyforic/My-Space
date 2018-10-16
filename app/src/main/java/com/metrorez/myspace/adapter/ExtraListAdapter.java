package com.metrorez.myspace.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.metrorez.myspace.R;
import com.metrorez.myspace.model.Extra;

import java.util.List;

public class ExtraListAdapter extends RecyclerView.Adapter<ExtraListAdapter.ViewHolder> implements Filterable {
    private List<Extra> extras_list;

    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private boolean clicked = false;
    private OnItemCheckedListener onItemCheckedListener;


    public interface OnItemClickListener {
        void onItemClick(View view, Extra obj, int position);
    }

    public interface OnItemCheckedListener {
        void onItemCheck(View view, Extra obj, int position);
    }

    public void setOnItemCheckedListener(final OnItemCheckedListener mItemCheckedListener) {
        this.onItemCheckedListener = mItemCheckedListener;
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public ExtraListAdapter(Context context, List<Extra> extras) {
        extras_list = extras;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_extra, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Extra extra = extras_list.get(position);
        holder.txtExtraName.setText(extra.getExtraName());
        holder.txtExtraPrice.setText(String.valueOf(extra.getExtraPrice()));
        holder.isAdded.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!clicked && onItemCheckedListener != null) {
                    clicked = true;
                    onItemCheckedListener.onItemCheck(compoundButton.getRootView(), extra, position);
                }
            }
        });
        clicked = false;
        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!clicked && mOnItemClickListener != null) {
                    clicked = true;
                    mOnItemClickListener.onItemClick(view, extra, position);
                }
            }
        });
        clicked = false;
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
        public LinearLayout lyt_parent;
        private CheckBox isAdded;

        public ViewHolder(View itemView) {
            super(itemView);
            txtExtraName = (TextView) itemView.findViewById(R.id.extra_name);
            txtExtraPrice = (TextView) itemView.findViewById(R.id.extra_price);
            lyt_parent = (LinearLayout) itemView.findViewById(R.id.lyt_parent);
            isAdded = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }
}
