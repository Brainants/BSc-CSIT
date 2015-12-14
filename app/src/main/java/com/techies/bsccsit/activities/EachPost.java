package com.techies.bsccsit.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.ProfilePictureView;
import com.squareup.picasso.Picasso;
import com.techies.bsccsit.R;
import com.techies.bsccsit.adapters.CommentsAdapter;
import com.techies.bsccsit.advance.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EachPost extends AppCompatActivity {

    private ArrayList<String> posterId=new ArrayList<>(), names=new ArrayList<>(),
            times=new ArrayList<>(), message=new ArrayList<>();
    private RecyclerView recyclerView;
    private View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_post);
        recyclerView = (RecyclerView) findViewById(R.id.eachPostRecy);
        readyHeader();
        fetchFromInternet();
    }

    private void readyHeader(){
        headerView=View.inflate(this,R.layout.news_each_post,null);
        TextView name= (TextView) headerView.findViewById(R.id.nameOfPoster);
        TextView time= (TextView) headerView.findViewById(R.id.timeOfPost);
        TextView message= (TextView) headerView.findViewById(R.id.messageOfPost);
        ImageView imageView= (ImageView) headerView.findViewById(R.id.imageOfPost);

        ProfilePictureView profilePictureView= (ProfilePictureView) headerView.findViewById(R.id.imageOfPoster);
        time.setText(Singleton.convertToSimpleDate(getIntent().getStringExtra("time")));
        name.setText(getIntent().getStringExtra("name"));
        message.setText(getIntent().getStringExtra("message"));
        profilePictureView.setProfileId(getIntent().getStringExtra("userId"));
        if (getIntent().getStringExtra("imageURL").equals(""))
            imageView.setVisibility(View.GONE);
        else
            Picasso.with(this).load(getIntent().getStringExtra("imageURL")).into(imageView);
    }

    private void fetchFromInternet() {
        Bundle params=new Bundle();
        new GraphRequest(AccessToken.getCurrentAccessToken(), getIntent().getStringExtra("postID")+"/comments", params, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if (response.getError()==null){
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
                        recyclerView.setLayoutManager(new LinearLayoutManager(EachPost.this));
                        recyclerView.setAdapter(new CommentsAdapter(EachPost.this,headerView,posterId,names,times,message));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).executeAsync();
    }
}
