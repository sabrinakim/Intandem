package com.example.intandem.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
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

import com.example.intandem.BuildConfig;
import com.example.intandem.ComposeEventActivity;
import com.example.intandem.ComposeLocationActivity;
import com.example.intandem.GoogleMapsService;
import com.example.intandem.FilterDialogFragment;
import com.example.intandem.dataModels.DistanceInfo;
import com.example.intandem.dataModels.DistanceSearchResult;
import com.example.intandem.models.CustomPlace;
import com.example.intandem.models.Friendship;
import com.example.intandem.models.Post;
import com.example.intandem.PostsAdapter;
import com.example.intandem.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PostsFragment extends Fragment implements FilterDialogFragment.FilterDialogListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_USER = "user";
    public static final String TAG = "PostsFragment";
    public static final int LIMIT = 20;
    private static final String BASE_URL = "https://maps.googleapis.com/";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currLocation;
    private Double latitude;
    private Double longitude;
    private int maxDistance;
    private ViewPager2 vp2Posts;
    private PostsAdapter adapter;
    private List<Post> allPosts;
    private List<Post> filteredDistancePosts;
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
        filteredDistancePosts = new ArrayList<>();
        fabCompose = view.findViewById(R.id.fabAddPost);
        adapter = new PostsAdapter(getContext(), allPosts, mUser, currLocation);

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
                Intent i = new Intent(getContext(), ComposeLocationActivity.class);
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

//        queryPosts();

        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        findCurrentLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                }).check();
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

    private void findCurrentLocation() {
//        queryPosts();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // check if gps is enabled or not and then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(getActivity(), 51);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        // TODO: change token
//        queryPosts();
        CancellationTokenSource tokenSource = new CancellationTokenSource();
        CancellationToken token = tokenSource.getToken();
        fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, token)
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
//                            queryPosts();
                            currLocation = task.getResult();
                            if (currLocation != null) {
//                                queryPosts();
//                                adapter used to be set here
//                                adapter = new PostsAdapter(getContext(), allPosts, mUser, currLocation);
                                latitude = currLocation.getLatitude();
                                longitude = currLocation.getLongitude();
                                Log.i(TAG, "Lat: " + currLocation.getLatitude());
                                Log.i(TAG, "Long: " + currLocation.getLongitude());

                                queryPosts();

                            } else { // will execute when an updated location is received.
                                // idk
                            }

                        } else {
                            Log.e(TAG, "couldn't get current location");
                        }
                    }
                });
    }

    private void showEditDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FilterDialogFragment filterDialogFragment = FilterDialogFragment.newInstance();
        filterDialogFragment.setTargetFragment(this, 300);
        filterDialogFragment.show(fm, "fragment_filter_dialog");
    }

    private void queryPosts(int maxDistance) {
        ParseQuery<Friendship> queryFriends = ParseQuery.getQuery(Friendship.class);
        queryFriends.whereEqualTo("user1Id", mUser.get("fbId"));
        queryFriends.findInBackground(new FindCallback<Friendship>() {
            @Override
            public void done(List<Friendship> friendships, ParseException e) {
                Set<String> friendIds = new HashSet<>();
                friendIds.add((String) mUser.get("fbId"));
                for (Friendship friendship : friendships) {
                    friendIds.add(friendship.getUser2Id());
                }
                ParseQuery<Post> queryPosts = ParseQuery.getQuery(Post.class);
                queryPosts.setLimit(LIMIT);
                queryPosts.whereContainedIn(Post.KEY_USER_FB_ID, friendIds);
                Calendar rightNow = Calendar.getInstance();
                queryPosts.whereGreaterThan(Post.KEY_EXPIRATION, rightNow.getTime());
                queryPosts.findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> posts, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "error getting posts");
                            return;
                        }
                        Log.i(TAG, "success getting posts");
                        for (Post post : posts) {
                            ParseUser user = post.getUser();
                            try {
                                user.fetchIfNeeded();
                                Log.d(TAG, user.getUsername());
                                Log.d(TAG, post.getExpiration().toString());
                                Log.d(TAG, post.getCaption());
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                        }
                        allPosts.addAll(posts);
//                        adapter.notifyDataSetChanged();
                        // FILTERING BY DISTANCE
                        filteredDistancePosts.clear();

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        StringBuilder destinations = new StringBuilder();
                        for (int i = 0; i < allPosts.size() - 1; i++) {
                            CustomPlace customPlace = allPosts.get(i).getCustomPlace();
                            try {
                                customPlace.fetchIfNeeded();
                                destinations.append("place_id:").append(customPlace.getGPlaceId()).append("|");
                            } catch (ParseException ex) {
                                Log.e(TAG, ex.toString());
                                ex.printStackTrace();
                            }
                        }
                        CustomPlace customPlace = allPosts.get(allPosts.size() - 1).getCustomPlace();
                        try {
                            customPlace.fetchIfNeeded();
                            destinations.append("place_id:").append(customPlace.getGPlaceId());
                        } catch (ParseException ex) {
                            Log.e(TAG, ex.toString());
                            ex.printStackTrace();
                        }

                        GoogleMapsService googleMapsService = retrofit.create(GoogleMapsService.class);
                        googleMapsService.getDistanceSearchResult(latitude + "," + longitude,
                                destinations.toString(),
                                "driving",
                                "en",
                                BuildConfig.MAPS_API_KEY).enqueue(new Callback<DistanceSearchResult>() {
                            @Override
                            public void onResponse(Call<DistanceSearchResult> call, Response<DistanceSearchResult> response) {
                                DistanceSearchResult distanceSearchResult = response.body();
                                Log.i(TAG, "success getting all the distances");
                                List<DistanceInfo> elements = distanceSearchResult.getRows().get(0).getElements();
                                for (int i = 0; i < elements.size(); i++) {
                                    Log.d(TAG, "" + elements.get(i).getDistance().getValue() / 1000.0);
                                    if ((elements.get(i).getDistance().getValue() / 1000.0) <= maxDistance) {
                                        Log.d(TAG, "accepted: " + elements.get(i).getDistance().getValue() / 1000.0);
                                        filteredDistancePosts.add(allPosts.get(i));
                                    }
                                }
                                allPosts.clear();
                                allPosts.addAll(filteredDistancePosts);
                                // we created the list of filtered posts now.
                                adapter.notifyDataSetChanged();
                                //adapter.notifyItemInserted(0);
                            }

                            @Override
                            public void onFailure(Call<DistanceSearchResult> call, Throwable t) {
                                Log.e(TAG, "error getting distance");
                            }
                        });
                    }
                });
            }
        });
    }

    private void queryPosts() {
        queryPosts(Integer.MAX_VALUE);
    }

    @Override
    public void onFinishFilterDialog(int maxDistance) {
        Log.d(TAG, "AFTER USER CHOOSES TO FILTER");
        queryPosts(maxDistance);
    }
}