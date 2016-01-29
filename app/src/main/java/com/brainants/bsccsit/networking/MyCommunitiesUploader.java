package com.brainants.bsccsit.networking;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.brainants.bsccsit.advance.MyApp;
import com.brainants.bsccsit.advance.Singleton;

import java.util.HashMap;
import java.util.Map;

public class MyCommunitiesUploader {
    private OnTaskCompleted listener;

    public void doInBackground() {
        String url = "http://bsccsit.brainants.com/updateusercommunities";
        final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onTaskCompleted(true);
                MyApp.getContext().getSharedPreferences("community", Context.MODE_PRIVATE).edit().putBoolean("changedComm", false).apply();
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
                params.put("user_id", MyApp.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE).getString("UserID", ""));
                params.put("communities", Singleton.getFollowingList().replace(",bsccsitapp", ""));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        if (MyApp.getContext().getSharedPreferences("community", Context.MODE_PRIVATE).getBoolean("changedComm", false)) {
            request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Singleton.getInstance().getRequestQueue().add(request);
        }
    }

    public void setTaskCompleteListener(OnTaskCompleted listener) {
        this.listener = listener;
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(boolean success);
    }
}

