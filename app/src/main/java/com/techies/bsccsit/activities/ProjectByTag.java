package com.techies.bsccsit.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.techies.bsccsit.R;

public class ProjectByTag extends AppCompatActivity {
    String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_by_tag);
        tag=getIntent().getStringExtra("tag");

    }
}
