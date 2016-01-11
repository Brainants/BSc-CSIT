package com.techies.bsccsit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.techies.bsccsit.R;
import com.techies.bsccsit.advance.BackgroundTaskHandler;
import com.techies.bsccsit.advance.Singleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class LoginActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    private MaterialDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editor = getSharedPreferences("loginInfo", MODE_PRIVATE).edit();
        preferences = getSharedPreferences("loginInfo", MODE_PRIVATE);

        final FancyButton button = (FancyButton) findViewById(R.id.loginButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccessToken.getCurrentAccessToken() == null)
                    loginButton.callOnClick();
                else
                    postFbLoginWork();
            }
        });

        loginButton = new LoginButton(this);

        loginButton.setReadPermissions("public_profile", "email", "user_hometown", "user_about_me");

        callbackManager = CallbackManager.Factory.create();

        //login button ko kaam
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                postFbLoginWork();
            }

            @Override
            public void onCancel() {
                Snackbar.make(findViewById(R.id.LoginCore), "Login process aborted.", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Snackbar.make(findViewById(R.id.LoginCore), "Unable to connect.", Snackbar.LENGTH_SHORT).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        button.callOnClick();
                    }
                }).show();
            }
        });
    }

    private void postFbLoginWork() {
        if (dialog == null)
            dialog = new MaterialDialog.Builder(this)
                    .content("Logging in...")
                    .progress(true, 0)
                    .cancelable(false)
                    .build();

        dialog.show();
        Bundle bundle = new Bundle();
        bundle.putString("fields", "id,name,email,hometown,gender,first_name,last_name");
        if (preferences.getString("FirstName", "").equals("")) {
            new GraphRequest(AccessToken.getCurrentAccessToken(), "me", bundle, HttpMethod.GET, new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    if (response.getError() != null) {
                        Snackbar.make(findViewById(R.id.LoginCore), "Unable to connect.", Snackbar.LENGTH_SHORT).setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                postFbLoginWork();
                            }
                        }).show();
                        dialog.dismiss();
                        return;
                    }
                    try {
                        JSONObject object = response.getJSONObject();
                        //email save garera complete login activity ma name ani email pass gareko
                        editor.putString("email", object.getString("email"));
                        editor.putString("FirstName", object.getString("first_name"));
                        editor.putString("LastName", object.getString("last_name"));
                        editor.putString("FullName", object.getString("name"));
                        editor.putString("Gender", object.getString("gender"));
                        editor.putString("UserID", object.getString("id"));
                        editor.putString("HomeTown", object.getJSONObject("hometown").getString("name"));
                        editor.apply();
                        postFbLoginWork();
                    } catch (JSONException ignored) {
                    }
                }
            }).executeAsync();
            return;
        }
        if (!preferences.getBoolean("checked", false)) {
            StringRequest request = new StringRequest(Request.Method.POST, "https://slim-bloodskate.c9users.io/app/api/checkUser", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        dialog.dismiss();
                        JSONObject object = new JSONObject(response);
                        if (object.getBoolean("exists")) {
                            addEveryThingToSp(object.getJSONObject("data"));
                            editor.putBoolean("checked", true);
                            editor.apply();
                            postFbLoginWork();
                        } else {
                            editor.putBoolean("loggedFirstIn", true);
                            editor.apply();
                            startActivity(new Intent(LoginActivity.this, CompleteLogin.class));
                            finish();
                        }
                    } catch (Exception ignored) {
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Snackbar.make(findViewById(R.id.LoginCore), "Unable to connect.", Snackbar.LENGTH_SHORT).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            postFbLoginWork();
                        }
                    }).show();
                    dialog.dismiss();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("fbid", preferences.getString("UserID", ""));
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
            return;
        }

        if (Singleton.getFollowingArray().size() - 1 == 0) {
            BackgroundTaskHandler.MyCommunitiesDownloader downloader = new BackgroundTaskHandler.MyCommunitiesDownloader();
            downloader.doInBackground();
            downloader.setTaskCompleteListener(new BackgroundTaskHandler.MyCommunitiesDownloader.OnTaskCompleted() {
                @Override
                public void onTaskCompleted(boolean success) {
                    if (success) {
                        editor.putBoolean("loggedIn", true);
                        editor.apply();
                        Toast.makeText(LoginActivity.this, "Welcome back " + preferences.getString("FirstName", ""), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Snackbar.make(findViewById(R.id.LoginCore), "Unable to connect.", Snackbar.LENGTH_SHORT).setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                postFbLoginWork();
                            }
                        }).show();
                        dialog.dismiss();
                    }
                }
            });
        }

    }

    private void addEveryThingToSp(JSONObject response) {
        try {
            editor.putString("semester", response.getString("semester"));
            editor.putString("college", response.getString("college"));
            editor.putString("phone_number", response.getString("phone_number"));
            editor.putBoolean("admin", response.getInt("admin") == 1);
            editor.apply();
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
