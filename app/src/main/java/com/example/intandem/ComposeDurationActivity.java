package com.example.intandem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.parse.ParseUser;

public class ComposeDurationActivity extends AppCompatActivity {

    public static final String TAG = "ComposeDurationActivity";
    private Spinner spinnerTimeUnits;
    private Button btnDurationNext;
    private EditText etDuration;
    private String duration;
    private String timeUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_duration);

        spinnerTimeUnits = findViewById(R.id.spinnerTimeUnit);
        btnDurationNext = findViewById(R.id.btnDurationNext);
        etDuration = findViewById(R.id.etDuration);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(ComposeDurationActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.timeUnits));

        // we want to make this list a dropdown list, not a simple list
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // allows adapter to show data in the spinner
        spinnerTimeUnits.setAdapter(myAdapter);

        spinnerTimeUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeUnit = (String) parent.getItemAtPosition(position);
                Log.i(TAG, "timeunit: " + timeUnit);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG, "nothing was selected");
            }
        });

        btnDurationNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duration = etDuration.getText().toString();
                Intent i = new Intent(ComposeDurationActivity.this, ComposePictureActivity.class);
                i.putExtras(getIntent());
                i.putExtra("duration", duration);
                i.putExtra("timeUnit", timeUnit);
                startActivity(i);
            }
        });

    }
}