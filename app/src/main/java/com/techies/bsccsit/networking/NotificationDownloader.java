package com.techies.bsccsit.networking;


import android.app.DownloadManager;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.techies.bsccsit.advance.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationDownloader {
    ArrayList<String> title = new ArrayList<>(),
            desc = new ArrayList<>(),
            link = new ArrayList<>();

    ArrayList<Integer> show = new ArrayList<>();
    ClickListener listener;

    public void doInBackground() {
        String url = "http://bsccsit.brainants.com/allnotifications";
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        title.add(object.getString("title"));
                        desc.add(object.getString("description"));
                        link.add(object.getString("deeplink"));

                        show.add(object.getInt("show"));
                    }
                    storeToDb();

                } catch (JSONException e) {
                    listener.OnTaskCompleted(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.OnTaskCompleted(false);
            }
        });

    }

    private void storeToDb() {

        SQLiteDatabase database = Singleton.getInstance().getDatabase();
        database.delete("notifications",null,null);
        ContentValues values = new ContentValues();

        for (int i = 0; i < title.size(); i++) {
            values.clear();
            if (show.get(i) == 1) {
                values.put("title", title.get(i));
                values.put("desc", desc.get(i));
                values.put("link", link.get(i));
                values.put("show", show.get(i));
                database.insert("notifications", null, values);
            }
        }
        listener.OnTaskCompleted(true);
    }

    public void setOnTaskCompleteListener(ClickListener listener) {
        this.listener = listener;
    }

    public interface ClickListener {
        void OnTaskCompleted(boolean success);
    }
}
