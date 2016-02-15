package com.brainants.bsccsit.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.brainants.bsccsit.R;

import mehdi.sakout.fancybuttons.FancyButton;

public class AboutUs extends AppCompatActivity {
    private FancyButton facebook, twitter, website;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        setSupportActionBar((Toolbar) findViewById(R.id.aboutToolbar));

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("About Us");


        facebook = (FancyButton) findViewById(R.id.facebook);
        twitter = (FancyButton) findViewById(R.id.twitter);
        website = (FancyButton) findViewById(R.id.website);

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = null;
                try {
                    getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/160590230974977"));
                } catch (Exception e) {
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/brainants"));
                } finally {
                    startActivity(i);
                }


            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = null;
                try {
                    getPackageManager().getPackageInfo("com.twitter.android", 0);
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=4720415052"));
                } catch (Exception e) {
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/brainants"));
                } finally {
                    startActivity(i);
                }
            }
        });

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.brainants.com/")));

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
