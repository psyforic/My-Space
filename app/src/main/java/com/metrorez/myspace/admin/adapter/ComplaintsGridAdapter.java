package com.metrorez.myspace.admin.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.model.City;
import com.metrorez.myspace.user.model.Complaint;
import com.metrorez.myspace.user.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ComplaintsGridAdapter extends RecyclerView.Adapter<ComplaintsGridAdapter.ViewHolder> implements Filterable {
    private List<City> original_items = new ArrayList<>();
    private List<City> filtered_items = new ArrayList<>();
    private ItemFilter itemFilter = new ItemFilter();

    private Context context;
    private DatabaseReference complaintsRefence = FirebaseDatabase.getInstance().getReference().child("complaints");
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, City obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_admin_complaints, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final @NonNull ViewHolder holder, final int position) {
        final City city = filtered_items.get(position);
        final List<Complaint> complaintList = new ArrayList<>();
        complaintsRefence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                complaintList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot complaintSnapShot : userSnapshot.getChildren()) {
                        Complaint complaint = complaintSnapShot.getValue(Complaint.class);
                        assert complaint != null;
                        if (complaint.getComplaintCity() != null) {
                            if (complaint.getComplaintCity().equals(city.getName())) {
                                complaintList.add(complaint);
                            }
                        }
                    }
                    holder.complaints.setText(String.valueOf(complaintList.size()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.title.setText(city.getName());
        Picasso.with(context).load(city.getPhoto()).resize(100, 100).transform(new CircleTransform()).into(holder.image);

        // Here you apply the animation when the view is bound
        setAnimation(holder.itemView, position);

        // view detail message conversation
        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    // clicked = true;
                    mOnItemClickListener.onItemClick(view, city, position);
                }
            }
        });
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

    @Override
    public int getItemCount() {
        return filtered_items.size();
    }

    @Override
    public Filter getFilter() {
        return itemFilter;
    }

    public ComplaintsGridAdapter(Context context, List<City> items) {
        this.original_items = items;
        this.filtered_items = items;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView content;
        public TextView complaints;
        public ImageView image;
        public LinearLayout lyt_parent;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            content = (TextView) itemView.findViewById(R.id.content);
            complaints = (TextView) itemView.findViewById(R.id.time);
            image = (ImageView) itemView.findViewById(R.id.image);
            lyt_parent = (LinearLayout) itemView.findViewById(R.id.lyt_parent);
        }
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String query = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();
            final List<City> list = original_items;
            final List<City> result_list = new ArrayList<>(list.size());

            for (int i = 0; i < list.size(); i++) {
                String str_title = list.get(i).getName();
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
            filtered_items = (List<City>) results.values;
            notifyDataSetChanged();
        }
    }
}
