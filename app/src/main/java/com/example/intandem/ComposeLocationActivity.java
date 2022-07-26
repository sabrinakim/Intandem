package com.example.intandem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.intandem.dataModels.Business;
import com.example.intandem.dataModels.BusinessSearchResult;
import com.example.intandem.dataModels.GoogleReview;
import com.example.intandem.dataModels.PlaceDetailsSearchResult;
import com.example.intandem.dataModels.ReviewSearchResult;
import com.example.intandem.dataModels.YelpReview;
import com.example.intandem.models.CustomPlace;
import com.example.intandem.models.CustomPlaceToReview;
import com.example.intandem.models.Review;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.List;

import bolts.Task;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ComposeLocationActivity extends AppCompatActivity {

    private static String TAG = "ComposeLocationActivity";
    private static int AUTOCOMPLETE_REQUEST_CODE = 100;
    private static int YELP_LIMIT = 5;
    public static final String GOOGLE_BASE_URL = "https://maps.googleapis.com/";
    public static final String YELP_BASE_URL = "https://api.yelp.com/v3/";
    private static final String YELP_API_KEY = "fq038-wNNvkjlvvsz_fBqD8a2Bl-mVUT1XHXz_-EJEZS-8SEO6OoynOpQmgTf5-Y7_Ujsc9LKl5TPJ_6Y2NdFPBVCUeC6v6r0wT3_uee4B2lJLldWP4rfKKqWVizYnYx";
    private boolean locationChosen = false;
    private Button textBNext;
    private String placeId;
    private String placeAddress;
    private String placeName;
    private LatLng latLng;
    private Toolbar composeLocationToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_location);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG, Place.Field.ADDRESS));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.i(TAG, "place autocomplete success");
                placeId = place.getId();
                placeName = place.getName();
                placeAddress = place.getAddress();
                latLng = place.getLatLng();

                // query yelp & places details here and merge responses
                // ONLY IF a merged one doesn't already exist in database
                ParseQuery<CustomPlace> query = ParseQuery.getQuery(CustomPlace.class);
                query.whereEqualTo("gPlaceId", placeId);
                query.findInBackground(new FindCallback<CustomPlace>() {
                    @Override
                    public void done(List<CustomPlace> objects, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "error querying custom places");
                        } else {
                            Log.i(TAG, "success querying custom places");
                            if (objects.size() == 0) { // we did not create a custom place object before.
                                createCustomPlace(placeId, placeName);
                            } else {
                                textBNext.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(ComposeLocationActivity.this, ComposeDurationActivity.class);
                                        i.putExtras(getIntent());
                                        i.putExtra("customPlace", objects.get(0));
                                        startActivity(i);
                                    }
                                });
                            }
                        }
                    }
                });

                Log.i(TAG, "place id: " + placeId);
                Log.i(TAG, "place name: " + placeName);
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        textBNext = findViewById(R.id.textBNext);

        composeLocationToolbar = findViewById(R.id.composeLocationToolbar);
        setSupportActionBar(composeLocationToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        // Initialize the SDK
        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        //etLocation.setFocusable(false);
//        search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // initialize place field list
//                List<Place.Field> fieldList = Arrays.asList(Place.Field.NAME, Place.Field.ID,
//                        Place.Field.LAT_LNG, Place.Field.ADDRESS);
//
//                // create intent
//                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
//                        .build(ComposeLocationActivity.this);
//
//                // start activity result
//                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK) {
//            // success
//            Log.i(TAG, "place autocomplete success");
//
//            // this place instance can retrieve details about the place
//            Place place = Autocomplete.getPlaceFromIntent(data);
//            etLocation.setText(place.getName());
//            placeId = place.getId();
//            placeName = place.getName();
//            placeAddress = place.getAddress();
//            latLng = place.getLatLng();
//
//            // query yelp & places details here and merge responses
//            // ONLY IF a merged one doesn't already exist in database
//            ParseQuery<CustomPlace> query = ParseQuery.getQuery(CustomPlace.class);
//            query.whereEqualTo("gPlaceId", placeId);
//            query.findInBackground(new FindCallback<CustomPlace>() {
//                @Override
//                public void done(List<CustomPlace> objects, ParseException e) {
//                    if (e != null) {
//                        Log.e(TAG, "error querying custom places");
//                    } else {
//                        Log.i(TAG, "success querying custom places");
//                        if (objects.size() == 0) { // we did not create a custom place object before.
//                            createCustomPlace(placeId, placeName);
//                        } else {
//                            btnNext2.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent i = new Intent(ComposeLocationActivity.this, ComposeDurationActivity.class);
//                                    i.putExtras(getIntent());
//                                    i.putExtra("customPlace", objects.get(0));
//                                    startActivity(i);
//                                }
//                            });
//                        }
//                    }
//                }
//            });
//
//            Log.i(TAG, "place id: " + placeId);
//            Log.i(TAG, "place name: " + placeName);
//
//        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
//            Status status = Autocomplete.getStatusFromIntent(data);
//            // display toast
//            Log.e(TAG, "place autocomplete error");
//        }
    }

    private void createCustomPlace(String placeId, String placeName) {
        CustomPlace customPlace = new CustomPlace();
        customPlace.setGPlaceId(placeId);
        customPlace.setName(placeName);
        customPlace.setLat(latLng.latitude);
        customPlace.setLong(latLng.longitude);
        performQueries(customPlace);
    }

    private void performQueries(CustomPlace customPlace) {
        // querying google places

        Retrofit googleRetrofit = new Retrofit.Builder()
                .baseUrl(GOOGLE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GoogleMapsService googleMapsService = googleRetrofit.create(GoogleMapsService.class);
        googleMapsService.getPlaceDetailsSearchResult(placeId,
                        "formatted_address,name,place_id,rating,reviews",
                        BuildConfig.MAPS_API_KEY)
                .enqueue(new Callback<PlaceDetailsSearchResult>() {
                    @Override
                    public void onResponse(Call<PlaceDetailsSearchResult> call, Response<PlaceDetailsSearchResult> response) {
                        Log.i(TAG, "success getting the place details");
                        PlaceDetailsSearchResult responseBody = response.body();
                        double gRating = responseBody.getResult().getRating();
                        queryYelp(customPlace, gRating); // querying google & yelp apis occur in parallel
                        // get reviews and save them
                        List<GoogleReview> googleReviews = responseBody.getResult().getReviews();
                        for (GoogleReview googleReview : googleReviews) {
                            String name = googleReview.getAuthorName();
                            double rating = googleReview.getRating();
                            String text = googleReview.getText();
                            String profilePicUrl = googleReview.getProfilePhotoUrl();

                            Review review = new Review();
                            review.putName(name);
                            review.putRating(rating);
                            review.putText(text);
                            if (profilePicUrl != null) {
                                review.putProfilePicUrl(profilePicUrl);
                            }

                            review.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null) {
                                        Log.e(TAG, "error saving google review");
                                        return;
                                    }
                                    Log.i(TAG, "success saving google review");
                                    CustomPlaceToReview placeToReview = new CustomPlaceToReview();
                                    placeToReview.setCustomPlace(customPlace);
                                    placeToReview.setReview(review);
                                    placeToReview.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e != null) {
                                                Log.e(TAG, "error saving placeToReview");
                                                return;
                                            }
                                            Log.i(TAG, "success saving placeToReview");
                                        }
                                    });
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<PlaceDetailsSearchResult> call, Throwable t) {
                        Log.e(TAG, "error occurred while getting place details");
                    }
                });

    }

    private void queryYelp(CustomPlace customPlace, double gRating) {
        Retrofit yelpRetrofit = new Retrofit.Builder()
                .baseUrl(YELP_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        YelpService yelpService = yelpRetrofit.create(YelpService.class);
        yelpService.searchBusinesses("Bearer " + YELP_API_KEY, placeName, placeAddress,
                latLng.latitude, latLng.longitude, YELP_LIMIT).enqueue(new Callback<BusinessSearchResult>() {
            @Override
            public void onResponse(Call<BusinessSearchResult> call, Response<BusinessSearchResult> response) {
                Log.i(TAG, "success getting yelp businesses");
                // for now, assume matching business is the first one.
                Business matchingYelpBusiness = response.body().getBusinesses().get(0);
                double yRating = matchingYelpBusiness.getRating();
                String yPrice = matchingYelpBusiness.getPrice();
                double avgRating = (gRating + yRating) / 2;
                String placeImageUrl = matchingYelpBusiness.getImageUrl();
                customPlace.setRating(avgRating);
                customPlace.setPrice(yPrice);
                customPlace.setPlaceImageUrl(placeImageUrl);
                customPlace.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "error saving custom place object");
                            return;
                        }
                        Log.i(TAG, "success saving custom place object");

                        // custom place exists in database, so we can now attach it to the post
                        textBNext.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(ComposeLocationActivity.this, ComposeDurationActivity.class);
                                i.putExtras(getIntent());
                                i.putExtra("customPlace", customPlace);
                                startActivity(i);
                            }
                        });
                    }
                });
                String matchingYelpBusinessId = matchingYelpBusiness.getId();
                yelpService.searchReviews("Bearer " + YELP_API_KEY, matchingYelpBusinessId).enqueue(new Callback<ReviewSearchResult>() {
                    @Override
                    public void onResponse(Call<ReviewSearchResult> call, Response<ReviewSearchResult> response) {
                        Log.i(TAG, "success getting yelp reviews");
                        List<YelpReview> yelpReviews = response.body().getReviews();
                        for (YelpReview yelpReview : yelpReviews) {
                            String name = yelpReview.getUser().getName();
                            double rating = yelpReview.getRating();
                            String text = yelpReview.getText();
                            String profilePicUrl = yelpReview.getUser().getImageUrl();

                            Review review = new Review();
                            review.putName(name);
                            review.putRating(rating);
                            review.putText(text);
                            if (profilePicUrl != null) {
                                review.putProfilePicUrl(profilePicUrl);
                            }

                            review.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null) {
                                        Log.e(TAG, "error saving yelp review");
                                        return;
                                    }
                                    Log.i(TAG, "success saving yelp review");
                                    CustomPlaceToReview placeToReview = new CustomPlaceToReview();
                                    placeToReview.setCustomPlace(customPlace);
                                    placeToReview.setReview(review);
                                    placeToReview.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e != null) {
                                                Log.e(TAG, "error saving placeToReview");
                                                return;
                                            }
                                            Log.i(TAG, "success saving placeToReview");
                                        }
                                    });
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<ReviewSearchResult> call, Throwable t) {
                        Log.i(TAG, "failure getting yelp reviews");
                    }
                });
            }

            @Override
            public void onFailure(Call<BusinessSearchResult> call, Throwable t) {
                Log.i(TAG, "error occurred while getting yelp businesses");
            }
        });
    }

}