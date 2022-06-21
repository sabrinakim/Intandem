package com.example.intandem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String FRIENDS = "user_friends";
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private ImageView ivProfilePic;
    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivProfilePic = findViewById(R.id.ivProfilePic);
        tvName = findViewById(R.id.tvName);

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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // passes in the login results to the login manager via the callback manager
        super.onActivityResult(requestCode, resultCode, data);

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