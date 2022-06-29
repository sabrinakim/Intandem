package com.example.intandem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;


public class RepliesAdapter extends RecyclerView.Adapter<RepliesAdapter.ViewHolder> {

    public static final String TAG = "RepliesAdapter";
    Context context;
    private List<Reply> replies;

    public RepliesAdapter(Context context, List<Reply> replies) {
        this.context = context;
        this.replies = replies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reply, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // all positions are iterated through.
        Reply reply = replies.get(position);
        // each viewholder is the same (created in above function)
        holder.bind(reply);
    }

    @Override
    public int getItemCount() {
        return replies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPictureReplyFeed;
        TextView tvCaptionReplyFeed;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPictureReplyFeed = itemView.findViewById(R.id.ivPictureReplyFeed);
            tvCaptionReplyFeed = itemView.findViewById(R.id.tvCaptionReplyFeed);
        }

        public void bind(Reply reply) {
            ParseFile image = reply.getPicture();
            if (image != null) { // image is optional, so its possible that it is null
                Glide.with(context).load(image.getUrl()).into(ivPictureReplyFeed);
            }
            tvCaptionReplyFeed.setText(reply.getCaption());
        }
    }
}
