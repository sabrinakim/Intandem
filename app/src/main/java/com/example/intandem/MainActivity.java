package com.example.intandem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.intandem.dataModels.DistanceInfo;
import com.example.intandem.dataModels.DistanceSearchResult;
import com.example.intandem.models.CustomPlace;
import com.example.intandem.models.Friendship;
import com.example.intandem.models.Post;
import com.facebook.login.LoginManager;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.simform.refresh.SSPullToRefreshLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private ParseUser user;
    public static final int LIMIT = 20;
    private static final String BASE_URL = "https://maps.googleapis.com/";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currLocation = new Location("");
    private Double latitude, longitude;
    private int maxDistance, currPage, numPostsFetched;
    private ViewPager2 vp2Posts;
    private PostsAdapter adapter;
    private List<Post> allPosts, filteredDistancePosts;
    private FloatingActionButton fabCompose;
    private Set<String> friendIds = new HashSet<>();
    private CircleImageView currUserProfileImage;
    private ImageButton ibFilter;
    private Toolbar homeToolbar;
    private LottieAnimationView walkingBlob;
    private TextView tvLoadingMsg, tvMaxDistance;
    private SSPullToRefreshLayout pullToRefresh;
    private Slider distSlider;
    private FrameLayout filterBottomSheet;
    private BottomSheetBehavior<FrameLayout> bottomSheetBehavior;
    private Button btnCancelFilter;
    private boolean filterOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //String s = removeParens("cicken meets rice");


        currUserProfileImage = findViewById(R.id.toolbarProfileImage);
        walkingBlob = findViewById(R.id.walkingBlob);
        tvLoadingMsg = findViewById(R.id.tvLoadingMsg);
        homeToolbar = findViewById(R.id.homeToolbar);
        ibFilter = findViewById(R.id.ibFilter);
        vp2Posts = findViewById(R.id.vp2Posts);
        fabCompose = findViewById(R.id.fabAddPost);
        distSlider = findViewById(R.id.distSlider);
        tvMaxDistance = findViewById(R.id.tvMaxDistance);
        filterBottomSheet = findViewById(R.id.filterBottomSheet);
        btnCancelFilter = findViewById(R.id.btnCancelFilter);

        fabCompose.setVisibility(View.INVISIBLE);

        user = getIntent().getParcelableExtra("user");
        Glide.with(this).load(user.getString("pictureUrl")).into(currUserProfileImage);

        bottomSheetBehavior = BottomSheetBehavior.from(filterBottomSheet);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        distSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                String val;
                if (value == 0.0) {
                    val = "> 100 km";
                } else {
                    val = "" + value + " km";
                }
                tvMaxDistance.setText(val);
            }
        });

        ibFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filterOn) {
                    filterOn = false;
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    filterOn = true;
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });

        btnCancelFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                // filter here
                float val = distSlider.getValue();
                if (val == 0.0) {
                    if (maxDistance == -1) {
                        // do nothing
                    } else {
                        maxDistance = -1;
                        queryPosts();
                    }
                } else {
                    maxDistance = Math.round(val);
                    queryPosts();
                }
            }
        });

        currUserProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                i.putExtra("user", user);
                startActivity(i);
            }
        });

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setSupportActionBar(homeToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fabCompose.setVisibility(View.INVISIBLE);
                //circle.setVisibility(View.VISIBLE);
                //animation.start();
                Intent i = new Intent(MainActivity.this, ComposeLocationActivity.class);
                i.putExtra("user", user);
                startActivity(i);
                overridePendingTransition(R.anim.slide_up_in, R.anim.nothing);
            }
        });

        allPosts = new ArrayList<>();
        filteredDistancePosts = new ArrayList<>();
        maxDistance = -1;
        adapter = new PostsAdapter(this, allPosts, user, currLocation, vp2Posts);

        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setLottieAnimation("loading_balls.json");
        pullToRefresh.setRepeatMode(SSPullToRefreshLayout.RepeatMode.REPEAT);
        pullToRefresh.setRepeatCount(SSPullToRefreshLayout.RepeatCount.INFINITE);
        pullToRefresh.setOnRefreshListener(new SSPullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                allPosts.clear();
                queryPosts();
            }
        });


        vp2Posts.setAdapter(adapter);
        vp2Posts.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            int previousState = ViewPager2.SCROLL_STATE_IDLE;

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
                if (previousState == ViewPager2.SCROLL_STATE_DRAGGING && state == ViewPager2.SCROLL_STATE_IDLE
                        && numPostsFetched > 0) {
                    Log.d(TAG, "OVERSCROLLED");
                    Log.i(TAG, "curr page " + currPage);
                    getMorePosts(currPage);
                    currPage++;
                }
                previousState = state;
            }
        });

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        findCurrentLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        if (permissionDeniedResponse.isPermanentlyDenied()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Permission Denied")
                                    .setMessage("Permission to access device location is permanently denied." +
                                            "You need to go to settings to allow the permission")
                                    .setNegativeButton("Cancel", null)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent();
                                            i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            // idk what this part does
                                            i.setData(Uri.fromParts("package", MainActivity.this.getPackageName(), null));
                                        }
                                    }).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private String removeParens(String s) {
        int openParenIdx = -1;
        int closeParenIdx = -1;
        if (s.length() == 0) {
            return "";
        }
        // fdajdal; (fdjal) fjdlka;jdla (jl;) jfkdla; (fd)
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                openParenIdx = i;
            } else if (s.charAt(i) == ')') {
                closeParenIdx = i;
            }
            if (openParenIdx != -1 && closeParenIdx != -1) {
                // assuming space precedes and succeeds paren.
                if (closeParenIdx == s.length() - 1) {
                    return s.substring(0, openParenIdx - 1) + removeParens("");
                }
                return s.substring(0, openParenIdx - 1) + removeParens(s.substring(closeParenIdx + 2));
            }
        }
        return s;
    }


    private void getMorePosts(int currPage) {
        numPostsFetched = 0;
        ParseQuery<Post> queryPosts = ParseQuery.getQuery(Post.class);
        queryPosts.setLimit(LIMIT);
        queryPosts.whereContainedIn(Post.KEY_USER_FB_ID, friendIds);
        Calendar rightNow = Calendar.getInstance();
        queryPosts.whereGreaterThan(Post.KEY_EXPIRATION, rightNow.getTime());
        queryPosts.setSkip(currPage * LIMIT);
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
                filterPosts(posts);
            }
        });
    }


    private void findCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // check if gps is enabled or not and then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(MainActivity.this, 51);
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
        CancellationTokenSource tokenSource = new CancellationTokenSource();
        CancellationToken token = tokenSource.getToken();
        fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, token)
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Location fetchedLocation = task.getResult();
                            if (fetchedLocation != null) {
                                latitude = fetchedLocation.getLatitude();
                                longitude = fetchedLocation.getLongitude();
                                Log.i(TAG, "Lat: " + fetchedLocation.getLatitude());
                                Log.i(TAG, "Long: " + fetchedLocation.getLongitude());

                                currLocation.setLatitude(latitude);
                                currLocation.setLongitude(longitude);

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
        FragmentManager fm = this.getSupportFragmentManager();
        FilterDialogFragment filterDialogFragment = FilterDialogFragment.newInstance();
//        filterDialogFragment.setTargetFragment(this, 300);
        filterDialogFragment.show(fm, "fragment_filter_dialog");
    }

    private void queryPosts() {
        allPosts.clear();
        currPage = 1;
        numPostsFetched = 0;
        ParseQuery<Friendship> queryFriends = ParseQuery.getQuery(Friendship.class);
        queryFriends.whereEqualTo("user1Id", user.get("fbId"));
        queryFriends.findInBackground(new FindCallback<Friendship>() {
            @Override
            public void done(List<Friendship> friendships, ParseException e) {
                friendIds.add((String) user.get("fbId"));
                for (Friendship friendship : friendships) {
                    friendIds.add(friendship.getUser2Id());
                }
                ParseQuery<Post> queryPosts = ParseQuery.getQuery(Post.class);
                queryPosts.setLimit(LIMIT);
                queryPosts.whereContainedIn(Post.KEY_USER_FB_ID, friendIds);
                Calendar rightNow = Calendar.getInstance();
                queryPosts.whereGreaterThan(Post.KEY_EXPIRATION, rightNow.getTime());
                queryPosts.addDescendingOrder(Post.KEY_CREATEDAT);
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
                        filterPosts(posts);
                    }
                });
            }
        });
    }

    private void filterPosts(List<Post> posts) {
        allPosts.addAll(posts);
        if (maxDistance == -1) {
            numPostsFetched = posts.size();
            walkingBlob.cancelAnimation();
            walkingBlob.setVisibility(View.INVISIBLE);
            tvLoadingMsg.setVisibility(View.INVISIBLE);
            fabCompose.setVisibility(View.VISIBLE);
            pullToRefresh.setRefreshing(false);
            adapter.notifyDataSetChanged();
            return;
        }
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
                numPostsFetched = filteredDistancePosts.size();
                allPosts.addAll(filteredDistancePosts);
                walkingBlob.cancelAnimation();
                walkingBlob.setVisibility(View.INVISIBLE);
                tvLoadingMsg.setVisibility(View.INVISIBLE);
                fabCompose.setVisibility(View.VISIBLE);
                pullToRefresh.setRefreshing(false);
                // we created the list of filtered posts now.
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<DistanceSearchResult> call, Throwable t) {
                Log.e(TAG, "error getting distance");
            }
        });
    }
}