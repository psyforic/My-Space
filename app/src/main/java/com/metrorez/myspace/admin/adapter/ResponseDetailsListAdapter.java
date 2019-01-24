package com.metrorez.myspace.admin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.model.ResponseDetails;
import com.metrorez.myspace.user.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ResponseDetailsListAdapter extends BaseAdapter {

    private DatabaseReference messagesReference = FirebaseDatabase.getInstance().getReference("responses");
    private List<ResponseDetails> mMessages;
    private Context context;

    public ResponseDetailsListAdapter(Context context, List<ResponseDetails> messages) {
        super();
        this.context = context;
        this.mMessages = messages;
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mMessages.get(position).getId();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ResponseDetails msg = (ResponseDetails) getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.row_response_details, parent, false);
            holder.time = (TextView) convertView.findViewById(R.id.text_time);
            holder.message = (TextView) convertView.findViewById(R.id.text_content);
            holder.lyt_thread = (CardView) convertView.findViewById(R.id.lyt_thread);
            holder.lyt_parent = (LinearLayout) convertView.findViewById(R.id.lyt_parent);
            holder.image_status = (ImageView) convertView.findViewById(R.id.image_status);
            holder.complaintImage = (ImageView) convertView.findViewById(R.id.image);
            //holder.complaintImage.setOnTouchListener(new ImageMatrixTouchHandler(ResponseActivity.this));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.message.setText(msg.getContent());
        holder.time.setText(msg.getDate());
        if (msg.getImageUrl() != null) {
            Picasso.with(context).load(msg.getImageUrl()).resize(100, 100)
                    .placeholder(R.drawable.animation_loader)
                    .error(R.drawable.ic_error)
                    .into(holder.complaintImage);
        }

        if (msg.isFromMe()) {
            holder.lyt_parent.setPadding(100, 10, 15, 10);
            holder.lyt_parent.setGravity(Gravity.RIGHT);
            holder.lyt_thread.setCardBackgroundColor(context.getResources().getColor(R.color.me_chat_bg));
        } else {
            holder.lyt_parent.setPadding(15, 10, 100, 10);
            holder.lyt_parent.setGravity(Gravity.LEFT);
            holder.lyt_thread.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            //holder.image_status.setImageResource(android.R.color.transparent);
        }
        return convertView;
    }

    /**
     * remove data item from messageAdapter
     **/
    public void remove(int position) {
        mMessages.remove(position);
    }

    /**
     * add data item to messageAdapter
     **/
    public void add(ResponseDetails msg) {
        mMessages.add(msg);
    }

    private void Respond(long id, String date, User user, String content, boolean fromMe) {
        ResponseDetails response = new ResponseDetails(id, date, user, content, fromMe);

        String UserId = user.getUserId();
        messagesReference.child(UserId).setValue(response);

    }

    private static class ViewHolder {
        TextView time;
        TextView message;
        LinearLayout lyt_parent;
        CardView lyt_thread;
        ImageView image_status;
        ImageView complaintImage;
    }
}
