package com.metrorez.myspace.user.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.activity.ResponseActivity;
import com.metrorez.myspace.user.data.Constants;
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
import java.util.List;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Notification> original_items = new ArrayList<>();
    private List<Notification> filtered_items = new ArrayList<>();
    private ItemFilter mFilter = new ItemFilter();
    public static final String USER_ID = "USER_ID";
    public static final String TYPE = "TYPE";
    private DatabaseReference userRefence = FirebaseDatabase.getInstance().getReference().child("users");
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    public NotificationListAdapter(Context context, List<Notification> notifications) {

        this.context = context;
        this.original_items = notifications;
        this.filtered_items = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notif, parent, false);
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Notification n = filtered_items.get(position);
        final List<User> user = new ArrayList<>();
        final StringBuilder snippet = new StringBuilder("");
        userRefence.child(n.getFromUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User newUser = dataSnapshot.getValue(User.class);
                user.add(newUser);

                switch (n.getType()) {
                    case Constants.COMPLAINT_TYPE:
                        reference.child("complaints").child(n.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Complaint complaint = dataSnapshot.getValue(Complaint.class);
                                snippet.append(complaint.getComplaintComment() + "\n" + "PRIORITY: " + complaint.getComplaintCategory() + "\n" + complaint.getComplaintResidence() + "\n"
                                        + "ROOM NO. : " + complaint.getComplaintRoom());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        break;
                    case Constants.MOVEIN_TYPE:
                        reference.child("checkins").child(n.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                MoveIn moveIn = dataSnapshot.getValue(MoveIn.class);
                                List<String> items = new ArrayList<>();
                                for (MoveInItem item : moveIn.getItemList()) {
                                    items.add(item.getItemName());
                                }
                                snippet.append("ITEMS CHECKED IN " + "\n" + items.toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        break;
                    case Constants.REQUEST_TYPE:
                        reference.child("extras").child(n.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                                    Request request = requestSnapshot.getValue(Request.class);

                                    List<String> items = new ArrayList<>();
                                    for (Extra item : request.getExtras()) {
                                        items.add(item.getExtraName());
                                    }
                                    snippet.append(request.getCity() + "\n" + items.size() + " Items" + "\n" + items + "\n");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.content.setText(Html.fromHtml(n.getContent()));
        holder.date.setText(n.getDate());
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
                ResponseActivity.navigate((AppCompatActivity) context, view.findViewById(R.id.lyt_parent), user.get(0), snippet.toString(), n.getDate());
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
        // each data item is just a string in this case
        public TextView content;
        public TextView date;
        public ImageView image;
        public LinearLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            content = (TextView) v.findViewById(R.id.content);
            date = (TextView) v.findViewById(R.id.date);
            image = (ImageView) v.findViewById(R.id.image);
            lyt_parent = (LinearLayout) v.findViewById(R.id.lyt_parent);
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
