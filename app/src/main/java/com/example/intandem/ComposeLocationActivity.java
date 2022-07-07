package com.example.intandem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.intandem.dataModels.PlaceDetailsSearchResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ComposeLocationActivity extends AppCompatActivity {

    private static String TAG = "ComposeLocationActivity";
    private static int AUTOCOMPLETE_REQUEST_CODE = 100;
    public static final String GOOGLE_BASE_URL = "https://maps.googleapis.com/";
    private EditText etLocation;
    private Button btnNext2;
    private String placeId;
    private String placeName;
    private LatLng latLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_location);

        etLocation = findViewById(R.id.etLocation);
        btnNext2 = findViewById(R.id.btnNext2);

        // Initialize the SDK
        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        etLocation.setFocusable(false);
        etLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // initialize place field list
                List<Place.Field> fieldList = Arrays.asList(Place.Field.NAME, Place.Field.ID, Place.Field.LAT_LNG);

                // create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
                        .build(ComposeLocationActivity.this);

                // start activity result
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK) {
            // success
            Log.i(TAG, "place autocomplete success");

            // this place instance can retrieve details about the place
            Place place = Autocomplete.getPlaceFromIntent(data);
            etLocation.setText(place.getName());
            placeId = place.getId();
            placeName = place.getName();
            latLng = place.getLatLng();

            // TODO: query yelp & places details here and merge responses.
            merging();


            Log.i(TAG, "place id: " + placeId);
            Log.i(TAG, "place name: " + placeName);



            btnNext2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ComposeLocationActivity.this, ComposeDurationActivity.class);
                    i.putExtras(getIntent());
                    i.putExtra("placeId", placeId);
                    i.putExtra("placeName", placeName);
                    // pass in places object
                    startActivity(i);
                }
            });
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            // display toast
            Log.e(TAG, "place autocomplete error");
        }
    }

    private void merging() {
        // querying google places
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GOOGLE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GoogleMapsService googleMapsService = retrofit.create(GoogleMapsService.class);
        googleMapsService.getPlaceDetailsSearchResult(placeId, BuildConfig.MAPS_API_KEY)
                .enqueue(new Callback<PlaceDetailsSearchResult>() {
                    @Override
                    public void onResponse(Call<PlaceDetailsSearchResult> call, Response<PlaceDetailsSearchResult> response) {
                        Log.i(TAG, "success getting the place details");
                    }

                    @Override
                    public void onFailure(Call<PlaceDetailsSearchResult> call, Throwable t) {
                        Log.e(TAG, "error occurred while getting place details");
                    }
                });

    }
}