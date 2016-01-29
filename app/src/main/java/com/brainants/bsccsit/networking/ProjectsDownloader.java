package com.brainants.bsccsit.networking;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.brainants.bsccsit.advance.Singleton;

import org.json.JSONArray;

public class ProjectsDownloader extends AsyncTask<Void, Void, Void> {

    private OnTaskCompleted listener;

    @Override
    public Void doInBackground(Void... params) {
        StringRequest request = new StringRequest(Request.Method.GET, "http://bsccsit.brainants.com/allprojects", new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                SQLiteDatabase database = Singleton.getInstance().getDatabase();
                database.delete("projects", null, null);
                ContentValues values = new ContentValues();
                try {
                    JSONArray response = new JSONArray(res);
                    for (int i = 0; i < response.length(); i++) {
                        values.clear();
                        values.put("title", response.getJSONObject(i).getString("title"));
                        values.put("detail", response.getJSONObject(i).getString("description"));
                        values.put("tags", response.getJSONObject(i).getString("tags"));
                        values.put("users", response.getJSONObject(i).getLong("user_id") + "");
                        values.put("projectID", response.getJSONObject(i).getString("id"));
                        database.insert("projects", null, values);
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
        });
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
