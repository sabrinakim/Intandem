package com.example.intandem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String BASE_URL = "https://api.yelp.com/v3/";
    private static final String API_KEY = "i dont get how to keep api key a secret so i erased it from here";
    private static final String FRIENDS = "user_friends";
    private static int AUTOCOMPLETE_REQUEST_CODE = 100;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private ImageView ivProfilePic;
    private TextView tvName;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        // next, we want to define the endpoints for our api by defining an interface.
        // retrofit will be in charge of filling in the functions in the interface.

        YelpService yelpService = retrofit.create(YelpService.class);
        // search Restaurants is asynchronous
//        yelpService.searchRestaurants("Bearer " + API_KEY, "Avocado Toast", "New York").enqueue(new Callback<ResponseBody>() {
//
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Log.i(TAG, "on response " + response);
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.i(TAG, "on failure ");
//            }
//        });

        ivProfilePic = findViewById(R.id.ivProfilePic);
        tvName = findViewById(R.id.tvName);
        etSearch = findViewById(R.id.etSearch);

        callbackManager = CallbackManager.Factory.create();

        loginButton = findViewById(R.id.login_button);
        loginButton.setPermissions(Arrays.asList(FRIENDS));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // loginResult contains parameters like the access token & granted permissions u set up
                Log.i(TAG, "login success");
            }

            @Override
            public void onCancel() {
                // App code
                Log.i(TAG, "login canceled");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.e(TAG, "login error");
            }
        });

        // Initialize the SDK
        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        etSearch.setFocusable(false);
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // initialize place field list
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,
                        Place.Field.LAT_LNG, Place.Field.NAME);

                // create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
                        .build(MainActivity.this);

                // start activity result
                startActivityForResult(intent, 100);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // passes in the login results to the login manager via the callback manager
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK) {
            // success

            // this place instance can retrieve details about the place
            Place place = Autocomplete.getPlaceFromIntent(data);
            etSearch.setText(place.getAddress());
            System.out.println("lat/long: " + place.getLatLng());
            System.out.println("name: " + place.getName());

            Log.i(TAG, "place autocomplete success");
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            // display toast
            Log.e(TAG, "place autocomplete error");
        }

        // !!! main purpose of logging in is to obtain an access token that allows you to use FB's APIs
        // we will use the Graph API

        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(@Nullable JSONObject jsonObject, @Nullable GraphResponse graphResponse) {
                Log.i(TAG, jsonObject.toString());

                try {
                    String name = jsonObject.getString("name");
                    String id = jsonObject.getString("id");
                    tvName.setText(name);
                    Picasso.get().load("https://graph.facebook.com/" + id + "/picture?type=large")
                            .into(ivProfilePic);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        Bundle bundle = new Bundle();

        // change later: these are what you are requesting from the graph api
        bundle.putString("fields", "name, id, first_name, last_name");

        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();
    }

    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        // whenever the access token is changed, this method is called automatically
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                LoginManager.getInstance().logOut();
                tvName.setText("");
                ivProfilePic.setImageResource(0);
            }
        }
    };

    @Override
    protected void onDestroy() {
        // this is the final call you receive before your activity is destroyed
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }
}