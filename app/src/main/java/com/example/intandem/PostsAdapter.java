package com.example.intandem;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.intandem.dataModels.DistanceSearchResult;
import com.example.intandem.models.CustomPlace;
import com.example.intandem.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public static final String TAG = "PostsAdapter";
    private static final String BASE_URL = "https://maps.googleapis.com/";
    Context context;
    private List<Post> posts;
    private ParseUser currUser;
    private Location currLocation;
    private boolean duration_flag = false;

    public PostsAdapter(Context context, List<Post> posts, ParseUser currUser, Location currLocation) {
        this.context = context;
        this.posts = posts;
        this.currUser = currUser;
        this.currLocation = currLocation;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // all positions are iterated through.
        Post post = posts.get(position);
        // each viewholder is the same (created in above function)
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvLocationFeed, tvCaptionFeed, tvName, tvExpiration, tvMoreData;
        ImageView ivPictureFeed, ivProfilePicture;
        ImageButton btnViewReplies;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            tvEventFeed = itemView.findViewById(R.id.tvEventFeed);
            tvLocationFeed = itemView.findViewById(R.id.tvLocationFeed);
            tvCaptionFeed = itemView.findViewById(R.id.tvCaptionFeed);
            ivPictureFeed = itemView.findViewById(R.id.ivPictureFeed);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePic);
            tvName = itemView.findViewById(R.id.tvName);
            tvExpiration = itemView.findViewById(R.id.tvExpiration);
            tvMoreData = itemView.findViewById(R.id.tvMoreData);
            btnViewReplies = itemView.findViewById(R.id.btnViewReplies);
            itemView.setOnClickListener(this);


            GestureDetector gestureDetector = new GestureDetector(context.getApplicationContext(), new GestureListener());
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });

        }

        public void bind(Post post) {

//            tvEventFeed.setText(post.getEvent());
//            tvEventFeed.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    // navigate to replies fragment
//                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
//                    Fragment fragment = RepliesFragment.newInstance(post);
//                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
//                }
//            });

            tvName.setText(post.getUser().getUsername());

            Calendar rightNow = Calendar.getInstance();
            String timeLeft = DateDiff.findDifference(rightNow.getTime(), post.getExpiration());

            tvExpiration.setText(timeLeft + " Left");

            btnViewReplies.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ViewRepliesActivity.class);
                    i.putExtra("currPost", post);
                    context.startActivity(i);
                }
            });

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            GoogleMapsService googleMapsService = retrofit.create(GoogleMapsService.class);
            CustomPlace customPlace = post.getCustomPlace();
            try {
                customPlace.fetchIfNeeded();
                tvLocationFeed.setText(post.getCustomPlace().getName());
                googleMapsService.getDistanceSearchResult(currLocation.getLatitude() + "," + currLocation.getLongitude(),
                        "place_id:" + post.getCustomPlace().getGPlaceId(),
                        "driving",
                        "en",
                        BuildConfig.MAPS_API_KEY).enqueue(new Callback<DistanceSearchResult>() {
                    @Override
                    public void onResponse(Call<DistanceSearchResult> call, Response<DistanceSearchResult> response) {
                        DistanceSearchResult distanceSearchResult = response.body();
                        Log.i(TAG, "success getting the distance");
                        StringBuilder moreInfo = new StringBuilder();
                        moreInfo.append(distanceSearchResult.getRows().get(0).getElements().get(0)
                                .getDistance().getText());
                        String price = customPlace.getPrice();
                        if (price != null) {
                            moreInfo.append(" | ");
                            moreInfo.append(customPlace.getPrice());
                        }
                        tvMoreData.setText(moreInfo);
                        tvMoreData.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!duration_flag) {
                                    duration_flag = true;
                                    String duration = distanceSearchResult.getRows().get(0).getElements()
                                            .get(0).getDuration().getText() + " by car";
                                    tvMoreData.setText(duration);
                                } else {
                                    duration_flag = false;
                                    tvMoreData.setText(moreInfo);
                                }

                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<DistanceSearchResult> call, Throwable t) {
                        Log.e(TAG, "error getting the distance");
                    }
                });
            } catch (ParseException e) {
                e.printStackTrace();
            }


//            CustomPlace c = post.getCustomPlace();
//            c.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
//                @Override
//                public void done(ParseObject object, ParseException e) {
//                    if (e != null) {
//                        Log.e(TAG, "error fetching if needed in background");
//                    }
//                    tvLocationFeed.setText(post.getCustomPlace().getName());
//                    tvLocationFeed.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            // TODO: make reviews activity
////                            AppCompatActivity activity = (AppCompatActivity) v.getContext();
////                            Fragment fragment = ReviewsFragment.newInstance(post);
////                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
//                        }
//                    });
//                }
//            });

            ParseFile image = post.getPicture();
            if (image != null) { // image is optional, so its possible that it is null
                Glide.with(context).load(image.getUrl()).into(ivPictureFeed);
            }

            Glide.with(context).load("https://s3-media3.fl.yelpcdn.com/photo/iwoAD12zkONZxJ94ChAaMg/o.jpg")
                    .transform(new CircleCrop())
                    .into(ivProfilePicture);

            tvCaptionFeed.setText(post.getCaption());
        }


        @Override
        public void onClick(View v) {
            //Toast.makeText(context, "reply", Toast.LENGTH_SHORT).show();
            int position = getAdapterPosition();
//            if (position != RecyclerView.NO_POSITION) {
//                Post post = posts.get(position);
//                Intent i = new Intent (context, ReplyActivity.class);
//                i.putExtra("user", currUser);
//                i.putExtra("post", post);
//                context.startActivity(i);
//            }
        }
        private class GestureListener extends GestureDetector.SimpleOnGestureListener {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                //Log.i(TAG, "double tapped");
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Post post = posts.get(position);
                    Intent i = new Intent(context, ComposeReplyActivity.class);
                    i.putExtra("user", currUser);
                    i.putExtra("post", post);
                    context.startActivity(i);
                }
                return super.onDoubleTapEvent(e);
            }
        }
    }



}
