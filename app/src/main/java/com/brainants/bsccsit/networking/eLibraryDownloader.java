package com.brainants.bsccsit.networking;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.brainants.bsccsit.advance.MyApp;
import com.brainants.bsccsit.advance.Singleton;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class eLibraryDownloader {

    private OnTaskCompleted listener;

    public Void doInBackground(Void... params) {
        StringRequest request = new StringRequest(Request.Method.POST, "http://bsccsit.brainants.com/getelibrary", new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                SQLiteDatabase database = Singleton.getInstance().getDatabase();
                database.delete("eLibrary", null, null);
                ContentValues values = new ContentValues();
                try {
                    JSONArray response = new JSONArray(res);
                    for (int i = 0; i < response.length(); i++) {
                        values.clear();
                        values.put("Title", response.getJSONObject(i).getString("title"));
                        values.put("Source", response.getJSONObject(i).getString("source"));
                        values.put("Tag", response.getJSONObject(i).getString("tag"));
                        values.put("Link", response.getJSONObject(i).getString("link"));
                        values.put("FileName", response.getJSONObject(i).getString("filename"));
                        database.insert("eLibrary", null, values);
                    }
                    listener.onTaskCompleted(true);
                } catch (Exception e) {
                    listener.onTaskCompleted(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onTaskCompleted(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("semester", MyApp.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE).getInt("semester", 0) + "");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Singleton.getInstance().getRequestQueue().add(request);
        return null;
    }

    public void setTaskCompleteListener(OnTaskCompleted listener) {
        this.listener = listener;
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(boolean success);
    }
}
