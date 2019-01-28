package com.metrorez.myspace.admin.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.data.ViewAnimation;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class AdminNotificationListAdapter extends RecyclerView.Adapter<AdminNotificationListAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Notification> original_items = new ArrayList<>();
    private List<Notification> filtered_items = new ArrayList<>();
    private ItemFilter mFilter = new ItemFilter();

    private OnItemClickListener mOnItemClickListener;
    private DatabaseReference notificationsReference = FirebaseDatabase.getInstance().getReference("notifications");

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

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_new_notif, parent, false);
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
        holder.content.setText(n.getContent());
        holder.header.setText(n.getType() + " from " + n.getUserName());
        holder.date.setText(n.getDate());

        setAnimation(holder.itemView, position);

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
        holder.btn_delete_notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteButtonClick(view, position);
            }
        });
    }

    private void onDeleteButtonClick(final View view, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.notif_dialog_message)
                .setTitle(R.string.del_notif_dialog_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String notifId = filtered_items.get(position).getNotif_id();
                Query complaintsQuery = notificationsReference.orderByChild("notif_id").equalTo(notifId);
                complaintsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot notifSnapshot : dataSnapshot.getChildren()) {
                            notifSnapshot.getRef().removeValue();
                            notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //complaint_list.remove(complaint);
                Snackbar.make(view, R.string.notif_delete_success, Snackbar.LENGTH_LONG).show();
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
        return filtered_items.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView content, header;
        private Button bt_hide_text;
        private ImageButton bt_toggle_text;
        private View lyt_expand_text;
        private TextView date;
        private ImageButton btn_delete_notif;
        private NestedScrollView nested_scroll_view;

        public ViewHolder(View v) {
            super(v);
            header = (TextView) v.findViewById(R.id.header);
            content = (TextView) v.findViewById(R.id.content);
            date = (TextView) v.findViewById(R.id.date);
            bt_toggle_text = (ImageButton) v.findViewById(R.id.bt_toggle_text);
            bt_hide_text = (Button) v.findViewById(R.id.bt_hide_text);
            lyt_expand_text = (View) v.findViewById(R.id.lyt_expand_text);
            lyt_expand_text.setVisibility(View.GONE);
            btn_delete_notif = (ImageButton) v.findViewById(R.id.bt_delete_notif);
            nested_scroll_view = (NestedScrollView) v.findViewById(R.id.nested_scroll_view);


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

        private boolean toggleArrow(View view) {
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
                if (str_title.toLowerCase().contains(query) || list.get(i).getType().toLowerCase().contains(query)) {
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
