package com.metrorez.myspace.admin.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.activity.ImageViewActivity;
import com.metrorez.myspace.admin.model.Image;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoveInImageAdapter extends RecyclerView.Adapter<MoveInImageAdapter.ViewHolder> {
    private static final String IMG_URL = "IMG_URL";
    private Context context;
    private List<Image> images;

    public MoveInImageAdapter(Context context, List<Image> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_move_in_image, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Image img = images.get(position);
        Picasso.with(context).load(img.getImageUrl())
                .placeholder(R.drawable.ic_loader)
                .fit()
                .into(holder.image);
        holder.matLaytout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), ImageViewActivity.class);
                intent.putExtra(IMG_URL, img.getImageUrl());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private MaterialRippleLayout matLaytout;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            matLaytout = itemView.findViewById(R.id.mat_parent);
        }
    }
}
