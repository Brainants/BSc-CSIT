package com.techies.bsccsit.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.squareup.picasso.Picasso;
import com.techies.bsccsit.R;
import com.techies.bsccsit.widget.TouchImageView;

public class ImageViewActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        TouchImageView imageView = (TouchImageView) findViewById(R.id.imageViewerImage);
        RobotoTextView desc = (RobotoTextView) findViewById(R.id.decsOfImage);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarImageView);

        setSupportActionBar(toolbar);
        setTitle("");


        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Picasso.with(this).load(getIntent().getStringExtra("ImageURL")).into(imageView);

        desc.setText(getIntent().getStringExtra("desc"));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return true;
    }
}
