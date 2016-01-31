package com.brainants.bsccsit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.brainants.bsccsit.R;
import com.brainants.bsccsit.advance.Singleton;
import com.devspark.robototextview.widget.RobotoTextView;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {
    private String userID;

    private ProgressBar progress;
    private LinearLayout errorLayout;
    private RobotoTextView email, hometown, college, semester;
    private ImageView profilePhoto;
    private NestedScrollView scrollView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private LinearLayout projectHolder, communityHolder;
    private RobotoTextView projectHolderTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userID = getIntent().getStringExtra("userID");
        setTitle("");

        setSupportActionBar((Toolbar) findViewById(R.id.profileToolbar));

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        projectHolder = (LinearLayout) findViewById(R.id.projectHolderProfile);
        communityHolder = (LinearLayout) findViewById(R.id.communityHolderProfile);
        progress = (ProgressBar) findViewById(R.id.progressbarProfile);
        errorLayout = (LinearLayout) findViewById(R.id.errorMessageProfile);
        email = (RobotoTextView) findViewById(R.id.emailProfile);
        hometown = (RobotoTextView) findViewById(R.id.hometownProfile);
        college = (RobotoTextView) findViewById(R.id.collegeProfile);
        semester = (RobotoTextView) findViewById(R.id.semesterProfile);
        profilePhoto = (ImageView) findViewById(R.id.profilePhoto);
        scrollView = (NestedScrollView) findViewById(R.id.nestedScrollProfile);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.profileCollapse);
        scrollView.setVisibility(View.GONE);
        projectHolderTitle = (RobotoTextView) findViewById(R.id.projectTitleUser);
        errorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                fetchFromInternet();
            }
        });

        Picasso.with(UserProfile.this).load("https://graph.facebook.com/" + userID + "/picture?type=large").into(profilePhoto);
        fetchFromInternet();
    }

    private void fetchFromInternet() {

        StringRequest arrayRequest = new StringRequest(Request.Method.POST, "http://bsccsit.brainants.com/getprofile", new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {
                progress.setVisibility(View.GONE);
                errorLayout.setVisibility(View.GONE);
                try {
                    JSONObject response = new JSONObject(resp);
                    JSONObject userObject = response.getJSONObject("user");
                    collapsingToolbarLayout.setTitle(userObject.getString("name"));
                    email.setText(userObject.getString("email"));
                    hometown.setText(userObject.getString("location"));
                    college.setText(userObject.getString("college"));
                    semester.setText(getSemester(userObject.getString("semester")));
                    scrollView.setVisibility(View.VISIBLE);
                    fillProjects(response.getJSONArray("admin_projects"), response.getJSONArray("member_projects"));
                    fillCommunities(userObject.getString("communities"));
                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userID);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        arrayRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Singleton.getInstance().getRequestQueue().add(arrayRequest);
    }

    private void fillCommunities(String communities) {
        Bundle params = new Bundle();
        params.putString("fields", "name,category");
        params.putString("ids", communities);
        final String[] comms = communities.split(",");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "", params, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(final GraphResponse response) {
                if (response.getError() == null) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(3, 3, 3, 3);

                    for (int i = 0; i < comms.length; i++) {
                        RelativeLayout eachComm = (RelativeLayout) LayoutInflater.from(UserProfile.this).inflate(R.layout.profile_each_community, null, false);
                        RobotoTextView title = (RobotoTextView) eachComm.findViewById(R.id.nameSearch);
                        RobotoTextView category = (RobotoTextView) eachComm.findViewById(R.id.extraDetail);
                        CircleImageView profilePIc = (CircleImageView) eachComm.findViewById(R.id.profileImage);
                        try {
                            final String titleText = response.getJSONObject().getJSONObject(comms[i]).getString("name");
                            final String categoryText = response.getJSONObject().getJSONObject(comms[i]).getString("category");
                            title.setText(titleText);
                            category.setText(categoryText);
                            Picasso.with(UserProfile.this).load("https://graph.facebook.com/" + comms[i] + "/picture?type=large").into(profilePIc);
                            final int finalI = i;
                            eachComm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(UserProfile.this, FbPage.class)
                                            .putExtra("id", comms[finalI])
                                            .putExtra("name", titleText)
                                            .putExtra("details", categoryText));
                                }
                            });
                        } catch (JSONException e) {
                        }
                        communityHolder.addView(eachComm, params);
                    }
                }
            }
        }).executeAsync();
    }

    private String getSemester(String pos) {
        final String[] semsters = {"Select your semester", "First Semester", "Second Semester", "Third Semester", "Fourth Semester",
                "Fifth Semester", "Sixth Semester", "Seventh Semester", "Eighth Semester"};
        return semsters[Integer.parseInt(pos)];

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void fillProjects(final JSONArray admin, final JSONArray member) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(3, 3, 3, 3);

        for (int i = 0; i < admin.length(); i++) {
            RelativeLayout eachProject = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.each_project, null, false);
            RobotoTextView title = (RobotoTextView) eachProject.findViewById(R.id.projectName);
            RobotoTextView detail = (RobotoTextView) eachProject.findViewById(R.id.projectDetail);
            RobotoTextView adminTag = (RobotoTextView) eachProject.findViewById(R.id.adminTag);
            adminTag.setVisibility(View.VISIBLE);
            try {
                title.setText(admin.getJSONObject(i).getString("title"));
                detail.setText(admin.getJSONObject(i).getString("description"));
            } catch (JSONException e) {
            }
            final int finalI = i;
            eachProject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (admin.getJSONObject(finalI).getString("user_id").equals(getSharedPreferences("loginInfo", Context.MODE_PRIVATE).getString("UserID", "")))
                            startActivity(new Intent(UserProfile.this, EachProjectAdmin.class)
                                    .putExtra("project_id", String.valueOf(admin.optJSONObject(finalI).getLong("id"))));
                        else
                            startActivity(new Intent(UserProfile.this, EachProject.class)
                                .putExtra("project_id", String.valueOf(admin.optJSONObject(finalI).getLong("id"))));

                    } catch (JSONException e) {
                    }
                }
            });
            projectHolder.addView(eachProject, params);
        }

        for (int i = 0; i < member.length(); i++) {
            RelativeLayout eachProject = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.each_project, null, false);
            RobotoTextView title = (RobotoTextView) eachProject.findViewById(R.id.projectName);
            RobotoTextView detail = (RobotoTextView) eachProject.findViewById(R.id.projectDetail);
            try {
                title.setText(member.getJSONObject(i).getString("title"));
                detail.setText(member.getJSONObject(i).getString("description"));
            } catch (JSONException e) {
            }
            final int finalI = i;
            eachProject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        startActivity(new Intent(UserProfile.this, EachProject.class)
                                .putExtra("project_id", String.valueOf(member.optJSONObject(finalI).getLong("id"))));
                    } catch (JSONException e) {
                    }
                }
            });
            projectHolder.addView(eachProject, params);
        }
        if (projectHolder.getChildCount() == 0)
            projectHolderTitle.setText("No Projects");
    }
}