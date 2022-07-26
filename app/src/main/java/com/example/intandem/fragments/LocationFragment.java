package com.example.intandem.fragments;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.intandem.MainActivity;
import com.example.intandem.R;
import com.example.intandem.ReviewsAdapter;
import com.example.intandem.models.CustomPlace;
import com.example.intandem.models.CustomPlaceToReview;
import com.example.intandem.models.Post;
import com.example.intandem.models.Review;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationFragment extends Fragment {

    public static final String TAG = "LocationFragment";
    private static final String ARG_CURR_POST = "currPost";
    private static final String ARG_CURR_LOCATION = "currLocation";
    private Post currPost;
    private Location currLocation;
    private FrameLayout standardBottomSheet;
    private BottomSheetBehavior<FrameLayout> bottomSheetBehavior;
    private ImageButton btnExpand, btnShrink;
    private ImageView ivPlaceImage;
    private TextView tvLocationReviews;
    private RatingBar ratingsMerged;
    private RecyclerView rvReviewsBottomSheet;
    private ReviewsAdapter reviewsAdapter;
    private List<Review> allReviews;

    public LocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocationFragment newInstance(Post currPost, Location currLocation) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CURR_POST, currPost);
        args.putParcelable(ARG_CURR_LOCATION, currLocation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currPost = getArguments().getParcelable(ARG_CURR_POST);
            currLocation = getArguments().getParcelable(ARG_CURR_LOCATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // when map is loaded
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                LatLng youLatLong = new LatLng(currLocation.getLatitude(), currLocation.getLongitude());
                Marker youMarker = googleMap.addMarker(new MarkerOptions()
                        .position(youLatLong)
                        .title("You"));
                builder.include(youMarker.getPosition());
                CustomPlace customPlace = currPost.getCustomPlace();
                try {
                    customPlace.fetchIfNeeded();
                    LatLng friendLatLong = new LatLng(customPlace.getLat(), customPlace.getLong());
                    Marker friendMarker = googleMap.addMarker(new MarkerOptions()
                            .position(friendLatLong)
                            .title(currPost.getUser().getString("firstName")));
                    builder.include(friendMarker.getPosition());
                    LatLngBounds bounds = builder.build();

                    int padding = 200; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    googleMap.moveCamera(cu);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        standardBottomSheet = view.findViewById(R.id.standardBottomSheet);
        btnExpand = view.findViewById(R.id.btnExpand);
        btnShrink = view.findViewById(R.id.btnShrink);
        ivPlaceImage = view.findViewById(R.id.ivPlaceImage);
        tvLocationReviews = view.findViewById(R.id.tvLocationReviews);
        ratingsMerged = view.findViewById(R.id.ratingsMerged);

        bottomSheetBehavior = BottomSheetBehavior.from(standardBottomSheet);
        btnShrink.setVisibility(View.INVISIBLE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(200);

        btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnExpand.setVisibility(View.INVISIBLE);
                btnShrink.setVisibility(View.VISIBLE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        btnShrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnShrink.setVisibility(View.INVISIBLE);
                btnExpand.setVisibility(View.VISIBLE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        CustomPlace customPlace = currPost.getCustomPlace();
        try {
            customPlace.fetchIfNeeded();
            String imageUrl = customPlace.getPlaceImageUrl();
            if (imageUrl != null) { // image is optional, so its possible that it is null
                Glide.with(getContext()).load(imageUrl).into(ivPlaceImage);
            }
            tvLocationReviews.setText(customPlace.getName());
            ratingsMerged.setRating(((Double) customPlace.getRating()).floatValue());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        rvReviewsBottomSheet = view.findViewById(R.id.rvReviewsBottomSheet);
        allReviews = new ArrayList<>();
        reviewsAdapter = new ReviewsAdapter(getContext(), allReviews);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvReviewsBottomSheet.setAdapter(reviewsAdapter);
        rvReviewsBottomSheet.setLayoutManager(linearLayoutManager);
        queryReviews();

    }

    private void queryReviews() {
        ParseQuery<CustomPlaceToReview> queryMapping = ParseQuery.getQuery(CustomPlaceToReview.class);
        queryMapping.whereEqualTo(CustomPlaceToReview.KEY_CUSTOMPLACE, currPost.getCustomPlace());
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
                reviewsAdapter.notifyDataSetChanged();
            }
        });
    }
}