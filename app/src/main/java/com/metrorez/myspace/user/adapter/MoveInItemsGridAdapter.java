package com.metrorez.myspace.user.adapter;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.metrorez.myspace.R;
import com.metrorez.myspace.user.model.MoveInItem;
import com.metrorez.myspace.user.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoveInItemsGridAdapter extends RecyclerView.Adapter<MoveInItemsGridAdapter.ViewHolder> implements Filterable {

    private List<MoveInItem> moveInItems;
    private Context ctx;

    public MoveInItemsGridAdapter(Context context, List<MoveInItem> moveInItems) {
        this.ctx = context;
        this.moveInItems = moveInItems;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.move_in_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MoveInItem moveIn = moveInItems.get(position);
        holder.title.setText(moveIn.getItemName());
        Picasso.with(ctx).load(moveIn.getImageUrl()).fit()
                .placeholder(R.drawable.ic_item_placeholder)
                .into(holder.image);
        setAnimation(holder.itemView, position);

        holder.camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private int lastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return moveInItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageButton camera_btn;
        public ImageButton delete_btn;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            image = (ImageView) itemView.findViewById(R.id.image);
            camera_btn = (ImageButton) itemView.findViewById(R.id.camera_btn);
            delete_btn = (ImageButton) itemView.findViewById(R.id.delete_btn);
        }
    }
}