package com.metrorez.myspace.admin.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.data.ViewAnimation;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.Notification;
import com.metrorez.myspace.user.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdminNotificationListAdapter extends RecyclerView.Adapter<AdminNotificationListAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Notification> original_items = new ArrayList<>();
    private List<Notification> filtered_items = new ArrayList<>();
    private ItemFilter mFilter = new ItemFilter();
    //    public static final String USER_ID = "USER_ID";
//    public static final String TYPE = "TYPE";
    private OnItemClickListener mOnItemClickListener;

    public AdminNotificationListAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.original_items = notifications;
        this.filtered_items = notifications;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Notification obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_new_notif, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Here is the key method to apply the animation
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Notification n = filtered_items.get(position);
        holder.content.setText(Html.fromHtml(n.getContent()));
        holder.header.setText(n.getUserName());
        /*holder.date.setText(n.getDate());
        Picasso.with(context).load(R.drawable.ic_bell)
                .placeholder(R.drawable.ic_bell)
                .resize(60, 60)
                .transform(new CircleTransform())
                .into(holder.image);
        setAnimation(holder.itemView, position);
        // view detail message conversation
        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    //clicked = true;
                    mOnItemClickListener.onItemClick(view, n, position);
                }
            }
        });*/


        holder.bt_toggle_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.toggleSectionText(holder.bt_toggle_text);
            }
        });

        holder.bt_hide_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.toggleSectionText(holder.bt_toggle_text);
            }
        });

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
        /* public TextView content;
         public TextView date;
         public ImageView image;
         public LinearLayout lyt_parent;
         */
        public TextView content, header;
        private Button bt_hide_text, bt_hide_input;
        private ImageButton bt_toggle_text;
        private View lyt_expand_text;
        private NestedScrollView nested_scroll_view;

        public ViewHolder(View v) {
            super(v);
            header = (TextView) v.findViewById(R.id.header);
            content = (TextView) v.findViewById(R.id.content);
            bt_toggle_text = (ImageButton) v.findViewById(R.id.bt_toggle_text);
            bt_hide_text = (Button) v.findViewById(R.id.bt_hide_text);
            lyt_expand_text = (View) v.findViewById(R.id.lyt_expand_text);
            lyt_expand_text.setVisibility(View.GONE);
            nested_scroll_view = (NestedScrollView) v.findViewById(R.id.nested_scroll_view);

           /* date = (TextView) v.findViewById(R.id.date);
            image = (ImageView) v.findViewById(R.id.image);
            lyt_parent = (LinearLayout) v.findViewById(R.id.lyt_parent);*/
        }

        private void toggleSectionText(View view) {
            boolean show = toggleArrow(view);
            if (show) {
                ViewAnimation.expand(lyt_expand_text, new ViewAnimation.AnimListener() {
                    @Override
                    public void onFinish() {
                        Tools.nestedScrollTo(nested_scroll_view, lyt_expand_text);
                    }
                });
            } else {
                ViewAnimation.collapse(lyt_expand_text);
            }
        }

        public boolean toggleArrow(View view) {
            if (view.getRotation() == 0) {
                view.animate().setDuration(200).rotation(180);
                return true;
            } else {
                view.animate().setDuration(200).rotation(0);
                return false;
            }
        }
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String query = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();
            final List<Notification> list = original_items;
            final List<Notification> result_list = new ArrayList<>(list.size());

            for (int i = 0; i < list.size(); i++) {
                String str_title = list.get(i).getUserName();
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
            filtered_items = (List<Notification>) results.values;
            notifyDataSetChanged();
        }

    }
}
