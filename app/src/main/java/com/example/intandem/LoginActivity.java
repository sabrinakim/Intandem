package com.example.intandem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.intandem.models.Friendship;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String FRIENDS = "user_friends";
    private CallbackManager callbackManager;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) { // user already logged in
            // query for users here to get current user
            Log.i(TAG, "user alr logged in before");
            GraphRequest meGraphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(@Nullable JSONObject jsonObject, @Nullable GraphResponse graphResponse) {
                    try {
                        String id = jsonObject.getString("id");
                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                        query.whereEqualTo("fbId", id);
                        query.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> objects, ParseException e) {
                                ParseUser user = objects.get(0);
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                i.putExtra("user", user);
                                startActivity(i);
                                finish(); // doesn't let you go back to login activity once logged in.
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            meGraphRequest.executeAsync();
        }

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "login success");
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

                                        // each time a new user logs in, we want to record their friends list.
                                        GraphRequest requestFriendsList = GraphRequest.newGraphPathRequest(
                                                AccessToken.getCurrentAccessToken(),
                                                "/" + id + "/friends",
                                                new GraphRequest.Callback() {
                                                    @Override
                                                    public void onCompleted(GraphResponse response) {
                                                        Log.i(TAG, response.toString());

                                                        try {
                                                            JSONArray friends = response.getJSONObject()
                                                                    .getJSONObject("friends")
                                                                    .getJSONArray("data");

                                                            for (int i = 0; i < friends.length(); i++) {
                                                                // testing pr
                                                                String friendsId = friends.getJSONObject(i).getString("id");
                                                                Friendship friendship = new Friendship();
                                                                friendship.setUser1Id(id);
                                                                friendship.setUser2Id(friendsId);
                                                                friendship.saveInBackground(new SaveCallback() {
                                                                    @Override
                                                                    public void done(ParseException e) {
                                                                        if (e != null) {
                                                                            Log.e(TAG, "error saving friendship");
                                                                        }
                                                                        Log.i(TAG, "friendship saved successfully");
                                                                    }
                                                                });
                                                            }

                                                        } catch (JSONException ex) {
                                                            ex.printStackTrace();
                                                        }

                                                        // move on to the main activity ONLY after we store friend data in our database.
                                                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                                        i.putExtra("user", user);
                                                        startActivity(i);
                                                    }
                                                });

                                        requestFriendsList.executeAsync();
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