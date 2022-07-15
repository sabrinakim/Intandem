package com.example.intandem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.RoundedCorner;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.intandem.fragments.RepliesFragment;
import com.example.intandem.fragments.ReviewsFragment;
import com.example.intandem.models.CustomPlace;
import com.example.intandem.models.Post;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.util.Calendar;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public static final String TAG = "PostsAdapter";
    Context context;
    private List<Post> posts;
    private ParseUser currUser;

    public PostsAdapter(Context context, List<Post> posts, ParseUser currUser, Location currLocation) {
        this.context = context;
        this.posts = posts;
        this.currUser = currUser;
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

        TextView tvLocationFeed, tvCaptionFeed, tvName, tvExpiration;
        ImageView ivPictureFeed, ivProfilePicture;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            tvEventFeed = itemView.findViewById(R.id.tvEventFeed);
            tvLocationFeed = itemView.findViewById(R.id.tvLocationFeed);
            tvCaptionFeed = itemView.findViewById(R.id.tvCaptionFeed);
            ivPictureFeed = itemView.findViewById(R.id.ivPictureFeed);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePic);
            tvName = itemView.findViewById(R.id.tvName);
            tvExpiration = itemView.findViewById(R.id.tvExpiration);
            itemView.setOnClickListener(this);
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
            tvExpiration.setText("3 hr Left");

            CustomPlace c = post.getCustomPlace();
            c.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "error fetching if needed in background");
                    }
                    tvLocationFeed.setText(post.getCustomPlace().getName());
                    tvLocationFeed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppCompatActivity activity = (AppCompatActivity) v.getContext();
                            Fragment fragment = ReviewsFragment.newInstance(post);
                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
                        }
                    });
                }
            });

            ParseFile image = post.getPicture();
//            if (image != null) {
//                Calendar rightNow = Calendar.getInstance();
//                if (rightNow.getTime().after(post.getExpiration())) {
//                    try {
//                        File photoFile = image.getFile();
//                        Bitmap postPhoto = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
//                        Bitmap grayedPostPhoto = grayOutImage(postPhoto);
//                        ivPictureFeed.setImageBitmap(grayedPostPhoto);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Glide.with(context).load(image.getUrl()).into(ivPictureFeed);
//                }
//            }


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
            if (position != RecyclerView.NO_POSITION) {
                Post post = posts.get(position);
                Intent i = new Intent (context, ReplyActivity.class);
                i.putExtra("user", currUser);
                i.putExtra("post", post);
                context.startActivity(i);
            }
        }
    }

    private Bitmap grayOutImage(Bitmap src) {
        // constant factors
        final double G_RED = 0.299;
        final double G_GREEN = 0.587;
        final double G_BLUE = 0.114;
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        // pixel information
        int A, R, G, B;
        int pixel;
        // get image size
        int width = src.getWidth();
        int height = src.getHeight();
        // scan through every single pixel
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get one pixel color
                pixel = src.getPixel(x, y);
                // retrieve color of all channels
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // take conversion up to one single value
                R = G = B = (int)(G_RED * R + G_GREEN * G + G_BLUE * B);
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        // return final image
        return bmOut;
    }

}
