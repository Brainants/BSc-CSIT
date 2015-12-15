package com.techies.bsccsit.activities;

import android.content.ContentValues;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.techies.bsccsit.advance.BackgroundTaskHandler;
import com.techies.bsccsit.advance.Singleton;

import org.json.JSONObject;

import java.util.Date;

public class FbEvent extends AppCompatActivity {


    private String eventId;
    private ProgressBar progressBar;
    private LinearLayout errorMsg;
    private FloatingActionButton fab;
    private NestedScrollView nestedScrollEvent;

    private TextView event_name, event_description, event_place, event_location, event_street, event_time;
    private String time;

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
        ImageView event_photo = (ImageView) findViewById(R.id.eventPhoto);
        fab = (FloatingActionButton) findViewById(R.id.eachEventFab);
        nestedScrollEvent= (NestedScrollView) findViewById(R.id.nestedScrollEvent);
        CollapsingToolbarLayout mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.eventCollapse);

        eventId = getIntent().getStringExtra("eventID");
        String eventName = getIntent().getStringExtra("eventName");
        String eventImage = getIntent().getStringExtra("imageURL");
        time= getIntent().getStringExtra("eventTime");

        progressBar = (ProgressBar) findViewById(R.id.progressbarFbEvent);
        errorMsg = (LinearLayout) findViewById(R.id.errorMessagePageEvent);
        errorMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFromInternet();
            }
        });

        Date current = new Date();
        current.setTime(System.currentTimeMillis());

        if (BackgroundTaskHandler.convertToSimpleDate(time)
                .compareTo(current)>0)
            fab.setVisibility(View.VISIBLE);
        else
            fab.setVisibility(View.GONE);

        if (Singleton.isScheduledEvent(eventId)) {
            fab.setImageResource(R.drawable.calender_check_white);
            fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.colorAccent)));
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Singleton.isScheduledEvent(eventId)) {
                    fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(FbEvent.this, R.color.white)));
                    fab.setImageResource(R.drawable.calender_plus);
                    Snackbar.make(MainActivity.drawerLayout, "Remainder removed.", Snackbar.LENGTH_SHORT).show();
                    Singleton.getInstance().getDatabase().execSQL("DELETE FROM remainder WHERE eventID = " + eventId);
                } else {
                    fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(FbEvent.this, R.color.colorAccent)));
                    fab.setImageResource(R.drawable.calender_check_white);
                    Snackbar.make(MainActivity.drawerLayout, "Remainder scheduled.", Snackbar.LENGTH_SHORT).show();
                    ContentValues values = new ContentValues();
                    values.put("eventID", eventId);
                    values.put("created_time",time);
                    Singleton.getInstance().getDatabase().insert("remainder", null, values);
                }
            }
        });


        setSupportActionBar((Toolbar) findViewById(R.id.eventToolbar));
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCollapsingToolbar.setTitle(eventName);

        Picasso.with(this).load(eventImage).into(event_photo);

        downloadFromInternet();
    }

    private void downloadFromInternet() {

        errorMsg.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        nestedScrollEvent.setVisibility(View.GONE);
        Bundle params = new Bundle();
        params.putString("fields", "description,name,place,start_time,end_time");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + eventId, params, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                progressBar.setVisibility(View.GONE);
                FacebookRequestError error = response.getError();
                if (error != null) {
                    errorMsg.setVisibility(View.VISIBLE);
                } else {
                    try {
                        JSONObject details = response.getJSONObject();
                        JSONObject place = details.getJSONObject("place");
                        JSONObject location = place.getJSONObject("location");

                        event_name.setText(details.getString("name"));
                        event_description.setText(details.getString("description"));
                        time = details.getString("start_time");
                        try {
                            event_time.setText(details.getString("start_time") + "\n" + details.getString("end_time"));
                        } catch (Exception e) {
                            event_time.setText(details.getString("start_time"));
                        }
                        event_place.setText(place.getString("name"));
                        event_street.setText(location.getString("street") + ", " + location.getString("zip"));
                        event_location.setText(location.getString("city") + ", " + location.getString("country"));
                        nestedScrollEvent.setVisibility(View.VISIBLE);
                    } catch (Exception ignored) {}
                }
            }
        }).executeAsync();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }
}
