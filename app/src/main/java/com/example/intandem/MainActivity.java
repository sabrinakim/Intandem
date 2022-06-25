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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String BASE_URL = "https://api.yelp.com/v3/";
    private static final String API_KEY = "fq038-wNNvkjlvvsz_fBqD8a2Bl-mVUT1XHXz_-EJEZS-8SEO6OoynOpQmgTf5-Y7_Ujsc9LKl5TPJ_6Y2NdFPBVCUeC6v6r0wT3_uee4B2lJLldWP4rfKKqWVizYnYx";
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
        yelpService.searchRestaurants("Bearer " + API_KEY, "Avocado Toast", "New York").enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "on response " + response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "on failure ");
            }
        });

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
//                Intent i = new Intent(MainActivity.this, SecondActivity.class);
//                // pass in user through activities
//                startActivity(i);
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
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
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

        GraphRequest meGraphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(@Nullable JSONObject jsonObject, @Nullable GraphResponse graphResponse) {
                Log.i(TAG, jsonObject.toString());

                try {
                    String name = jsonObject.getString("name");
                    String id = jsonObject.getString("id");
                    String first_name = jsonObject.getString("first_name");
                    String last_name = jsonObject.getString("last_name");
                    tvName.setText(name);
                    Picasso.get().load("https://graph.facebook.com/" + id + "/picture?type=large")
                            .into(ivProfilePic);

                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("fbId", id);
                    query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Issue with getting user", e);
                                return;
                            }

                            if (objects.size() == 0) {
                                Log.i(TAG, "new user");
                                ParseUser user = new ParseUser();
                                user.put("firstName", first_name);
                                user.put("lastName", last_name);
                                // username and password is just their name
                                user.put("username", name);
                                user.put("password", name);
                                user.put("fbId", id);

                                user.signUpInBackground(new SignUpCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e != null) {
                                            Log.e(TAG, "something went wrong with saving user: " + e);
                                            return;
                                        }
                                        Log.i(TAG, "user saved successfully");

                                        Intent i = new Intent(MainActivity.this, SecondActivity.class);
                                        // pass in user through activities
                                        i.putExtra("user", user);
                                        startActivity(i);
                                    }
                                });
                            } else { // user already registered in our database
                                Log.i(TAG, "user already exists");
                                Intent i = new Intent(MainActivity.this, SecondActivity.class);
                                // pass in user through activities
                                i.putExtra("user", objects.get(0));
                                startActivity(i);
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        Bundle bundle = new Bundle();

        // change later: these are what you are requesting from the graph api
        bundle.putString("fields", "name, id, first_name, last_name");

        meGraphRequest.setParameters(bundle);
        meGraphRequest.executeAsync();

//        // fetching logged in user's friends
//        GraphRequest friendsGraphRequest = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(),
//                new GraphRequest.GraphJSONArrayCallback() {
//                    @Override
//                    public void onCompleted(@Nullable JSONArray jsonArray, @Nullable GraphResponse graphResponse) {
//                        if (jsonArray != null) {
//                            Log.i(TAG, jsonArray.toString());
//                            ParseQuery<Friendships> query = ParseQuery.getQuery(Friendships.class);
//                            query.whereEqualTo("user1", ParseUser.getCurrentUser());
//                        }
//                    }
//                });
//
//        friendsGraphRequest.executeAsync();
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