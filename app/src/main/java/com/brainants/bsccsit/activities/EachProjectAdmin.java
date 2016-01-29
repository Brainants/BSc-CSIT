package com.brainants.bsccsit.activities;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

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
import com.brainants.bsccsit.advance.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

public class EachProjectAdmin extends AppCompatActivity {
    RobotoTextView title, detail, seekingCount, membersHeader, requestsHeader;
    LinearLayout tagHolder, userHolder, errorLayout, requestHolder;
    NestedScrollView mScrollView;
    ProgressBar loading;
    String project_id;
    HorizontalScrollView horizontalScrollViewUser, horizontalScrollViewRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_project_admin);
        setSupportActionBar((Toolbar) findViewById(R.id.eachProjectAdminToolbar));

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        title = (RobotoTextView) findViewById(R.id.eachProjectTitle);
        detail = (RobotoTextView) findViewById(R.id.eachProjectDetail);
        tagHolder = (LinearLayout) findViewById(R.id.tagHolderEachProject);
        userHolder = (LinearLayout) findViewById(R.id.usersHolderProject);
        requestsHeader = (RobotoTextView) findViewById(R.id.requestHeader);
        requestHolder = (LinearLayout) findViewById(R.id.requestHolderProject);
        horizontalScrollViewRequest = (HorizontalScrollView) findViewById(R.id.horizontalRequest);
        errorLayout = (LinearLayout) findViewById(R.id.errorMessageEachProject);
        mScrollView = (NestedScrollView) findViewById(R.id.nestedScrollProject);
        loading = (ProgressBar) findViewById(R.id.progressbarEachProject);
        seekingCount = (RobotoTextView) findViewById(R.id.seekingCount);
        membersHeader = (RobotoTextView) findViewById(R.id.membersHeader);
        horizontalScrollViewUser = (HorizontalScrollView) findViewById(R.id.horizontalUser);
        errorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFromInternet();
            }
        });

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
                    JSONObject coreObject = new JSONObject(response);
                    title.setText(coreObject.getString("title"));
                    setTitle(coreObject.getString("title"));
                    detail.setText(coreObject.getString("description"));
                    fillTags(coreObject.getString("tags"));
                    fillUsers(coreObject.getJSONArray("members"));
                    fillRequest(coreObject.getJSONArray("requests"));

                    if ((coreObject.getInt("required_users") - coreObject.getInt("num_users")) != 0)
                        seekingCount.setText("Seeking for " + (coreObject.getInt("required_users") - coreObject.getInt("num_users")) + " members.");
                    else {
                        seekingCount.setText("Team is full.");
                    }
                    handleEdit();
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

    private void handleEdit() {

    }

    private void fillRequest(final JSONArray requests) throws Exception {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 4, 0);
        requestHolder.removeAllViews();
        for (int i = 0; i < requests.length(); i++) {
            final RelativeLayout eachUser = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.user_widget, null);

            RobotoTextView name = (RobotoTextView) eachUser.findViewById(R.id.name);
            CircleImageView userView = (CircleImageView) eachUser.findViewById(R.id.image);
            name.setText(requests.getJSONObject(i).getString("name"));
            Picasso.with(this).load("https://graph.facebook.com/" + requests.getJSONObject(i).getString("id") + "/picture?type=large").into(userView);
            final int finalI = i;
            eachUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        new MaterialDialog.Builder(EachProjectAdmin.this)
                                .title(requests.getJSONObject(finalI).getString("name"))
                                .items(new String[]{"View Profile", "Accept Request"})
                                .itemsCallback(new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                        try {
                                            if (which == 0) {
                                                startActivity(new Intent(EachProjectAdmin.this, UserProfile.class).putExtra("userID", requests.getJSONObject(finalI).getString("id")));
                                                dialog.dismiss();
                                            } else
                                                acceptRequest(requests.getJSONObject(finalI), dialog);
                                        } catch (Exception e) {}
                                    }
                                })
                                .show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            requestHolder.addView(eachUser, params);
        }
        if (requests.length() == 0) {
            requestsHeader.setText("No requests");
            horizontalScrollViewRequest.setVisibility(View.GONE);
        }
    }

    private void acceptRequest(final JSONObject jsonObject, final MaterialDialog dialogPrevious) throws Exception {
        final MaterialDialog dialog = new MaterialDialog.Builder(EachProjectAdmin.this)
                .content("Requesting...")
                .progress(true, 0)
                .build();
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, "http://bsccsit.brainants.com/accept", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                dialogPrevious.dismiss();
                if (response.toLowerCase().contains("success")) {
                    loadFromInternet();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(EachProjectAdmin.this, "Unable to connect.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                try {
                    params.put("id", String.valueOf(jsonObject.getInt("id")));
                } catch (JSONException e) {
                }
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

    private void fillUsers(final JSONArray users) throws Exception {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(4, 0, 4, 0);
        userHolder.removeAllViews();
        for (int i = 0; i < users.length(); i++) {
            RelativeLayout eachUser = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.user_widget, null);

            RobotoTextView name = (RobotoTextView) eachUser.findViewById(R.id.name);
            CircleImageView userView = (CircleImageView) eachUser.findViewById(R.id.image);
            name.setText(users.getJSONObject(i).getString("name"));
            Picasso.with(this).load("https://graph.facebook.com/" + users.getJSONObject(i).getString("id") + "/picture?type=large").into(userView);
            final int finalI = i;
            eachUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        startActivity(new Intent(EachProjectAdmin.this, UserProfile.class).putExtra("userID", users.getJSONObject(finalI).getString("id")));
                    } catch (JSONException e) {
                    }
                }
            });
            userHolder.addView(eachUser, params);
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
