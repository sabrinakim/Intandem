package com.example.intandem.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcel;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.intandem.BuildConfig;
import com.example.intandem.ComposeReplyActivity;
import com.example.intandem.DateDiff;
import com.example.intandem.GoogleMapsService;
import com.example.intandem.R;
import com.example.intandem.ViewRepliesActivity;
import com.example.intandem.dataModels.DistanceSearchResult;
import com.example.intandem.models.CustomPlace;
import com.example.intandem.models.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.text.ParseException;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PictureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PictureFragment extends Fragment {

    private static final String ARG_CURR_POST = "currPost";
    public static final String TAG = "PictureFragment";
    private static final String ARG_CURR_LOCATION = "currLocation";
    public static final String ARG_CURR_USER = "currUser";
    private static final String BASE_URL = "https://maps.googleapis.com/";
    private Post currPost;
    private Location currLocation;
    private ParseUser currUser;
    private TextView tvLocationFeed, tvCaptionFeed, tvName, tvExpiration, tvMoreData;
    private ImageView ivPictureFeed, ivProfilePicture;
    private ImageButton btnViewReplies;
    private boolean durationFlag = false;

    public PictureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param currPost Parameter 1.
     * @return A new instance of fragment PictureFragment.
     */
    public static PictureFragment newInstance(Post currPost, Location currLocation, ParseUser currUser) {
        PictureFragment fragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CURR_POST, currPost);
        args.putParcelable(ARG_CURR_LOCATION, currLocation);
        args.putParcelable(ARG_CURR_USER, currUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currPost = getArguments().getParcelable(ARG_CURR_POST);
            currLocation = getArguments().getParcelable(ARG_CURR_LOCATION);
            currUser = getArguments().getParcelable(ARG_CURR_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_picture, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

            tvLocationFeed = view.findViewById(R.id.tvLocationFeed);
            tvCaptionFeed = view.findViewById(R.id.tvCaptionFeed);
            ivPictureFeed = view.findViewById(R.id.ivPictureFeed);
            ivProfilePicture = view.findViewById(R.id.ivProfilePic);
            tvName = view.findViewById(R.id.tvName);
            tvExpiration = view.findViewById(R.id.tvExpiration);
            tvMoreData = view.findViewById(R.id.tvMoreData);
            btnViewReplies = view.findViewById(R.id.btnViewReplies);

            tvName.setText(currPost.getUser().getString("firstName"));

            Calendar rightNow = Calendar.getInstance();
            String timeLeft = DateDiff.findDifference(rightNow.getTime(), currPost.getExpiration());

            tvExpiration.setText(timeLeft + " Left");

            btnViewReplies.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), ViewRepliesActivity.class);
                    i.putExtra("currPost", currPost);
                    getContext().startActivity(i);
                }
            });

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            GoogleMapsService googleMapsService = retrofit.create(GoogleMapsService.class);
            CustomPlace customPlace = currPost.getCustomPlace();
            try {
                customPlace.fetchIfNeeded();
                tvLocationFeed.setText(currPost.getCustomPlace().getName());
                googleMapsService.getDistanceSearchResult(currLocation.getLatitude() + "," + currLocation.getLongitude(),
                        "place_id:" + currPost.getCustomPlace().getGPlaceId(),
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
                                if (!durationFlag) {
                                    durationFlag = true;
                                    String duration = distanceSearchResult.getRows().get(0).getElements()
                                            .get(0).getDuration().getText() + " by car";
                                    tvMoreData.setText(duration);
                                } else {
                                    durationFlag = false;
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
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }

            ParseFile image = currPost.getPicture();
            if (image != null) { // image is optional, so its possible that it is null
                Glide.with(getContext()).load(image.getUrl()).into(ivPictureFeed);
            }

            Glide.with(getContext()).load(currUser.getString("pictureUrl"))
                    .transform(new CircleCrop())
                    .into(ivProfilePicture);

            tvCaptionFeed.setText(currPost.getCaption());

        GestureDetector gestureDetector = new GestureDetector(getContext().getApplicationContext(), new GestureListener());
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "touched");
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i(TAG, "double tapped");
            Intent i = new Intent(getContext(), ComposeReplyActivity.class);
            i.putExtra("user", currUser);
            i.putExtra("post", currPost);
            getContext().startActivity(i);
            return super.onDoubleTapEvent(e);
        }
    }
}