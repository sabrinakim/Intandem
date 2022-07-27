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
import com.parse.DeleteCallback;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        useGraphApi();
    }

    private void useGraphApi() {
        GraphRequest meGraphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(@Nullable JSONObject jsonObject, @Nullable GraphResponse graphResponse) {
                Log.i(TAG, jsonObject.toString());

                try {
                    String name = jsonObject.getString("name");
                    String id = jsonObject.getString("id");
                    String first_name = jsonObject.getString("first_name");
                    String last_name = jsonObject.getString("last_name");
                    String pictureUrl = jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");

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
                                createNewUser(name, first_name, last_name, id, pictureUrl);
                            } else { // user already registered in our database
                                Log.i(TAG, "user already exists");
                                updateFriendsList(id);
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
        bundle.putString("fields", "name, id, first_name, last_name, picture.width(150).height(150)");

        meGraphRequest.setParameters(bundle);
        meGraphRequest.executeAsync();
    }

    private void createNewUser(String name, String firstName, String lastName, String id, String pictureUrl) {
        ParseUser user = new ParseUser();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        // username and password is just their name
        user.put("username", name);
        user.put("password", name);
        user.put("fbId", id);
        user.put("pictureUrl", pictureUrl);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "something went wrong with saving user: " + e);
                    return;
                }
                Log.i(TAG, "user saved successfully");

                recordFriendsList(id, user);
            }
        });
    }

    private void updateFriendsList(String id) {

        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + id + "/friends",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        Log.i(TAG, response.toString());
                        try {
                            Set<String> currFriendsList = new HashSet<>();
                            HashMap<String, Friendship> storedFriendsMap = new HashMap<>();

                            JSONArray friends = response.getJSONObject().getJSONArray("data");
                            for (int i = 0; i < friends.length(); i++) {
                                currFriendsList.add(friends.getJSONObject(i).getString("id"));
                            }
                            ParseQuery<Friendship> query = ParseQuery.getQuery(Friendship.class);
                            query.whereEqualTo("user1Id", id);
                            query.findInBackground(new FindCallback<Friendship>() {
                                @Override
                                public void done(List<Friendship> friends, ParseException e) {
                                    if (e != null) {
                                        Log.e(TAG, "error querying friends data");
                                    } else {
                                        for (Friendship friend : friends) {
                                            storedFriendsMap.put(friend.getUser2Id(), friend);
                                        }
                                        // find difference btwn sets.
                                        // {1, 2, 3} storedFriendsMap
                                        // {1, 4, 3} currFriendsList

                                        FriendshipUpdates friendshipUpdates = new FriendshipUpdates(currFriendsList);
                                        HashMap<String, Friendship> deletedFriendsMap = friendshipUpdates.getFriendsToDelete(storedFriendsMap);

                                        for (Map.Entry<String, Friendship> deletedFriend : deletedFriendsMap.entrySet()) {
                                            deletedFriend.getValue().deleteInBackground(new DeleteCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e != null) {
                                                        Log.e(TAG, "error deleting friendship");
                                                    } else {
                                                        Log.i(TAG, "success deleting friendship");
                                                    }
                                                }
                                            });
                                        }

                                        if (deletedFriendsMap.size() > 0) {
                                            ParseQuery<Friendship> query = ParseQuery.getQuery(Friendship.class);
                                            query.whereEqualTo("user2Id", id);
                                            query.findInBackground(new FindCallback<Friendship>() {
                                                @Override
                                                public void done(List<Friendship> friendships, ParseException e) {
                                                    if (e != null) {
                                                        Log.e(TAG, "error getting inverse friendships");
                                                        return;
                                                    }

                                                    Set<Friendship> toDelete = friendshipUpdates.getFriendsToDeleteInverse(deletedFriendsMap, friendships);

                                                    for (Friendship deletedFriend : toDelete) {
                                                        deletedFriend.deleteInBackground(new DeleteCallback() {
                                                            @Override
                                                            public void done(ParseException e) {
                                                                if (e != null) {
                                                                    Log.e(TAG, "error deleting friendship inverse");
                                                                } else {
                                                                    Log.i(TAG, "success deleting friendship inverse");
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }

                                        Set<String> newFriends = friendshipUpdates.getNewFriendships(storedFriendsMap);

                                        for (String newFriend : newFriends) {
                                            Friendship friendship = new Friendship();
                                            friendship.setUser1Id(id);
                                            friendship.setUser2Id(newFriend);

                                            Friendship friendshipInverse = new Friendship();
                                            friendshipInverse.setUser1Id(newFriend);
                                            friendshipInverse.setUser2Id(id);

                                            friendship.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e != null) {
                                                        Log.e(TAG, "error saving new friend");
                                                    } else {
                                                        Log.i(TAG, "success saving new friend");
                                                    }
                                                }
                                            });

                                            friendshipInverse.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e != null) {
                                                        Log.e(TAG, "error saving new friend inverse");
                                                    } else {
                                                        Log.i(TAG, "success saving new friend inverse");
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        request.executeAsync();
    }

    private void recordFriendsList(String id, ParseUser user) {
        // each time a new user logs in, we want to record their friends list.
        GraphRequest requestFriendsList = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + id + "/friends",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        Log.i(TAG, response.toString());

                        try {
                            JSONArray friends = response.getJSONObject().getJSONArray("data");

                            for (int i = 0; i < friends.length(); i++) {
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

                                // friendship is bidirectional
                                Friendship friendshipInverse = new Friendship();
                                friendshipInverse.setUser1Id(friendsId);
                                friendshipInverse.setUser2Id(id);
                                friendshipInverse.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e != null) {
                                            Log.e(TAG, "error saving friendship inverse");
                                        }
                                        Log.i(TAG, "friendship inverse saved successfully");
                                    }
                                });
                            }

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }

                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.putExtra("user", user);
                        startActivity(i);
                    }
                });

        requestFriendsList.executeAsync();
    }
}