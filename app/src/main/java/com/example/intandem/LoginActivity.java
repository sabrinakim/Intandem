package com.example.intandem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String FRIENDS = "user_friends";
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private Button btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        callbackManager = CallbackManager.Factory.create();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && accessToken.isExpired() == false) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish(); // doesn't let you go back to login activity once logged in.
        }

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "login success");
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "login canceled");
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                Log.e(TAG, "login error");
            }
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList(FRIENDS));
            }
        });

        //loginButton = findViewById(R.id.login_button);
        //loginButton.setPermissions(Arrays.asList(FRIENDS));
        // If you are using in a fragment, call loginButton.setFragment(this);

//        // Callback registration
//        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                // loginResult contains parameters like the access token & granted permissions u set up
//                Log.i(TAG, "login success");
//            }
//
//            @Override
//            public void onCancel() {
//                // App code
//                Log.i(TAG, "login canceled");
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                // App code
//                Log.e(TAG, "login error");
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // passes in the login results to the login manager via the callback manager
        super.onActivityResult(requestCode, resultCode, data);


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

                                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                        // pass in user through activities
                                        i.putExtra("user", user);
                                        startActivity(i);
                                    }
                                });
                            } else { // user already registered in our database
                                Log.i(TAG, "user already exists");
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
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

//        GraphRequest request = GraphRequest.newGraphPathRequest(
//                AccessToken.getCurrentAccessToken(),
//                "/me/friends",
//                new GraphRequest.Callback() {
//                    @Override
//                    public void onCompleted(GraphResponse response) {
//                        System.out.println(response);
//                    }
//                });
//
//        request.executeAsync();
    }
}