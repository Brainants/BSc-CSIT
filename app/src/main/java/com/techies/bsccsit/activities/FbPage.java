package com.techies.bsccsit.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.ProfilePictureView;
import com.squareup.picasso.Picasso;
import com.techies.bsccsit.R;
import com.techies.bsccsit.adapters.FbPageAdapter;
import com.techies.bsccsit.advance.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

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


        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.pageCollapse);
        collapsingToolbarLayout.setTitle(getIntent().getStringExtra("name"));

        ProfilePictureView pageLogo = (ProfilePictureView) findViewById(R.id.page_logo);
        pageLogo.setProfileId(page_id);

        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setSubtitle(getIntent().getStringExtra("details"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        downloadFromInternet();
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
                            }catch (Exception e){
                                names.add(arrayItem.getJSONObject("from").getString("name"));
                                posterId.add(arrayItem.getJSONObject("from").getString("id"));

                                try {
                                    imageURL.add(arrayItem.getString("full_picture"));
                                }catch (Exception ex) {
                                    imageURL.add("");
                                }

                                try {
                                    messages.add(arrayItem.getString("message"));
                                }catch (Exception ex) {
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
