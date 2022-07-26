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

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.intandem.models.Post;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseUser;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public static final String TAG = "PostsAdapter";
    Context context;
    private List<Post> posts;
    private ParseUser currUser;
    private Location currLocation;
    private ViewPager2 vp2Posts;

    public PostsAdapter(Context context, List<Post> posts, ParseUser currUser, Location currLocation, ViewPager2 vp2Posts) {
        this.context = context;
        this.posts = posts;
        this.currUser = currUser;
        this.currLocation = currLocation;
        this.vp2Posts = vp2Posts;
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
        TabLayout tabLayout;
        ViewPager2 viewPager2;
        MyViewPagerAdapter myViewPagerAdapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tabLayout = itemView.findViewById(R.id.postTabs);
            viewPager2 = itemView.findViewById(R.id.viewPager2);

            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    if (position == 0) {
                        viewPager2.setUserInputEnabled(true);
                        vp2Posts.setUserInputEnabled(true);
                    } else {
                        viewPager2.setUserInputEnabled(false);
                        vp2Posts.setUserInputEnabled(false);
                    }

                }
            });

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager2.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    tabLayout.getTabAt(position).select();
                }
            });

//            tvLocationFeed = itemView.findViewById(R.id.tvLocationFeed);
//            tvCaptionFeed = itemView.findViewById(R.id.tvCaptionFeed);
//            ivPictureFeed = itemView.findViewById(R.id.ivPictureFeed);
//            ivProfilePicture = itemView.findViewById(R.id.ivProfilePic);
//            tvName = itemView.findViewById(R.id.tvName);
//            tvExpiration = itemView.findViewById(R.id.tvExpiration);
//            tvMoreData = itemView.findViewById(R.id.tvMoreData);
//            btnViewReplies = itemView.findViewById(R.id.btnViewReplies);
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
            myViewPagerAdapter = new MyViewPagerAdapter((FragmentActivity) context, post, currLocation);
            viewPager2.setAdapter(myViewPagerAdapter);
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "CLICK");
        }

        private class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.i(TAG, "double tapped");
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
