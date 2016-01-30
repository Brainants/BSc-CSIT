package com.brainants.bsccsit.networking;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.brainants.bsccsit.advance.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TagsDownloader {
    ArrayList<String> mTags = new ArrayList<>();
    ArrayList<Integer> mIds = new ArrayList<>();
    private ClickListener listener;

    public void doInBackground() {
        String url = "http://bsccsit.brainants.com/alltags";
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        mIds.add(object.getInt("id"));
                        mTags.add(object.getString("name"));
                    }
                    storeToDb();
                } catch (JSONException e) {
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onTaskComplete(false);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Singleton.getInstance().getRequestQueue().add(request);
    }

    private void storeToDb() {
        SQLiteDatabase database = Singleton.getInstance().getDatabase();
        database.delete("tags", null, null);
        ContentValues values = new ContentValues();

        for (int i = 0; i < mTags.size(); i++) {
            values.clear();
            values.put("id", mIds.get(i));
            values.put("tag_name", mTags.get(i));
            database.insert("tags", null, values);
        }
        listener.onTaskComplete(true);
    }

    public void setOnTaskCompleteListener(ClickListener listener) {
        this.listener = listener;
    }

    public interface ClickListener {
        void onTaskComplete(boolean success);
    }
}
