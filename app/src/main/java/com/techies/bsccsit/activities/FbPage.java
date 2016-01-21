package com.techies.bsccsit.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.techies.bsccsit.R;
import com.techies.bsccsit.adapters.FbPageAdapter;
import com.techies.bsccsit.advance.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FbPage extends AppCompatActivity {

    private ArrayList<String> messages = new ArrayList<>(),
            time = new ArrayList<>(),
            names = new ArrayList<>(),
            posterId = new ArrayList<>(),
            imageURL = new ArrayList<>();


    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private LinearLayout errorMsg;
    private String page_id;
    private FloatingActionButton followFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_page);

        progressBar = (ProgressBar) findViewById(R.id.progressbarFb);
        recyclerView = (RecyclerView) findViewById(R.id.recyFb);
        errorMsg = (LinearLayout) findViewById(R.id.errorMessageFb);
        errorMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFromInternet();
            }
        });

        page_id = getIntent().getStringExtra("id");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
        getSupportActionBar().setSubtitle(getIntent().getStringExtra("details"));

        downloadFromInternet();

        followFAB= (FloatingActionButton) findViewById(R.id.pageFollowFAB);

        if ( Singleton.checkExistInFollowing(page_id)){
            followFAB.setImageResource(R.drawable.cross);
            followFAB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.unfollowColor)));
        }else {
            followFAB.setImageResource(R.drawable.plus);
            followFAB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.colorPrimary)));
        }

        followFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Singleton.checkExistInFollowing(page_id)){
                    if (Singleton.getFollowingArray().size() <= 6) {
                        Snackbar.make(findViewById(R.id.fbPageCore), "You must follow at least 5 communities.", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    Singleton.getInstance().getDatabase().execSQL("DELETE FROM myCommunities WHERE FbID = "+page_id);
                    Snackbar.make(findViewById(R.id.fbPageCore),getIntent().getStringExtra("name")+" removed Successfully.",Snackbar.LENGTH_SHORT).show();
                }else {
                    ContentValues values=new ContentValues();
                    values.put("Title",getIntent().getStringExtra("name"));
                    values.put("FbID",page_id);
                    values.put("ExtraText",getIntent().getStringExtra("details"));
                    Singleton.getInstance().getDatabase().insert("myCommunities",null,values);
                    Snackbar.make(findViewById(R.id.fbPageCore),getIntent().getStringExtra("name")+" added Successfully.",Snackbar.LENGTH_SHORT).show();
                    getSharedPreferences("community", Context.MODE_PRIVATE).edit().putBoolean("changedComm",true).apply();
                }

                if ( Singleton.checkExistInFollowing(page_id)){
                    followFAB.setImageResource(R.drawable.cross);
                    followFAB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(FbPage.this,R.color.unfollowColor)));
                }else {
                    followFAB.setImageResource(R.drawable.plus);
                    followFAB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(FbPage.this,R.color.colorPrimary)));
                }
            }
        });
    }

    private void downloadFromInternet() {
        errorMsg.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        Bundle params = new Bundle();
        params.putString("fields", "message,story,full_picture,from,created_time");
        new GraphRequest(AccessToken.getCurrentAccessToken(), page_id + "/posts", params, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                try {
                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        progressBar.setVisibility(View.GONE);
                        errorMsg.setVisibility(View.VISIBLE);
                    } else {
                        messages.clear();
                        names.clear();
                        posterId.clear();
                        imageURL.clear();

                        JSONArray array = response.getJSONObject().getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject arrayItem = array.getJSONObject(i);
                            try {
                                arrayItem.getString("story");
                            } catch (Exception e) {
                                names.add(arrayItem.getJSONObject("from").getString("name"));
                                posterId.add(arrayItem.getJSONObject("from").getString("id"));

                                try {
                                    imageURL.add(arrayItem.getString("full_picture"));
                                } catch (Exception ex) {
                                    imageURL.add("");
                                }

                                try {
                                    messages.add(arrayItem.getString("message"));
                                } catch (Exception ex) {
                                    messages.add("");
                                }

                                time.add(Singleton.convertToSimpleDate(arrayItem.getString("created_time")).toString());

                            }
                        }
                        fillRecy();
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    errorMsg.setVisibility(View.VISIBLE);
                }
            }
        }).executeAsync();
    }

    private void fillRecy() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setAdapter(new FbPageAdapter(this, names, time, posterId, messages, imageURL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return true;
    }
}
