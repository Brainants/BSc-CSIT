package com.techies.bsccsit.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.techies.bsccsit.R;

public class FbEvent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_event);
        TextView name= (TextView) findViewById(R.id.eventId);
        name.setText(getIntent().getStringExtra("eventID"));
    }
}
