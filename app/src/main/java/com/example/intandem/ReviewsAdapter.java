package com.example.intandem;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.intandem.models.Post;
import com.example.intandem.models.Review;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    public static final String TAG = "ReviewsAdapter";
    private Context context;
    private List<Review> reviews;
    private Post currPost;

    public ReviewsAdapter(Context context, List<Review> reviews, Post currPost) {
        this.context = context;
        this.reviews = reviews;
        this.currPost = currPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewUserName, tvReviewText;
        ImageView ivReviewProfilePic;
        RatingBar rbReview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewUserName = itemView.findViewById(R.id.tvReviewUserName);
            tvReviewText = itemView.findViewById(R.id.tvReviewText);
            ivReviewProfilePic = itemView.findViewById(R.id.ivReviewProfilePic);
            rbReview = itemView.findViewById(R.id.rbReview);
        }

        public void bind(Review review) {
            tvReviewUserName.setText(review.getName());
            tvReviewText.setText(review.getText());
            rbReview.setRating(((Double) review.getRating()).floatValue());
            Glide.with(context).load(review.getProfilePicUrl())
                    .into(ivReviewProfilePic);
        }
    }
}
