package com.brainants.bsccsit.networking;

import android.content.ContentValues;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.brainants.bsccsit.R;
import com.brainants.bsccsit.advance.MyApp;
import com.brainants.bsccsit.advance.Singleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PopularCommunitiesDownloader {

    private OnTaskCompleted listener;

    public void doInBackground() {
        String url = MyApp.getContext().getString(R.string.allCommunities);
        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Singleton.getInstance().getDatabase().execSQL("DELETE FROM popularCommunities");
                ArrayList<String> ids = new ArrayList<>(),
                        names = new ArrayList<>(),
                        extra = new ArrayList<>();

                ids.clear();
                names.clear();
                extra.clear();
                try {
                    ContentValues values = new ContentValues();
                    Singleton.getInstance().getDatabase().execSQL("DELETE FROM popularCommunities;");
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        ids.add(object.getString("id"));
                        names.add(object.getString("title"));
                        extra.add(object.getString("extra"));

                        values.clear();
                        values.put("FbID", ids.get(i));
                        values.put("Title", names.get(i));
                        values.put("ExtraText", extra.get(i));
                        Singleton.getInstance().getDatabase().insert("popularCommunities", null, values);
                    }
                    listener.onTaskCompleted(true);
                } catch (Exception ignored) {
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
    }

    public void setTaskCompleteListener(OnTaskCompleted listener) {
        this.listener = listener;
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(boolean success);
    }
}
