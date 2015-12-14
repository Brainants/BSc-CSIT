package com.techies.bsccsit.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.squareup.picasso.Picasso;
import com.techies.bsccsit.R;

import org.json.JSONException;
import org.json.JSONObject;

public class FbEvent extends AppCompatActivity {


    private CollapsingToolbarLayout mCollapsingToolbar;
    private String eventId;
    private String eventName;
    private String eventImage;
    private ProgressBar progressBar;
    private LinearLayout errorMsg;

    private TextView event_name, event_description, event_place, event_location, event_street, event_time;
    ImageView event_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_event);

        event_name = (TextView) findViewById(R.id.eventName);
        event_description = (TextView) findViewById(R.id.eventDesc);
        event_place = (TextView) findViewById(R.id.eventPlace);
        event_location = (TextView) findViewById(R.id.eventLocation);
        event_street = (TextView) findViewById(R.id.eventStreet);
        event_time = (TextView) findViewById(R.id.eventTime);
        event_photo = (ImageView) findViewById(R.id.eventPhoto);

        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.eventCollapse);

        eventId = getIntent().getStringExtra("eventID");
        eventName = getIntent().getStringExtra("eventName");
        eventImage = getIntent().getStringExtra("imageURL");

        progressBar = (ProgressBar) findViewById(R.id.progressbarFbEvent);
        errorMsg = (LinearLayout) findViewById(R.id.errorMessagePageEvent);

        errorMsg.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        setSupportActionBar((Toolbar) findViewById(R.id.eventToolbar));
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCollapsingToolbar.setTitle(eventName);
        Picasso.with(this).load(eventImage).into(event_photo);


        downloadFromInternet();


    }

    private void downloadFromInternet() {
        Bundle params = new Bundle();
        params.putString("fields", "description,name,place,start_time,end_time");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + eventId, params, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                FacebookRequestError error = response.getError();
                if (error != null) {
                    progressBar.setVisibility(View.GONE);
                    errorMsg.setVisibility(View.VISIBLE);

                } else {
                    try {
                        JSONObject details = response.getJSONObject();
                        JSONObject place = details.getJSONObject("place");
                        JSONObject location = place.getJSONObject("location");

                        event_name.setText(details.getString("name"));
                        event_description.setText(details.getString("description"));
                        try {
                            event_time.setText(details.getString("start_time") + "\n" + details.getString("end_time"));
                        } catch (Exception e){
                            event_time.setText(details.getString("start_time"));
                        }
                        event_place.setText(place.getString("name"));
                        event_street.setText(location.getString("street") + ", " + location.getString("zip"));
                        event_location.setText(location.getString("city") + ", " + location.getString("country"));
                    } catch(Exception ignored){}
                }


            }
        }).executeAsync();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            super.onBackPressed();
        return true;
    }
}
