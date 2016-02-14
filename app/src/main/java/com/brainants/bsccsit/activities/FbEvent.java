package com.brainants.bsccsit.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.brainants.bsccsit.R;
import com.brainants.bsccsit.advance.Singleton;
import com.devspark.robototextview.widget.RobotoTextView;
import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FbEvent extends AppCompatActivity {


    private String eventId;
    private String eventName;
    private String eventPhoto;
    private String eventPlace;
    private double latitude;
    private double longitude;
    private ProgressBar progressBar;
    private LinearLayout errorMsg;
    private FloatingActionButton fab;
    private NestedScrollView nestedScrollEvent;
    private LinearLayout locationLayout;
    String startTime = "", endTime = "";
    private RobotoTextView event_name, event_description, event_place, event_location, event_street, event_time, hosted_by;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_event);

        event_name = (RobotoTextView) findViewById(R.id.eventName);
        event_description = (RobotoTextView) findViewById(R.id.eventDesc);
        event_place = (RobotoTextView) findViewById(R.id.eventPlace);
        event_location = (RobotoTextView) findViewById(R.id.eventLocation);
        event_street = (RobotoTextView) findViewById(R.id.eventStreet);
        event_time = (RobotoTextView) findViewById(R.id.eventTime);
        hosted_by = (RobotoTextView) findViewById(R.id.eventHost);


        ImageView event_photo = (ImageView) findViewById(R.id.eventPhoto);

        fab = (FloatingActionButton) findViewById(R.id.eachEventFab);

        nestedScrollEvent = (NestedScrollView) findViewById(R.id.nestedScrollEvent);

        CollapsingToolbarLayout mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.eventCollapse);

        eventId = getIntent().getStringExtra("eventID");

        locationLayout = (LinearLayout) findViewById(R.id.locationLayout);


        progressBar = (ProgressBar) findViewById(R.id.progressbarFbEvent);
        errorMsg = (LinearLayout) findViewById(R.id.errorMessagePageEvent);
        errorMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFromInternet();
            }
        });


        if (Singleton.isScheduledEvent(eventId)!=-1) {
            fab.setImageResource(R.drawable.calender_check_white);
            fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent)));
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent();
            }
        });


        setSupportActionBar((Toolbar) findViewById(R.id.eventToolbar));
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM events WHERE eventIDs ='" + eventId + "'", null);
        while (cursor.moveToNext()) {
            eventName = cursor.getString(cursor.getColumnIndex("names"));
            eventPhoto = cursor.getString(cursor.getColumnIndex("fullImage"));
        }

        mCollapsingToolbar.setTitle(eventName);

        if (!eventPhoto.equals(""))
            Picasso.with(this).load(eventPhoto).into(event_photo);

        downloadFromInternet();

        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FbEvent.this, EventVenueMap.class)
                        .putExtra("name", eventPlace)
                        .putExtra("lat", latitude)
                        .putExtra("long", longitude));
            }
        });
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
                        JSONObject place = null;
                        JSONObject location = null;

                        event_name.setText(details.getString("name"));

                        try {
                            place = details.getJSONObject("place");
                        } catch (Exception e) {
                        }

                        try {
                            location = place.getJSONObject("location");
                        } catch (Exception e) {
                        }

                        try {
                            event_description.setText(details.getString("description"));
                        } catch (Exception e) {
                            event_description.setText("No event description available for now!!");
                        }


                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZZZZZ", Locale.US);

                        try {
                            startTime = details.getString("start_time");
                            endTime = details.getString("end_time");

                            Date current = new Date();
                            current.setTime(System.currentTimeMillis());

                            if(current.compareTo(format.parse(startTime))>0)
                                fab.setVisibility(View.GONE);
                            else
                                fab.setVisibility(View.VISIBLE);


                            String strMonthStart = (String) android.text.format.DateFormat.format("MMM", format.parse(startTime));
                            String dayStart = (String) android.text.format.DateFormat.format("dd", format.parse(startTime));
                            String yearStart = (String) android.text.format.DateFormat.format("yyy", format.parse(startTime));
                            String dayWeekStart = (String) android.text.format.DateFormat.format("EEEE", format.parse(startTime));

                            String strMonthEnd = (String) android.text.format.DateFormat.format("MMM", format.parse(endTime));
                            String dayEnd = (String) android.text.format.DateFormat.format("dd", format.parse(endTime));
                            String yearEnd = (String) android.text.format.DateFormat.format("yyy", format.parse(endTime));
                            String dayWeekEnd = (String) android.text.format.DateFormat.format("EEEE", format.parse(endTime));

                            event_time.setText(dayStart + " " + strMonthStart + " " + yearStart + " (" + dayWeekStart + ")" +
                                    " to \n" + dayEnd + " " + strMonthEnd + " " + yearEnd + " (" + dayWeekEnd + ")");


                        } catch (JSONException e) {
                            String strMonthStart = (String) android.text.format.DateFormat.format("MMM", format.parse(startTime));
                            String dayStart = (String) android.text.format.DateFormat.format("dd", format.parse(startTime));
                            String yearStart = (String) android.text.format.DateFormat.format("yyy", format.parse(startTime));
                            String dayWeekStart = (String) android.text.format.DateFormat.format("EEEE", format.parse(startTime));

                            event_time.setText(dayStart + " " + strMonthStart + ", " + yearStart + " (" + dayWeekStart + ")");
                        }


                        try {
                            eventPlace = place.getString("name");
                            event_place.setText(eventPlace);
                        } catch (Exception e) {
                            event_place.setVisibility(View.GONE);
                        }

                        try {
                            event_location.setText(location.getString("city") + ", " + location.getString("country"));
                        } catch (Exception e) {
                            event_location.setVisibility(View.GONE);
                        }

                        try {
                            latitude = location.getDouble("latitude");
                            longitude = location.getDouble("longitude");
                        } catch (Exception e) {

                        }

                        try {
                            event_street.setText(location.getString("street") + ", " + location.getString("zip"));

                        } catch (Exception e) {
                            event_street.setVisibility(View.GONE);
                        }
                        nestedScrollEvent.setVisibility(View.VISIBLE);
                    } catch (Exception ignored) {
                    }
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

    private void addEvent() {
        long calId = Singleton.calenderID(this);
        long startTimeForomDate;
        try {
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZZZZZ", Locale.US);
            startTimeForomDate= sfd.parse(startTime).getTime();
        } catch (ParseException e) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startTimeForomDate);
        values.put(CalendarContract.Events.DTEND, startTimeForomDate);
        values.put(CalendarContract.Events.TITLE, event_name.getText().toString());
        values.put(CalendarContract.Events.EVENT_LOCATION, event_place.getText().toString());
        values.put(CalendarContract.Events.CALENDAR_ID, calId);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kathmandu");
        values.put(CalendarContract.Events.DESCRIPTION,event_description.getText().toString());
// reasonable defaults exist:
        values.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
        values.put(CalendarContract.Events.SELF_ATTENDEE_STATUS,
                CalendarContract.Events.STATUS_CONFIRMED);
        values.put(CalendarContract.Events.ALL_DAY, 1);
        values.put(CalendarContract.Events.ORGANIZER, hosted_by.getText().toString());
        values.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, 1);
        values.put(CalendarContract.Events.GUESTS_CAN_MODIFY, 1);
        values.put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        if (ActivityCompat.checkSelfPermission(FbEvent.this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {

            Uri uri = getContentResolver().insert(CalendarContract.Events.CONTENT_URI, values);
            long addedEventId = new Long(uri.getLastPathSegment());

            values.clear();
            values.put(CalendarContract.Reminders.EVENT_ID, addedEventId);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            values.put(CalendarContract.Reminders.MINUTES, 60);
            getContentResolver().insert(CalendarContract.Reminders.CONTENT_URI, values);
            getSharedPreferences("event",MODE_PRIVATE).edit().putLong(eventId,addedEventId).apply();
        }

    }
}
