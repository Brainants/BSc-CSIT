package com.techies.bsccsit.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.techies.bsccsit.R;

public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        ImageView imageView= (ImageView) findViewById(R.id.imageViewerImage);

        Picasso.with(this).load(getIntent().getStringExtra("ImageURL")).into(imageView);
    }
}
