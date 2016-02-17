package com.brainants.bsccsit.networking;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.brainants.bsccsit.R;
import com.brainants.bsccsit.advance.MyApp;
import com.brainants.bsccsit.advance.Singleton;

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
        JsonArrayRequest request = new JsonArrayRequest(MyApp.getContext().getString(R.string.allNotifications), new Response.Listener<JSONArray>() {
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

        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Singleton.getInstance().getRequestQueue().add(request);

    }

    private void storeToDb() {

        SQLiteDatabase database = Singleton.getInstance().getDatabase();
        database.delete("notifications", null, null);
        ContentValues values = new ContentValues();

        for (int i = 0; i < title.size(); i++) {
            values.clear();
            values.put("title", title.get(i));
            values.put("desc", desc.get(i));
            values.put("link", link.get(i));
            values.put("show", show.get(i));
            database.insert("notifications", null, values);
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
