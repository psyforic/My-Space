package com.metrorez.myspace.admin.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.ListMenuItemView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.metrorez.myspace.R;
import com.metrorez.myspace.user.model.Complaint;
import com.metrorez.myspace.user.model.User;
import com.metrorez.myspace.user.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdminComplaintListAdapter extends RecyclerView.Adapter<AdminComplaintListAdapter.ViewHolder> implements Filterable {


    private Context context;
    private List<Complaint> complaints;
    private List<Complaint> filtered_items;
    private List<User> users;
    private ItemFilter mFilter = new ItemFilter();

    public AdminComplaintListAdapter(Context context, List<Complaint> complaints, List<User> users) {
        this.context = context;
        this.complaints = complaints;
        this.filtered_items = complaints;
        this.users = users;
    }

    private OnItemClickListener mOnItemClickListener;
    //private boolean clicked = false;

    public interface OnItemClickListener {
        void onItemClick(View view, Complaint obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // for item long click listener
    private OnItemLongClickListener mOnItemLongClickListener;

    public interface OnItemLongClickListener {
        void onItemClick(View view, Complaint obj, int position);
    }

    public void setOnItemLongClickListener(final OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_complaint_user, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Complaint complaint = filtered_items.get(position);
        User newUser = new User();
        for (User user : users) {
            if (user.getUserId().equals(complaint.getUserId())) {
                newUser = user;
            }
        }
        holder.name.setText(newUser.getUserFirstName().concat(" ").concat(newUser.getUserLastName()));
        holder.city.setText(complaint.getComplaintCity());
        Picasso.with(context).load(R.drawable.unknown_avatar).resize(100, 100)
                .placeholder(R.drawable.unknown_avatar)
                .transform(new CircleTransform())
                .into(holder.image);

        setAnimation(holder.itemView, position);

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null ) {
                   // clicked = true;
                    mOnItemClickListener.onItemClick(view, complaint, position);
                }

            }
        });
       // clicked = false;
    }

    private Complaint getComplaint(int position) {
        return filtered_items.get(position);
    }

    @Override
    public int getItemCount() {
        return filtered_items.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView city;
        public ImageView image;
        public LinearLayout lyt_parent;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            city = (TextView) itemView.findViewById(R.id.city);
            image = (ImageView) itemView.findViewById(R.id.image);
            lyt_parent = (LinearLayout) itemView.findViewById(R.id.lyt_parent);
        }
    }

    private int lastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String query = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();
            final List<Complaint> list = complaints;
            final List<Complaint> result_list = new ArrayList<>(list.size());

            for (int i = 0; i < list.size(); i++) {
                String str_title = list.get(i).getComplaintCity();
                if (str_title.toLowerCase().contains(query)) {
                    result_list.add(list.get(i));
                }
            }
            results.values = result_list;
            results.count = result_list.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered_items = (List<Complaint>) results.values;
            notifyDataSetChanged();
        }
    }
}
