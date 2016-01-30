package com.brainants.bsccsit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.devspark.robototextview.widget.RobotoTextView;
import com.squareup.picasso.Picasso;
import com.brainants.bsccsit.R;
import com.brainants.bsccsit.advance.MyApp;
import com.brainants.bsccsit.advance.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

public class EachProject extends AppCompatActivity {

    CircleImageView adminImageView;
    RobotoTextView title, detail, seekingCount, managedBy, membersHeader;
    LinearLayout tagHolder, userHolder, errorLayout;
    FancyButton sendRequest;
    NestedScrollView mScrollView;
    ProgressBar loading;
    String project_id;
    HorizontalScrollView horizontalScrollViewUser;
    boolean requested = false;
    boolean isMember=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_project);

        adminImageView = (CircleImageView) findViewById(R.id.adminImageProject);
        title = (RobotoTextView) findViewById(R.id.eachProjectTitle);
        detail = (RobotoTextView) findViewById(R.id.eachProjectDetail);
        tagHolder = (LinearLayout) findViewById(R.id.tagHolderEachProject);
        userHolder = (LinearLayout) findViewById(R.id.usersHolderProject);
        errorLayout = (LinearLayout) findViewById(R.id.errorMessageEachProject);
        sendRequest = (FancyButton) findViewById(R.id.sendRequestProject);
        mScrollView = (NestedScrollView) findViewById(R.id.nestedScrollProject);
        loading = (ProgressBar) findViewById(R.id.progressbarEachProject);
        seekingCount = (RobotoTextView) findViewById(R.id.seekingCount);
        managedBy = (RobotoTextView) findViewById(R.id.adminName);
        membersHeader = (RobotoTextView) findViewById(R.id.membersHeader);
        horizontalScrollViewUser = (HorizontalScrollView) findViewById(R.id.horizontalUser);
        errorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFromInternet();
            }
        });

        setSupportActionBar((Toolbar) findViewById(R.id.eachProjectToolbar));
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
        project_id = getIntent().getStringExtra("project_id");
        loadFromInternet();
    }

    private void loadFromInternet() {
        mScrollView.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);

        StringRequest request = new StringRequest(Request.Method.POST, "http://bsccsit.brainants.com/getproject", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    mScrollView.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);
                    final JSONObject coreObject = new JSONObject(response);
                    title.setText(coreObject.getString("title"));
                    setTitle(coreObject.getString("title"));
                    detail.setText(coreObject.getString("description"));
                    Picasso.with(EachProject.this).load("https://graph.facebook.com/" + coreObject.getLong("user_id") + "/picture?type=large").into(adminImageView);
                    adminImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                startActivity(new Intent(EachProject.this,UserProfile.class).putExtra("user_id",String.valueOf(coreObject.getLong("user_id"))));
                            } catch (JSONException e) {}
                        }
                    });
                    fillTags(coreObject.getString("tags"));
                    fillUsers(coreObject.getJSONObject("admin"), coreObject.getJSONArray("members"));
                    handleRequestButton(coreObject.getJSONArray("requests"));

                    if ((coreObject.getInt("required_users") - coreObject.getInt("num_users")) != 0)
                        seekingCount.setText("Seeking for " + (coreObject.getInt("required_users") - coreObject.getInt("num_users")) + " members.");
                    else {
                        seekingCount.setText("Team is full.");
                        sendRequest.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mScrollView.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("project_id", project_id);
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
    }

    private void handleRequestButton(JSONArray requests) throws Exception {
        requested = false;
        for (int i = 0; i < requests.length(); i++) {
            String userId = requests.getJSONObject(i).getLong("user_id") + "";
            if (userId.equals(MyApp.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE).getString("UserID", "")))
                requested = true;
        }
        if (requested) {
            sendRequest.setText("Cancel Request");
            sendRequest.setIconResource(R.drawable.cross);
            sendRequest.setBackgroundColor(ContextCompat.getColor(this, R.color.unfollowColor));
        } else {
            sendRequest.setText("Send Request");
            sendRequest.setIconResource(R.drawable.plus);
            sendRequest.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        }
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MaterialDialog dialog = new MaterialDialog.Builder(EachProject.this)
                        .content("Requesting...")
                        .progress(true, 0)
                        .build();
                dialog.show();
                String method;
                if (requested)
                    method = "cancelrequest";
                else
                    method = "request";

                StringRequest request = new StringRequest(Request.Method.POST, "http://bsccsit.brainants.com/" + method, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        if (response.contains("true")) {
                            loadFromInternet();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Snackbar.make(findViewById(R.id.projectEachCoordinator), "Unable to connect.", Snackbar.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("project_id", project_id);
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
                request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Singleton.getInstance().getRequestQueue().add(request);

            }
        });
    }

    private void fillUsers(JSONObject admin, final JSONArray users) throws Exception {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,4,0);
        managedBy.setText(admin.getString("name"));
        userHolder.removeAllViews();
        for (int i = 0; i < users.length(); i++) {
            RelativeLayout eachUser = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.user_widget, null);

            RobotoTextView name = (RobotoTextView) eachUser.findViewById(R.id.name);
            CircleImageView userView = (CircleImageView) eachUser.findViewById(R.id.image);
            name.setText(users.getJSONObject(i).getString("name").split(" ")[0]);
            Picasso.with(this).load("https://graph.facebook.com/" + users.getJSONObject(i).getString("id") + "/picture?type=large").into(userView);
            userHolder.addView(eachUser, params);
            if(users.getJSONObject(i).getString("id").equals(MyApp.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE).getString("UserID", "")))
                sendRequest.setVisibility(View.GONE);
            final int finalI = i;
            eachUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        startActivity(new Intent(EachProject.this, UserProfile.class).putExtra("userID", users.getJSONObject(finalI).getString("id")));
                    } catch (JSONException e) {
                    }
                }
            });
        }
        if (users.length() == 0) {
            membersHeader.setText("No members");
            horizontalScrollViewUser.setVisibility(View.GONE);
        }
    }

    private void fillTags(String tags) {
        String[] tag = tags.split(",");
        tagHolder.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (String eachTagString : tag) {
            FancyButton eachTag = (FancyButton) LayoutInflater.from(this).inflate(R.layout.tag_widget, null);
            params.setMargins(4, 0, 4, 0);
            eachTag.setText(eachTagString);
            tagHolder.addView(eachTag, params);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
