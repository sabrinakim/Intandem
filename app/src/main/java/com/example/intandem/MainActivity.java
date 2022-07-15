package com.example.intandem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
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
import android.view.View;
import android.widget.Toast;

import com.example.intandem.dataModels.DistanceInfo;
import com.example.intandem.dataModels.DistanceSearchResult;
import com.example.intandem.fragments.ActivityFragment;
import com.example.intandem.fragments.ComposeFragment;
import com.example.intandem.fragments.PostsFragment;
import com.example.intandem.fragments.ProfileFragment;
import com.example.intandem.models.CustomPlace;
import com.example.intandem.models.Friendship;
import com.example.intandem.models.Post;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.HttpUrl;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import okhttp3.ResponseBody;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
//    private static final String BASE_URL = "https://api.yelp.com/v3/";
//    private static final String API_KEY = "fq038-wNNvkjlvvsz_fBqD8a2Bl-mVUT1XHXz_-EJEZS-8SEO6OoynOpQmgTf5-Y7_Ujsc9LKl5TPJ_6Y2NdFPBVCUeC6v6r0wT3_uee4B2lJLldWP4rfKKqWVizYnYx";
//    final FragmentManager fragmentManager = getSupportFragmentManager();
//    private BottomNavigationView bottomNavigationView;
    private ParseUser user;
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
    private Set<String> friendIds = new HashSet<>();
    private int currPage;
    private int numPostsFetched;
    private CircleImageView currUserProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currUserProfileImage = findViewById(R.id.custom_profile_image);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#000000"));
        actionBar.setBackgroundDrawable(colorDrawable);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.custom_bar, null);
        actionBar.setCustomView(action_bar_view);


        // unwrap parcel here
        user = getIntent().getParcelableExtra("user");

        vp2Posts = findViewById(R.id.vp2Posts);
        allPosts = new ArrayList<>();
        filteredDistancePosts = new ArrayList<>();
        //fabCompose = view.findViewById(R.id.fabAddPost);
        maxDistance = -1;
        adapter = new PostsAdapter(this, allPosts, user, currLocation);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

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



//        fabCompose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(MainActivity.this, ComposeLocationActivity.class);
//                i.putExtra("user", user);
//                startActivity(i);
//            }
//        });

        vp2Posts.setAdapter(adapter);
        vp2Posts.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            int previousState = ViewPager2.SCROLL_STATE_IDLE;
            //final int[] previousState = {ViewPager2.SCROLL_STATE_IDLE};

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            LoginManager.getInstance().logOut();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish(); // doesn't let you go back to main activity once logged out
            return true;
        }
        if (item.getItemId() == R.id.filter) {
            showEditDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                            currLocation = task.getResult();
                            if (currLocation != null) {
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
        FragmentManager fm = this.getSupportFragmentManager();
        FilterDialogFragment filterDialogFragment = FilterDialogFragment.newInstance();
//        filterDialogFragment.setTargetFragment(this, 300);
        filterDialogFragment.show(fm, "fragment_filter_dialog");
    }

    private void queryPosts() {
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

//    @Override
//    public void onFinishFilterDialog(int maxDistance) {
//        Log.d(TAG, "AFTER USER CHOOSES TO FILTER");
//        this.maxDistance = maxDistance;
//        allPosts.clear();
//        adapter.notifyDataSetChanged();
//        queryPosts();
//    }

    private void filterPosts(List<Post> posts) {
        allPosts.addAll(posts);
        if (maxDistance == -1) {
            numPostsFetched = posts.size();
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