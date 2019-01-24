package com.metrorez.myspace.user.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.metrorez.myspace.R;
import com.metrorez.myspace.user.model.MoveIn;
import com.metrorez.myspace.user.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoveInListAdapter extends RecyclerView.Adapter<MoveInListAdapter.ViewHolder> {

    private List<MoveIn> moveIn_list;
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private boolean clicked = false;

    public interface OnItemClickListener {
        void onItemClick(View view, MoveIn obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_move_in, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MoveInListAdapter.ViewHolder holder, final int position) {
        final MoveIn moveIn = moveIn_list.get(position);
        holder.title.setText(moveIn.getDate());
        holder.time.setText(moveIn.getDate());

        Picasso.with(ctx).load(R.drawable.ic_checkin).resize(100, 100)
                .transform(new CircleTransform())
                .into(holder.image);
        setAnimation(holder.itemView, position);

        holder.card_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    //clicked = true;
                    mOnItemClickListener.onItemClick(view, moveIn, position);
                }
                //clicked = false;
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
        return moveIn_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView content;
        public TextView time;
        public ImageView image;
        public LinearLayout card_parent;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            content = (TextView) itemView.findViewById(R.id.content);
            time = (TextView) itemView.findViewById(R.id.time);
            image = (ImageView) itemView.findViewById(R.id.image);
            card_parent = (LinearLayout) itemView.findViewById(R.id.cardParent);
        }
    }

    public MoveInListAdapter(Context context, List<MoveIn> items) {
        this.ctx = context;
        this.moveIn_list = items;
    }
}
