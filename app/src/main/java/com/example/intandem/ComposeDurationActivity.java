package com.example.intandem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class ComposeDurationActivity extends AppCompatActivity {

    public static final String TAG = "ComposeDurationActivity";
    private Button btnDurationNext;
    private EditText etDuration;
    private String duration;
    private Toolbar composeDurationToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_duration);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnDurationNext = findViewById(R.id.btnDurationNext);
        etDuration = findViewById(R.id.etCaption);
        composeDurationToolbar = findViewById(R.id.composeDurationToolbar);
        setSupportActionBar(composeDurationToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnDurationNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duration = etDuration.getText().toString();
                Intent i = new Intent(ComposeDurationActivity.this, ComposePictureActivity.class);
                i.putExtras(getIntent());
                i.putExtra("duration", duration);
                startActivity(i);
            }
        });
    }
}