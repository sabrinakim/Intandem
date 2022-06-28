package com.example.intandem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;

public class ComposeEventActivity extends AppCompatActivity {

    private Button btnNext;
    private EditText etEvent;
    private ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_event);
        getSupportActionBar().setTitle("New Post");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etEvent = findViewById(R.id.etEvent);
        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ComposeEventActivity.this, ComposeLocationActivity.class);
                i.putExtras(getIntent());
                i.putExtra("event", etEvent.getText().toString());
                startActivity(i);
            }
        });
    }
}