package com.example.intandem.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.intandem.R;
import com.example.intandem.RepliesAdapter;
import com.example.intandem.ReviewsAdapter;
import com.example.intandem.models.CustomPlaceToReview;
import com.example.intandem.models.Post;
import com.example.intandem.models.Reply;
import com.example.intandem.models.Review;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewsFragment extends Fragment {


    private static final String ARG_POST = "post";
    public static final String TAG = "ReviewsFragment";
    private Post mPost;
    private RecyclerView rvReviews;
    private List<Review> allReviews;
    private ReviewsAdapter adapter;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param post Parameter 1.
     * @return A new instance of fragment ReviewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewsFragment newInstance(Post post) {
        ReviewsFragment fragment = new ReviewsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_POST, post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPost = getArguments().getParcelable(ARG_POST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reviews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvReviews = view.findViewById(R.id.rvReviews);
        allReviews = new ArrayList<>();
        adapter = new ReviewsAdapter(getContext(), allReviews);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvReviews.setAdapter(adapter);
        rvReviews.setLayoutManager(linearLayoutManager);
        queryReviews();
    }

    private void queryReviews() {
        ParseQuery<CustomPlaceToReview> queryMapping = ParseQuery.getQuery(CustomPlaceToReview.class);
        queryMapping.whereEqualTo(CustomPlaceToReview.KEY_CUSTOMPLACE, mPost.getCustomPlace());
        queryMapping.findInBackground(new FindCallback<CustomPlaceToReview>() {
            @Override
            public void done(List<CustomPlaceToReview> mappings, ParseException e) {
                for (CustomPlaceToReview mapping : mappings) {
                    Review review = mapping.getReview();
                    try {
                        review.fetchIfNeeded();
                        Log.d(TAG, "" + mapping.getReview().getRating());
                        allReviews.add(mapping.getReview());
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}