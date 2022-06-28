package com.example.intandem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.intandem.fragments.ActivityFragment;
import com.example.intandem.fragments.ComposeFragment;
import com.example.intandem.fragments.PostsFragment;
import com.example.intandem.fragments.ProfileFragment;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "SecondActivity";
    private static final String BASE_URL = "https://api.yelp.com/v3/";
    private static final String API_KEY = "fq038-wNNvkjlvvsz_fBqD8a2Bl-mVUT1XHXz_-EJEZS-8SEO6OoynOpQmgTf5-Y7_Ujsc9LKl5TPJ_6Y2NdFPBVCUeC6v6r0wT3_uee4B2lJLldWP4rfKKqWVizYnYx";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;
    private ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

//        GraphRequest request = GraphRequest.newMeRequest(accessToken,
//                new GraphRequest.GraphJSONObjectCallback() {
//            @Override
//            public void onCompleted(@Nullable JSONObject jsonObject, @Nullable GraphResponse graphResponse) {
//                try {
//                    System.out.println("name: " + jsonObject.getString("name"));
//                    System.out.println("id: " + jsonObject.getString("id"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        request.executeAsync();

//        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
//
//        // next, we want to define the endpoints for our api by defining an interface.
//        // retrofit will be in charge of filling in the functions in the interface.
//
//        YelpService yelpService = retrofit.create(YelpService.class);
//        // search Restaurants is asynchronous
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

        // unwrap parcel here
        user = getIntent().getParcelableExtra("user");

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = PostsFragment.newInstance(user);
                        break;
                    case R.id.action_compose:
                        fragment = new ComposeFragment();
                        break;
                    case R.id.action_activity:
                        fragment = new ActivityFragment();
                        break;
                    case R.id.action_profile:
                    default:
                        fragment = new ProfileFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onLogoutAction(MenuItem mi) {
        // handle logout here
        LoginManager.getInstance().logOut();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish(); // doesn't let you go back to main activity once logged out
    }
}