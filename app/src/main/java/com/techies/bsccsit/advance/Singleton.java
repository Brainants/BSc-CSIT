package com.techies.bsccsit.advance;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateUtils;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.squareup.picasso.Picasso;
import com.techies.bsccsit.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

public class Singleton {
    private static Singleton sInstance = null;
    private RequestQueue mRequestQueue;
    private DatabaseHandler mDatabase;
    private GcmNetworkManager mScheduler;


    private Singleton() {
        mDatabase = new DatabaseHandler(MyApp.getContext());
        mRequestQueue = Volley.newRequestQueue(MyApp.getContext());
        mScheduler = GcmNetworkManager.getInstance(MyApp.getContext());
    }

    public static Singleton getInstance() {
        if (sInstance == null) {
            sInstance = new Singleton();
        }
        return sInstance;
    }

    public static boolean checkExistInFollowing(String id) {
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT Title FROM myCommunities WHERE FbID = " + id, null);
        if (cursor.moveToNext()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public static boolean checkExistInPopular(String id) {
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT Title FROM popularCommunities WHERE FbID = " + id, null);
        if (cursor.moveToNext()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public static CharSequence convertDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        try {
            Date finalDate = simpleDateFormat.parse(date);
            return DateUtils.getRelativeTimeSpanString(finalDate.getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);


        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown Time";
        }
    }



    public static String getFollowingList() {
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT FbID FROM myCommunities", null);
        String string = "";
        while (cursor.moveToNext()) {
            string = string + cursor.getString(cursor.getColumnIndex("FbID")) + ",";
        }
        string = string + "bsccsitapp";
        cursor.close();
        return string;
    }

    public static ArrayList<String> getFollowingArray() {
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT FbID FROM myCommunities", null);
        ArrayList<String> names = new ArrayList<>();
        while (cursor.moveToNext()) {
            names.add(cursor.getString(cursor.getColumnIndex("FbID")));
        }
        names.add("bsccsitapp");
        cursor.close();
        return names;
    }

    public static int eLibraryCount() {
        int i = 0;
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT Title FROM eLibrary", null);
        while (cursor.moveToNext()) {
            i++;
        }
        cursor.close();
        return i;
    }

    public static int getEventNo() {
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT names FROM events", null);
        int i = 0;
        while (cursor.moveToNext()) {
            i++;
        }
        cursor.close();
        return i;
    }

    public static String getLatestEventName() {
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT names FROM events", null);
        String string = "";
        if (cursor.moveToNext())
            string = cursor.getString(cursor.getColumnIndex("names"));
        cursor.close();
        return string;
    }

    public static String getLatestEventHost() {
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT hosters FROM events", null);
        String string = "";
        if (cursor.moveToNext())
            string = cursor.getString(cursor.getColumnIndex("hosters"));
        cursor.close();
        return string;
    }

    public static String getLatestEventId() {
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT eventIDs FROM events", null);
        String string = "";
        if (cursor.moveToNext())
            string = cursor.getString(cursor.getColumnIndex("eventIDs"));
        cursor.close();
        return string;
    }

    public static CharSequence convertToSimpleDate(String created_time) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZZZZZ", Locale.US);
        try {
            Date date = simpleDateFormat.parse(created_time);
            return DateUtils.getRelativeTimeSpanString(date.getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
        } catch (Exception e) {
            return "Unknown Time";
        }
    }

    public static boolean isScheduledEvent(String eventId) {
        return false;
        /*Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM remainder WHERE eventID = " + eventId, null);
        if (cursor.moveToNext()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }*/
    }

    public static String getSemester() {
        return MyApp.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE).getInt("semester", 0) + "sem";
    }

    public static int getSizeName(Context context) {
        int screenLayout = context.getResources().getConfiguration().screenLayout;
        screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenLayout) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return 2;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return 2;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return 1;
            case 4: // Configuration.SCREENLAYOUT_SIZE_XLARGE is API >= 9
                return 1;
            default:
                return 2;
        }
    }

    public static int getSpanCount(Context context) {
        int screenLayout = context.getResources().getConfiguration().screenLayout;
        screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenLayout) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return 1;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return 1;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return 2;
            case 4: // Configuration.SCREENLAYOUT_SIZE_XLARGE is API >= 9
                return 2;
            default:
                return 1;
        }
    }

    public static FancyButton getTagView(Context context, String tag) {
        FancyButton button = (FancyButton) View.inflate(context, R.layout.tag_widget, null);
        button.setText(tag);
        return button;
    }

    public static CircleImageView getProfileView(Context context, String userID) {
        CircleImageView imageView = (CircleImageView) View.inflate(context, R.layout.tag_widget, null);
        Picasso.with(context).load("https://graph.facebook.com/" + userID + "/picture?type=large").into(imageView);
        return imageView;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase.getWritableDatabase();
    }

    public GcmNetworkManager getGcmScheduler() {
        return mScheduler;
    }
}

