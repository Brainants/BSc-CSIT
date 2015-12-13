package com.techies.bsccsit.advance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GcmNetworkManager;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase.getWritableDatabase();
    }

    public static boolean checkExistInFollowing(String id){
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT Title FROM myCommunities WHERE FbID = "+id,null);
        if (cursor.moveToNext()){
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public static boolean checkExistInPopular(String id){
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT Title FROM popularCommunities WHERE FbID = "+id,null);
        if (cursor.moveToNext()){
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public static String getFollowingList(){
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT FbID FROM myCommunities",null);
        String string="";
        while(cursor.moveToNext()){
            string=string+cursor.getString(cursor.getColumnIndex("FbID"))+",";
        }
        string=string+"bsccsitapp";
        cursor.close();
        return string;
    }

    public static ArrayList<String> getFollowingArray(){
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT FbID FROM myCommunities",null);
        ArrayList<String> names=new ArrayList<>();
        while(cursor.moveToNext()){
            names.add(cursor.getString(cursor.getColumnIndex("FbID")));
        }
        names.add("bsccsitapp");
        cursor.close();
        return names;
    }

    public static int getEventNo(){
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT names FROM events",null);
        int i=0;
        while(cursor.moveToNext()){
            i++;
        }
        cursor.close();
        return i;
    }

    public static String getLatestEventName(){
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT names FROM events",null);
        String string="";
        if(cursor.moveToLast())
            string= cursor.getString(cursor.getColumnIndex("names"));
        cursor.close();
        return string;
    }

    public static String getLatestEventHost(){
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT hosters FROM events",null);
        String string="";
        if(cursor.moveToLast())
            string= cursor.getString(cursor.getColumnIndex("hosters"));
        cursor.close();
        return string;
    }

    public static String getLatestEventId(){
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT eventIDs FROM events",null);
        String string="";
        if(cursor.moveToLast())
            string= cursor.getString(cursor.getColumnIndex("eventIDs"));
        cursor.close();
        return string;
    }

    public static CharSequence convertToSimpleDate(String created_time) {

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZZZZZ", Locale.US);
        try {
            Date date = simpleDateFormat.parse(created_time);
            return DateUtils.getRelativeTimeSpanString(date.getTime(),System.currentTimeMillis(),DateUtils.SECOND_IN_MILLIS,DateUtils.FORMAT_ABBREV_RELATIVE);
        } catch (Exception e) {
            return "Unknown Time";
        }
    }

    public GcmNetworkManager getGcmScheduler() {
        return mScheduler;
    }
}
