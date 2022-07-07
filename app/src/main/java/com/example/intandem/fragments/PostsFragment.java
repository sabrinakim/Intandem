package com.example.intandem.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.intandem.ComposeEventActivity;
import com.example.intandem.DistanceMatrixService;
import com.example.intandem.FilterDialogFragment;
import com.example.intandem.models.Post;
import com.example.intandem.PostsAdapter;
import com.example.intandem.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

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

        // using retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DistanceMatrixService distanceMatrixService = retrofit.create(DistanceMatrixService.class);
//        distanceMatrixService.getDistanceSearchResult("place_id:ChIJ98rot0a_j4AR1IjYiTsx2oo",
//                "place_id:ChIJhXcepTW7j4ARkdzoQMZEBoU",
//                "driving",
//                "en",
//                BuildConfig.MAPS_API_KEY).enqueue(new Callback<DistanceSearchResult>() {
//            @Override
//            public void onResponse(Call<DistanceSearchResult> call, Response<DistanceSearchResult> response) {
//                Log.i(TAG, "on response");
//                DistanceSearchResult distanceSearchResult = response.body();
//                System.out.println("distance from place 1 to place 2: " + distanceSearchResult.getRows().get(0).getElements().get(0).getDistance().getText());
//            }
//
//            @Override
//            public void onFailure(Call<DistanceSearchResult> call, Throwable t) {
//                Log.e(TAG, "on failure");
//            }
//        });

        vp2Posts = view.findViewById(R.id.vp2Posts);
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts, mUser);
        fabCompose = view.findViewById(R.id.fabAddPost);

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
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getCaption());
                }
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onFinishFilterDialog(List<Post> filteredPosts) {
        allPosts.clear();
        allPosts.addAll(filteredPosts);
        adapter.notifyDataSetChanged();
    }
}