package com.example.intandem;

import android.content.Context;
import android.media.Image;
import android.util.Log;
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

public class ReviewsAdapter extends RecyclerView.Adapter {

    public static final String TAG = "ReviewsAdapter";
    private Context context;
    private List<ReviewItemClass> reviews;

    public ReviewsAdapter(Context context, List<ReviewItemClass> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public int getItemViewType(int position) {
        return reviews.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivPlaceImage;
        private TextView tvLocationReviews;
        private RatingBar ratingsMerged;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPlaceImage = itemView.findViewById(R.id.ivPlaceImage);
            tvLocationReviews = itemView.findViewById(R.id.tvLocationReviews);
            ratingsMerged = itemView.findViewById(R.id.ratingsMerged);
        }

        public void bind(ReviewItemClass header) {
            String imageUrl = header.getPlaceImageUrl();
            if (imageUrl != null) { // image is optional, so its possible that it is null
                Glide.with(context).load(imageUrl).into(ivPlaceImage);
            }
            tvLocationReviews.setText(header.getPlaceName());
            ratingsMerged.setRating(((Double) header.getPlaceRatingOverall()).floatValue());
        }
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView tvReviewUserName, tvReviewText;
        private ImageView ivReviewProfilePic;
        private RatingBar rbReview;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewUserName = itemView.findViewById(R.id.tvReviewUserName);
            tvReviewText = itemView.findViewById(R.id.tvReviewText);
            ivReviewProfilePic = itemView.findViewById(R.id.ivReviewProfilePic);
            rbReview = itemView.findViewById(R.id.rbReview);
        }

        public void bind(ReviewItemClass review) {
            tvReviewUserName.setText(review.getName());
            tvReviewText.setText(review.getText());
            rbReview.setRating(((Double) review.getPlaceRating()).floatValue());
            Glide.with(context).load(review.getProfilePicUrl())
                    .into(ivReviewProfilePic);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View header = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_review_header, parent, false);
                return new HeaderViewHolder(header);
            case 1:
                View review = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_review, parent, false);
                return new ReviewViewHolder(review);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ReviewItemClass reviewItemClass = reviews.get(position);

        switch (reviewItemClass.getViewType()) {
            case 0:
                Log.d(TAG, "HEADER");
                ((HeaderViewHolder) holder).bind(reviewItemClass);
                break;
            case 1:
                Log.d(TAG, "NOT HEADER");
                ((ReviewViewHolder) holder).bind(reviewItemClass);
                break;
            default:
                return;
        }
    }
}
