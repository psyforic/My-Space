package com.metrorez.myspace.user.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.metrorez.myspace.R;
import com.metrorez.myspace.user.ViewComplaintActivity;
import com.metrorez.myspace.user.data.Constants;
import com.metrorez.myspace.user.model.Complaint;

import java.util.List;

public class ComplaintListAdapter extends RecyclerView.Adapter<ComplaintListAdapter.ViewHolder> implements Filterable {

    private List<Complaint> complaint_list;

    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private OnDeleteButtonClickListener onDeleteButtonClickListener;
    private boolean clicked = false;


    public interface OnItemClickListener {
        void onItemClick(View view, Complaint obj, int position);
    }

    public interface OnDeleteButtonClickListener {
        void onItemClick(View view, Complaint obj, int position);
    }

    public void setOnDeleteButtonClickListener(final OnDeleteButtonClickListener onDeleteButtonClickListener) {
        this.onDeleteButtonClickListener = onDeleteButtonClickListener;
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
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
        public CardView card_parent;
        ImageButton btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            txtComplaint = (TextView) itemView.findViewById(R.id.text_complaint);
            txtDate = (TextView) itemView.findViewById(R.id.text_date);
            card_parent = (CardView) itemView.findViewById(R.id.cardParent);
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
        holder.txtDate.setText(complaint.getComplaintDate());
        holder.card_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!clicked && mOnItemClickListener != null) {
                    clicked = true;
                    mOnItemClickListener.onItemClick(view, complaint, position);
                }
                clicked = false;
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteButtonClick(view, complaint);
            }
        });


    }

    private void onDeleteButtonClick(final View view, final Complaint complaint) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                complaint_list.remove(complaint);
                Snackbar.make(view, "Complaint Deleted Successfully", Snackbar.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }


    @Override
    public int getItemCount() {
        return complaint_list.size();
    }
}
