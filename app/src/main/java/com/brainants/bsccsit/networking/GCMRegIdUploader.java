package com.brainants.bsccsit.networking;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.brainants.bsccsit.R;
import com.brainants.bsccsit.advance.MyApp;
import com.brainants.bsccsit.advance.Singleton;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GCMRegIdUploader {
    String token;

    public void doInBackground() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                InstanceID instanceID = InstanceID.getInstance(MyApp.getContext());
                try {
                    token = instanceID.getToken(MyApp.getContext().getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    Singleton.getInstance().getRequestQueue().add(new StringRequest(Request.Method.POST, MyApp.getContext().getString(R.string.addRegId), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            MyApp.getContext().getSharedPreferences("notification", Context.MODE_PRIVATE).edit().putBoolean("updatedGCM", true).apply();
                        }
                    }, null) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("user_id", MyApp.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE).getString("UserID", ""));
                            params.put("reg_id", token);
                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("Content-Type", "application/x-www-form-urlencoded");
                            return params;
                        }
                    });
                } catch (IOException e) {
                    token = "Failed" + e;
                }
                return null;
            }
        }.execute();
    }
}
