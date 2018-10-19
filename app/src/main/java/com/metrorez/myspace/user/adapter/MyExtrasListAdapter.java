package com.metrorez.myspace.user.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.metrorez.myspace.R;
import com.metrorez.myspace.user.model.Extra;

import java.util.List;

public class MyExtrasListAdapter extends RecyclerView.Adapter<MyExtrasListAdapter.ViewHolder> {

    private Context context;
    private List<Extra> extras;


    public MyExtrasListAdapter(Context context, List<Extra> extras) {
        this.context = context;
        this.extras = extras;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_myextra, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyExtrasListAdapter.ViewHolder holder, int position) {
        final Extra extra = extras.get(position);
        holder.txtExtraName.setText(extra.getExtraName());
        holder.txtExtraPrice.setText(String.valueOf(extra.getExtraPrice()));
    }

    @Override
    public int getItemCount() {
        return extras.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtExtraName;
        private TextView txtExtraPrice;
        public LinearLayout lyt_parent;

        public ViewHolder(View itemView) {
            super(itemView);
            txtExtraName = (TextView) itemView.findViewById(R.id.extra_name);
            txtExtraPrice = (TextView) itemView.findViewById(R.id.extra_price);
            lyt_parent = (LinearLayout) itemView.findViewById(R.id.lyt_parent);
        }
    }
}
