package com.metrorez.myspace.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.metrorez.myspace.R;
import com.metrorez.myspace.model.Extra;

import java.util.List;

public class ExtraListAdapter extends RecyclerView.Adapter<ExtraListAdapter.ViewHolder> implements Filterable {
    private List<Extra> extras_list;

    private Context context;
    private ComplaintListAdapter.OnItemClickListener mOnItemClickListener;
    private boolean clicked = false;


    public interface OnItemClickListener {
        void onItemClick(View view, Extra obj, int position);
    }

    public void setOnItemClickListener(final ComplaintListAdapter.OnItemClickListener mItemClickListener) {
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

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
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
