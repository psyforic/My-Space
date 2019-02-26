package com.metrorez.myspace.user.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.activity.ResponseActivity;
import com.metrorez.myspace.admin.data.ViewAnimation;
import com.metrorez.myspace.user.data.Constants;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.MoveIn;
import com.metrorez.myspace.user.model.Complaint;
import com.metrorez.myspace.user.model.Extra;
import com.metrorez.myspace.user.model.MoveInItem;
import com.metrorez.myspace.user.model.Notification;
import com.metrorez.myspace.user.model.Request;
import com.metrorez.myspace.user.model.User;
import com.metrorez.myspace.user.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Notification> original_items = new ArrayList<>();
    private List<Notification> filtered_items = new ArrayList<>();
    private ItemFilter mFilter = new ItemFilter();
    public static final String USER_ID = "USER_ID";
    public static final String TYPE = "TYPE";

    private DatabaseReference notificationsReference = FirebaseDatabase.getInstance().getReference("notifications");

    public NotificationListAdapter(Context context, List<Notification> notifications) {

        this.context = context;
        this.original_items = notifications;
        this.filtered_items = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_notif, parent, false);
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Notification n = filtered_items.get(position);
        holder.content.setText(n.getContent());
        holder.header.setText(n.getType() + " from " + n.getUserName());
        holder.date.setText(n.getDate());

        setAnimation(holder.itemView, position);

        holder.bt_toggle_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.toggleSectionText(holder.bt_toggle_text);
//                Map<String, Object> isRead = new HashMap<>();
//                isRead.put("read", true);
//                notificationsReference.child(n.getNotif_id()).updateChildren(isRead);
            }
        });

        holder.bt_hide_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.toggleSectionText(holder.bt_toggle_text);
//                Map<String, Object> isRead = new HashMap<>();
//                isRead.put("read", true);
//                notificationsReference.child(n.getNotif_id()).updateChildren(isRead);
            }
        });

        holder.btn_delete_notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteButtonClick(view, position);
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
        private TextView content, header;
        private Button bt_hide_text;
        private ImageButton bt_toggle_text;
        private View lyt_expand_text;
        private TextView date;
        private NestedScrollView nested_scroll_view;
        private ImageButton btn_delete_notif;

        public ViewHolder(View v) {
            super(v);
            header = (TextView) v.findViewById(R.id.header);
            content = (TextView) v.findViewById(R.id.content);
            date = (TextView) v.findViewById(R.id.date);
            bt_toggle_text = (ImageButton) v.findViewById(R.id.bt_toggle_text);
            bt_hide_text = (Button) v.findViewById(R.id.bt_hide_text);
            lyt_expand_text = (View) v.findViewById(R.id.lyt_expand_text);
            lyt_expand_text.setVisibility(View.GONE);
            nested_scroll_view = (NestedScrollView) v.findViewById(R.id.nested_scroll_view);
            btn_delete_notif = (ImageButton) v.findViewById(R.id.btn_delete_notif);


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
