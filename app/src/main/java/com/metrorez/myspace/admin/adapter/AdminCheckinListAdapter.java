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

import com.metrorez.myspace.R;
import com.metrorez.myspace.user.model.Checkin;
import com.metrorez.myspace.user.model.Extra;
import com.metrorez.myspace.user.model.User;
import com.metrorez.myspace.user.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdminCheckinListAdapter extends RecyclerView.Adapter<AdminCheckinListAdapter.ViewHolder>  implements Filterable{

    private Context context;
    private List<Checkin> checkins;
    private List<Checkin> filtered_items;
    private List<User> users;
    private ItemFilter mFilter = new ItemFilter();

    private OnItemClickListener mOnItemClickListener;
    private boolean clicked = false;

    public interface OnItemClickListener {
        void onItemClick(View view, Checkin obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // for item long click listener
    private OnItemLongClickListener mOnItemLongClickListener;

    public interface OnItemLongClickListener {
        void onItemClick(View view, Checkin obj, int position);
    }

    public void setOnItemLongClickListener(final OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    public AdminCheckinListAdapter(Context context, List<Checkin> checkins, List<User> users) {
        this.context = context;
        this.checkins = checkins;
        this.filtered_items = checkins;
        this.users = users;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    @NonNull
    @Override
    public AdminCheckinListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_checkin_user, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminCheckinListAdapter.ViewHolder holder, final int position) {
        final Checkin checkin = filtered_items.get(position);
        final User user = users.get(position);
        holder.name.setText(user.getUserFirstName().concat(" ").concat(user.getUserLastName()));
        holder.city.setText(checkin.getCity());
        Picasso.with(context).load(R.drawable.unknown_avatar).resize(100, 100)
                .placeholder(R.drawable.unknown_avatar)
                .transform(new CircleTransform())
                .into(holder.image);

        setAnimation(holder.itemView, position);

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!clicked && mOnItemClickListener != null) {
                    clicked = true;
                    mOnItemClickListener.onItemClick(view, checkin, position);
                }
            }
        });
        clicked = false;
    }

    @Override
    public int getItemCount() {
        return filtered_items.size();
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
            final List<Checkin> list = checkins;
            final List<Checkin> result_list = new ArrayList<>(list.size());

            for (int i = 0; i < list.size(); i++) {
                String str_title = list.get(i).getDate();
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
            filtered_items = (List<Checkin>) results.values;
            notifyDataSetChanged();
        }
    }
}
