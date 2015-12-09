package com.techies.bsccsit.advance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase.getWritableDatabase();
    }

    public static void downloadElibrary(){
        JsonArrayRequest request=new JsonArrayRequest(Request.Method.POST, "https://slim-bloodskate.c9users.io/app/api/elibrary", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                SQLiteDatabase database= Singleton.getInstance().getDatabase();
                database.delete("eLibrary",null,null);
                ContentValues values=new ContentValues();
                for(int i=0;i<response.length();i++){
                    try {
                        values.put("Title", response.getJSONObject(i).getString("title"));
                        values.put("Source", response.getJSONObject(i).getString("source"));
                        values.put("Tag", response.getJSONObject(i).getString("tag"));
                        values.put("Link", response.getJSONObject(i).getString("link"));
                        values.put("Link", response.getJSONObject(i).getString("filename"));
                        database.insert("eLibrary",null,values);
                        values.clear();
                    }catch (Exception e){}
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("semester", MyApp.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE).getInt("semester",0)+"");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        Singleton.getInstance().getRequestQueue().add(request);
    }

    public static boolean checkExist(String id){
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT Title FROM myCommunities WHERE FbID = "+id,null);
        if (cursor.moveToNext()){
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }
}
