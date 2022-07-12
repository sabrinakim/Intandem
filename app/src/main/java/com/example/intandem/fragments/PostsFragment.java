package com.example.intandem.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.intandem.ComposeEventActivity;
import com.example.intandem.GoogleMapsService;
import com.example.intandem.FilterDialogFragment;
import com.example.intandem.models.Friendship;
import com.example.intandem.models.Post;
import com.example.intandem.PostsAdapter;
import com.example.intandem.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PostsFragment extends Fragment implements FilterDialogFragment.FilterDialogListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_USER = "user";
    public static final String TAG = "PostsFragment";
    public static final int LIMIT = 20;
    private static final String BASE_URL = "https://maps.googleapis.com/";
    private ViewPager2 vp2Posts;
    private PostsAdapter adapter;
    private List<Post> allPosts;
    private FloatingActionButton fabCompose;
    private SwipeRefreshLayout swipeContainer;

    // TODO: Rename and change types of parameters
    private ParseUser mUser;

    public PostsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostsFragment newInstance(ParseUser user) {
        PostsFragment fragment = new PostsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mUser = getArguments().getParcelable(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vp2Posts = view.findViewById(R.id.vp2Posts);
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts, mUser);
        fabCompose = view.findViewById(R.id.fabAddPost);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                allPosts.clear();
                adapter.notifyDataSetChanged();
                queryPosts();
                swipeContainer.setRefreshing(false);
            }
        });

        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ComposeEventActivity.class);
                i.putExtra("user", mUser);
                startActivity(i);
            }
        });

        vp2Posts.setAdapter(adapter);
        vp2Posts.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            // This method is triggered when there is any scrolling activity for the current page
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            // triggered when you select a new page
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            // triggered when there is
            // scroll state will be changed
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        queryPosts();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            return false;
        }
        if (item.getItemId() == R.id.filter) {
            showEditDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEditDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FilterDialogFragment filterDialogFragment = FilterDialogFragment.newInstance();
        filterDialogFragment.setTargetFragment(this, 300);
        filterDialogFragment.show(fm, "fragment_filter_dialog");
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(LIMIT);
        query.addAscendingOrder("expiration");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                Log.i(TAG, "success getting posts");
                filterPostsAndNotify(posts);
            }
        });
    }

    @Override
    public void onFinishFilterDialog(List<Post> filteredPosts) {
        Log.d(TAG, "AFTER USER CHOOSES TO FILTER");
        allPosts.clear();
        filterPostsAndNotify(filteredPosts);
    }

    private void filterPostsAndNotify(List<Post> postsToFilter) {
        ParseQuery<Friendship> queryFriends = ParseQuery.getQuery(Friendship.class);
        queryFriends.whereEqualTo("user1Id", mUser.get("fbId"));
        queryFriends.findInBackground(new FindCallback<Friendship>() {
            @Override
            public void done(List<Friendship> friendships, ParseException e) {
                Set<String> friendIds = new HashSet<>();
                for (Friendship friendship : friendships) {
                    friendIds.add(friendship.getUser2Id());
                }
                for (Post post : postsToFilter) {
                    try {
                        ParseUser user = post.getUser().fetchIfNeeded();
                        if (friendIds.contains(user.get("fbId")) || user.get("fbId").equals(mUser.get("fbId"))) {
                            Calendar rightNow = Calendar.getInstance();
                            if (rightNow.getTime().before(post.getExpiration())) {
                                Log.d(TAG, "exp :" + post.getExpiration().toString());
                                allPosts.add(post);
                            }
                        }

                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                }
                for (Post post : allPosts) {
                    Log.d(TAG, "user: " + post.getUser().getUsername());
                    Log.d(TAG, "caption: " + post.getCaption());
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}