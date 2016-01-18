package com.techies.bsccsit.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.techies.bsccsit.R;
import com.techies.bsccsit.adapters.CommentsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EachPost extends AppCompatActivity {

    private ArrayList<String> posterId=new ArrayList<>(), names=new ArrayList<>(),
            times=new ArrayList<>(), message=new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_post);

        setSupportActionBar((Toolbar) findViewById(R.id.idEachToolbar));
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Comments");

        recyclerView = (RecyclerView) findViewById(R.id.eachPostRecy);
        progressBar = (ProgressBar) findViewById(R.id.loadingEachPost);
        errorLayout = (LinearLayout) findViewById(R.id.errorMessageEachPost);
        errorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchFromInternet();
            }
        });

        fetchFromInternet();
    }

    private void fetchFromInternet() {
        errorLayout.setVisibility(View.GONE);
        Bundle params=new Bundle();
        new GraphRequest(AccessToken.getCurrentAccessToken(), getIntent().getStringExtra("postID")+"/comments", params, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if (response.getError() != null) {
                    errorLayout.setVisibility(View.VISIBLE);
                } else {
                    JSONObject object= response.getJSONObject();
                    try {
                        JSONArray array= object.getJSONArray("data");
                        for (int i=0;i<array.length();i++){
                            JSONObject eachObj= array.getJSONObject(i);
                            names.add(eachObj.getJSONObject("from").getString("name"));
                            posterId.add(eachObj.getJSONObject("from").getString("id"));
                            times.add(eachObj.getString("created_time"));
                            message.add(eachObj.getString("message"));
                        }
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setLayoutManager(new LinearLayoutManager(EachPost.this));
                        recyclerView.setAdapter(new CommentsAdapter(EachPost.this, getIntent().getExtras(), posterId, names, times, message));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).executeAsync();
    }
}
