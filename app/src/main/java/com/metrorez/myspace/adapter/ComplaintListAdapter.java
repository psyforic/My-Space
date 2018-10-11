package com.metrorez.myspace.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.metrorez.myspace.R;
import com.metrorez.myspace.model.Complaint;

import java.util.List;

public class ComplaintListAdapter extends RecyclerView.Adapter<ComplaintListAdapter.ViewHolder> implements Filterable {

    private List<Complaint> complaint_list;

    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private boolean clicked = false;


    public interface OnItemClickListener {
        void onItemClick(View view, Complaint obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public ComplaintListAdapter(Context context, List<Complaint> complaints) {
        complaint_list = complaints;
        this.context = context;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtComplaint;
        public TextView txtDate;
        public LinearLayout lyt_parent;
        ImageButton btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            txtComplaint = (TextView) itemView.findViewById(R.id.text_complaint);
            txtDate = (TextView) itemView.findViewById(R.id.text_date);
            lyt_parent = (LinearLayout) itemView.findViewById(R.id.lyt_parent);
            btnDelete = (ImageButton) itemView.findViewById(R.id.btn_delete_complaint);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_complaint, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Complaint complaint = complaint_list.get(position);
        holder.txtComplaint.setText(complaint.getComplaintComment());
        holder.txtDate.setText(complaint.getComplaintDate().toString());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!clicked && mOnItemClickListener != null) {
                    clicked = true;
                    mOnItemClickListener.onItemClick(view, complaint, position);
                }
            }
        });
        clicked = false;

    }

    @Override
    public int getItemCount() {
        return complaint_list.size();
    }
}
