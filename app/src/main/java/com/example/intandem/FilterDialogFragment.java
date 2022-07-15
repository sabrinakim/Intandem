package com.example.intandem;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.intandem.dataModels.DistanceInfo;
import com.example.intandem.dataModels.DistanceSearchResult;
import com.example.intandem.models.CustomPlace;
import com.example.intandem.models.Post;
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

import java.util.ArrayList;
import java.util.List;

//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.Headers;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FilterDialogFragment extends DialogFragment {
    public static final String TAG = "FilterDialogFragment";
    private static final String BASE_URL = "https://maps.googleapis.com/";
    public static final String ARG_ALL_POSTS = "allPosts";
    public static final int LIMIT = 20;
    private EditText etDistance;
    private Button btnFilter;
    private FusedLocationProviderClient fusedLocationProviderClient;
    //private Location lastKnownLocation;
    private Location currLocation;
    //private LocationCallback locationCallback;
    private Double latitude;
    private Double longitude;
    private List<Post> filteredDistancePosts;
    private List<Post> mAllPosts;

    public interface FilterDialogListener {
        void onFinishFilterDialog(int maxDistance);
    }

    public FilterDialogFragment() {}

    public static FilterDialogFragment newInstance() {
        FilterDialogFragment fragment = new FilterDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAllPosts = getArguments().getParcelableArrayList(ARG_ALL_POSTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etDistance = view.findViewById(R.id.etDistance);
        btnFilter = view.findViewById(R.id.btnFilter);

        // set click listener on button
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterDialogListener listener = (FilterDialogListener) getTargetFragment();
                // saving user's entered distance
                int currMaxDistance = Integer.parseInt(etDistance.getText().toString());
                listener.onFinishFilterDialog(currMaxDistance);
                dismiss();
            }
        });
    }

    // TODO: GET RID OF BELOW FUNCTIONS EVENTUALLY

    private void queryFilteredPosts() {
        filteredDistancePosts = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // building destinations query parameter
        StringBuilder destinations = new StringBuilder();
        for (int i = 0; i < mAllPosts.size() - 1; i++) {
            CustomPlace customPlace = mAllPosts.get(i).getCustomPlace();
            try {
                customPlace.fetchIfNeeded();
                destinations.append("place_id:").append(customPlace.getGPlaceId()).append("|");
            } catch (ParseException ex) {
                Log.e(TAG, ex.toString());
                ex.printStackTrace();
            }
        }
        CustomPlace customPlace = mAllPosts.get(mAllPosts.size() - 1).getCustomPlace();
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
                    if ((elements.get(i).getDistance().getValue() / 1000.0) <= 0) {
                        Log.d(TAG, "accepted: " + elements.get(i).getDistance().getValue() / 1000.0);
                        filteredDistancePosts.add(mAllPosts.get(i));
                    }
                }
                // we created the list of filtered posts now.
                // triggers the parent activity to start its implemented function
                FilterDialogListener listener = (FilterDialogListener) getTargetFragment();
                //listener.onFinishFilterDialog(filteredDistancePosts);
                dismiss(); // exits out of dialog fragment.
            }

            @Override
            public void onFailure(Call<DistanceSearchResult> call, Throwable t) {
                Log.e(TAG, "error getting distance");
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (resultCode == Activity.RESULT_OK) { // user accepted request and enabled gps
                // now we can find user's current location.
                getDeviceLocation();
            }
        }
    }

    private void findCurrentLocation() {
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

                                queryFilteredPosts(); // updates var to contain filtered posts

                            } else { // will execute when an updated location is received.
                                // idk
                            }

                        } else {
                            Log.e(TAG, "couldn't get current location");
                        }
                    }
                });
    }

//    @SuppressLint("MissingPermission")
//    private void getDeviceLocation() {
//        fusedLocationProviderClient.getLastLocation()
//                .addOnCompleteListener(new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Location> task) {
//                        if (task.isSuccessful()) {
//                            lastKnownLocation = task.getResult(); // this could be null...
//                            if (lastKnownLocation != null) {
//                                Log.i(TAG, "Lat: " + lastKnownLocation.getLatitude());
//                                Log.i(TAG, "Long: " + lastKnownLocation.getLongitude());
//                            } else { // getting location update
//                                final LocationRequest locationRequest = LocationRequest.create();
//                                locationRequest.setInterval(10000);
//                                locationRequest.setFastestInterval(5000);
//                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//                                locationCallback = new LocationCallback() { // will execute when an updated location is received.
//                                    @Override
//                                    public void onLocationResult(LocationResult locationResult) {
//                                        super.onLocationResult(locationResult);
//                                        if (locationResult == null) {
//                                            return;
//                                        }
//                                        lastKnownLocation = locationResult.getLastLocation();
//                                        Log.i(TAG, "Lat: " + lastKnownLocation.getLatitude());
//                                        Log.i(TAG, "Long: " + lastKnownLocation.getLongitude());
//                                        // stops us from recursively getting location updates.
//                                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//                                    }
//                                };
//                                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
//                            }
//                        } else {
//                            Toast.makeText(getContext(), "unable to get last location", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

}
