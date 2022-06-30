package com.example.intandem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.Arrays;
import java.util.List;

public class ComposeLocationActivity extends AppCompatActivity {

    private static String TAG = "ComposeLocationActivity";
    private static int AUTOCOMPLETE_REQUEST_CODE = 100;
    private EditText etLocation;
    private Button btnNext2;
    private String placeId;
    private String placeName;


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
                List<Place.Field> fieldList = Arrays.asList(Place.Field.NAME, Place.Field.ID);

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

            // this place instance can retrieve details about the place
            Place place = Autocomplete.getPlaceFromIntent(data);
            etLocation.setText(place.getName());
            placeId = place.getId();
            placeName = place.getName();
            Log.i(TAG, "place id: " + placeId);
            Log.i(TAG, "place name: " + placeName);
            //System.out.println("lat/long: " + place.getLatLng());
            //System.out.println("name: " + place.getName());

            Log.i(TAG, "place autocomplete success");

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
}