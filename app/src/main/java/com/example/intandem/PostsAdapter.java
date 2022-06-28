package com.example.intandem;

import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.text.Layout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.intandem.fragments.ActivityFragment;
import com.example.intandem.fragments.RepliesFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public static final String TAG = "PostsAdapter";
    Context context;
    private List<Post> posts;
    private ParseUser currUser;

    public PostsAdapter(Context context, List<Post> posts, ParseUser currUser) {
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

        TextView tvEventFeed, tvLocationFeed, tvCaptionFeed;
        ImageView ivPictureFeed;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventFeed = itemView.findViewById(R.id.tvEventFeed);
            tvLocationFeed = itemView.findViewById(R.id.tvLocationFeed);
            tvCaptionFeed = itemView.findViewById(R.id.tvCaptionFeed);
            ivPictureFeed = itemView.findViewById(R.id.ivPictureFeed);
            itemView.setOnClickListener(this);
        }

        public void bind(Post post) {
            tvEventFeed.setText(post.getEvent());

            tvEventFeed.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // navigate to replies fragment
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    // TODO: pass in post object as parameter instead of current user
                    Fragment fragment = RepliesFragment.newInstance(currUser);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
                }
            });

            // we want to extract place name from place id...
            String placeId = post.getPlaceId();
            List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,
                    Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ID);
            FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, fieldList);

            // Initialize the SDK
            Places.initialize(context.getApplicationContext(), BuildConfig.MAPS_API_KEY);
            // Create a new PlacesClient instance
            PlacesClient placesClient = Places.createClient(context);

            Task<FetchPlaceResponse> fetchPlaceResponseTask = placesClient.fetchPlace(request);
            fetchPlaceResponseTask.addOnCompleteListener(new OnCompleteListener<FetchPlaceResponse>() {
                @Override
                public void onComplete(@NonNull Task<FetchPlaceResponse> task) {
                    String currPlaceName = task.getResult().getPlace().getName();
                    tvLocationFeed.setText(currPlaceName);
                }
            });

            ParseFile image = post.getPicture();
            if (image != null) { // image is optional, so its possible that it is null
                Glide.with(context).load(image.getUrl()).into(ivPictureFeed);
            }

            tvCaptionFeed.setText(post.getCaption());
        }


        @Override
        public void onClick(View v) {
            //Toast.makeText(context, "reply", Toast.LENGTH_SHORT).show();
            Intent i = new Intent (context, ReplyActivity.class);
            i.putExtra("user", currUser);
            context.startActivity(i);
        }
    }
}
