package com.techies.bsccsit.networking;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.techies.bsccsit.advance.MyApp;
import com.techies.bsccsit.advance.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCommunitiesDownloader {

    private OnTaskCompleted listener;

    public void doInBackground() {
        String url = "http://bsccsit.brainants.com/getusercommunities";
        final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ArrayList<String> list = new ArrayList<>();
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++)
                        list.add(array.getString(i));
                    String tags = "";
                    for (String aSelectedTxt : list) {
                        tags = tags + aSelectedTxt + ",";
                    }
                    Log.d("Debug", tags.substring(0, tags.length() - 1));
                    fillMyCommFromResponse(tags.substring(0, tags.length() - 1), list);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onTaskCompleted(false);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", MyApp.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE).getString("UserID", ""));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        Singleton.getInstance().getRequestQueue().add(request);
    }

    private void fillMyCommFromResponse(String response, final List<String> pages) {
        Bundle param = new Bundle();
        param.putString("fields", "name,category");
        param.putString("ids", response);
        Log.d("Debug", pages.toString());
        new GraphRequest(AccessToken.getCurrentAccessToken(), "", param, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if (response.getError() != null) {
                    listener.onTaskCompleted(false);
                    response.getError().getException().printStackTrace();
                } else {
                    JSONObject object = response.getJSONObject();
                    ContentValues values = new ContentValues();
                    try {
                        for (int i = 0; i < pages.size(); i++) {
                            values.clear();
                            JSONObject eachPage = object.getJSONObject(pages.get(i));
                            values.put("FbID", eachPage.getString("id"));
                            values.put("Title", eachPage.getString("name"));
                            values.put("ExtraText", eachPage.getString("category"));
                            Singleton.getInstance().getDatabase().insert("myCommunities", null, values);
                        }
                        listener.onTaskCompleted(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        listener.onTaskCompleted(false);
                    }
                }
            }
        }).executeAsync();
    }

    public void setTaskCompleteListener(OnTaskCompleted listener) {
        this.listener = listener;
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(boolean success);
    }
}
