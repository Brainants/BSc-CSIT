package com.techies.bsccsit.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.techies.bsccsit.R;

public class ImageViewActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        ImageView imageView= (ImageView) findViewById(R.id.imageViewerImage);
        TextView likeNo= (TextView) findViewById(R.id.likeNo),
                commentNo= (TextView) findViewById(R.id.commentNo),
                desc= (TextView) findViewById(R.id.decsOfImage);
        LinearLayout comntNlike= (LinearLayout) findViewById(R.id.commentNlike);

        Picasso.with(this).load(getIntent().getStringExtra("ImageURL")).into(imageView);

        desc.setText(getIntent().getStringExtra("desc"));
        if(getIntent().getStringExtra("like")==null)
            comntNlike.setVisibility(View.GONE);
        else{
            likeNo.setText(getIntent().getStringExtra("like"));
            commentNo.setText(getIntent().getStringExtra("comment"));
        }
    }
}
