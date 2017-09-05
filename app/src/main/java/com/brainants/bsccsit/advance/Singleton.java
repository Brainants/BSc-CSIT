package com.brainants.bsccsit.advance;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.brainants.bsccsit.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import mehdi.sakout.fancybuttons.FancyButton;

public class Singleton {
    private static Singleton sInstance = null;
    private RequestQueue mRequestQueue;
    private DatabaseHandler mDatabase;


    private Singleton() {
        mDatabase = new DatabaseHandler(MyApp.getContext());
        mRequestQueue = Volley.newRequestQueue(MyApp.getContext());
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

    public static String getName() {
        return MyApp.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE).getString("FirstName", "User");
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
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT Title FROM eLibrary ", null);
        i = cursor.getCount();
        cursor.close();
        return i;
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

    public static long isScheduledEvent(String eventId) {
        return MyApp.getContext().getSharedPreferences("event", Context.MODE_PRIVATE).getLong(eventId, -1);
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

    public static FancyButton getTagView(final Context context, final String tag) {
        FancyButton button = (FancyButton) View.inflate(context, R.layout.tag_widget, null);
        button.setText(tag);

        return button;
    }

    public static int getElibraryCount() {
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM eLibrary", null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public static int noticeCount() {
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM notices", null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public static int getCommunityCount() {
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM popularCommunities", null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public static int getNewsCount() {
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM news", null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public static boolean canShowNotif(String tag) {
        SharedPreferences notif = MyApp.getContext().getSharedPreferences("notification", Context.MODE_PRIVATE);
        return notif.getBoolean(tag, true);
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase.getWritableDatabase();
    }


    public static boolean hasNewNotifications() {
        return MyApp.getContext().getSharedPreferences("notifications", Context.MODE_PRIVATE).getBoolean("hasNewNotif", false);
    }

    public static void setNotificationStatus(boolean hasNew) {
        MyApp.getContext().getSharedPreferences("notifications", Context.MODE_PRIVATE).edit().putBoolean("hasNewNotif", hasNew).apply();
    }

    public static int getNotifCount() {
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM notifications", null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public static long calenderID(Context context) {
        return context.getSharedPreferences("event", Context.MODE_PRIVATE).getLong("calenderID", createCalender(context));
    }

    public static long createCalender(Context context) {
        ContentValues values = new ContentValues();
        values.put(
                CalendarContract.Calendars.ACCOUNT_NAME,
                "brainants_bsccsit");
        values.put(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL);
        values.put(
                CalendarContract.Calendars.NAME,
                "BSc CSIT Calender");
        values.put(
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                "BSc CSIT Events Calender");
        values.put(
                CalendarContract.Calendars.CALENDAR_COLOR,
                0xff009688);
        values.put(
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars.CAL_ACCESS_OWNER);
        values.put(
                CalendarContract.Calendars.OWNER_ACCOUNT,
                "info@brainants.com");
        values.put(
                CalendarContract.Calendars.CALENDAR_TIME_ZONE,
                "Europe/Berlin");
        values.put(
                CalendarContract.Calendars.SYNC_EVENTS,
                1);
        Uri.Builder builder =
                CalendarContract.Calendars.CONTENT_URI.buildUpon();
        builder.appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_NAME,
                "com.brainants.bsccsit");
        builder.appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL);
        builder.appendQueryParameter(
                CalendarContract.CALLER_IS_SYNCADAPTER,
                "true");
        Uri uri =
                context.getContentResolver().insert(builder.build(), values);
        context.getSharedPreferences("event", Context.MODE_PRIVATE).edit().putLong("calenderID", Long.valueOf(uri.getLastPathSegment())).apply();
        return Long.valueOf(uri.getLastPathSegment());
    }

    public int getDownloadID(String fileName) {
        return MyApp.getContext().getSharedPreferences("download_ids", Context.MODE_PRIVATE).getInt(fileName, 0);
    }

    public void setDownloadId(int downloadId, String fileName) {
        MyApp.getContext().getSharedPreferences("download_ids", Context.MODE_PRIVATE).edit().putInt(fileName, downloadId).apply();
    }
}

