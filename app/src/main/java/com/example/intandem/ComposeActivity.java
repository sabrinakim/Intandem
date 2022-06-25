package com.example.intandem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;

public class ComposeActivity extends AppCompatActivity {

    private Button btnNext;
    private EditText etEvent;
    private ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // unwrap parcel here
        user = getIntent().getExtras().getParcelable("user");

        etEvent = findViewById(R.id.etEvent);
        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ComposeActivity.this, ComposeLocationActivity.class);
                i.putExtra("event", etEvent.getText().toString());
                i.putExtra("user", user);
                startActivity(i);
            }
        });
    }
}