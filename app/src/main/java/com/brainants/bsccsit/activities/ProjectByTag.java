package com.brainants.bsccsit.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.brainants.bsccsit.R;
import com.brainants.bsccsit.adapters.ProjectAdapter;
import com.brainants.bsccsit.advance.Singleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProjectByTag extends AppCompatActivity {
    String tag;

    private ArrayList<String> titles = new ArrayList<>(),
            projectID = new ArrayList<>(),
            tags = new ArrayList<>(),
            user = new ArrayList<>(),
            detail = new ArrayList<>();


    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private LinearLayout errorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_page);
        tag = getIntent().getStringExtra("tag");

        progressBar = (ProgressBar) findViewById(R.id.progressbarFb);
        recyclerView = (RecyclerView) findViewById(R.id.recyFb);
        errorMsg = (LinearLayout) findViewById(R.id.errorMessageFb);
        errorMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFromInternet();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(tag);

        downloadFromInternet();
    }

    private void downloadFromInternet() {
        errorMsg.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        StringRequest request = new StringRequest(Request.Method.POST, getString(R.string.tagProjects), new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                try {
                    JSONObject object = new JSONObject(res);
                    if (object.getBoolean("error")) {
                        return;
                    }
                    JSONArray response = object.getJSONArray("data");
                    for (int i = 0; i < response.length(); i++) {
                        titles.add(response.getJSONObject(i).getString("title"));
                        detail.add(response.getJSONObject(i).getString("description"));
                        tags.add(response.getJSONObject(i).getString("tags"));
                        user.add(response.getJSONObject(i).getLong("user_id") + "");
                        projectID.add(response.getJSONObject(i).getString("id"));
                    }
                    fillRecy();
                } catch (Exception e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorMsg.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tag", tag);
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

    private void fillRecy() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        ProjectAdapter adapter = new ProjectAdapter(this, projectID, titles, user, tags, detail);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, Singleton.getSpanCount(this)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
