package com.brainants.bsccsit.networking;

import android.content.ContentValues;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.brainants.bsccsit.advance.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class NoticeDownloader {
    private OnTaskCompleted listener;

    private ArrayList<String> mTitles = new ArrayList<>(),
            mShorts = new ArrayList<>(),
            mDetails = new ArrayList<>(),
            mDates = new ArrayList<>(),
            mAttachmentLinks = new ArrayList<>(),
            mAttachmentTitles = new ArrayList<>();

    private ArrayList<Integer> mIds = new ArrayList<>();

    public void doInBackground() {
        String url = "http://bsccsit.brainants.com/allnotices";
        JsonArrayRequest arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        mIds.add(object.getInt("id"));
                        mTitles.add(object.getString("title"));
                        mShorts.add(object.getString("short_desc"));
                        mDetails.add(object.getString("notice"));
                        try {
                            mAttachmentLinks.add(object.getString("attachment_link"));
                        } catch (JSONException e) {
                            mAttachmentLinks.add("");
                        }

                        try {
                            mAttachmentTitles.add(object.getString("attachment_title"));
                        } catch (JSONException e) {
                            mAttachmentTitles.add("");
                        }
                        mDates.add(object.getString("time"));

                        storeNoticeToDb();
                    } catch (JSONException e) {
                        listener.onTaskCompleted(false);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onTaskCompleted(false);

            }

        });
        arrayRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Singleton.getInstance().getRequestQueue().add(arrayRequest);

    }

    private void storeNoticeToDb() {
        ContentValues values = new ContentValues();
        Singleton.getInstance().getDatabase().delete("notices", null, null);
        for (int i = 0; i < mTitles.size(); i++) {
            values.clear();
            values.put("id", mIds.get(i));
            values.put("title", mTitles.get(i));
            values.put("short_desc", mShorts.get(i));
            values.put("detail", mDetails.get(i));
            values.put("date", mDates.get(i));
            values.put("attachment_link", mAttachmentLinks.get(i));
            values.put("attachment_title", mAttachmentTitles.get(i));
            Singleton.getInstance().getDatabase().insert("notices", null, values);

        }
        listener.onTaskCompleted(true);
    }

    public void setOnTaskCompleteListener(OnTaskCompleted listener) {
        this.listener = listener;
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(boolean success);
    }
}