package com.example.intandem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.parse.ParseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView ivProfileProfilePic;
    private TextView tvProfileName;
    private Toolbar profileToolbar;
    private Button btnLogout;
    private ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        user = getIntent().getParcelableExtra("user");

        profileToolbar = findViewById(R.id.profileToolbar);
        btnLogout = findViewById(R.id.btnLogout);
        ivProfileProfilePic = findViewById(R.id.ivProfileProfilePic);
        tvProfileName = findViewById(R.id.tvProfileName);

        setSupportActionBar(profileToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        Glide.with(this).load(user.getString("pictureUrl")).into(ivProfileProfilePic);
        tvProfileName.setText(user.getUsername());

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(i);
                finish(); // doesn't let you go back to main activity once logged out
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Respond to the action bar's Up/Home button
                Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.nothing, R.anim.slide_right_in);
                return false;
        }
        return super.onOptionsItemSelected(item);
    }
}