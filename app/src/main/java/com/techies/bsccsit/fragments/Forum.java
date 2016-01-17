package com.techies.bsccsit.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.techies.bsccsit.R;
import com.techies.bsccsit.activities.MainActivity;
import com.techies.bsccsit.adapters.ForumAdapter;
import com.techies.bsccsit.advance.Singleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Forum extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private String semTag;
    private ArrayList<String> messagesEach=new ArrayList<>(),
            timeEach=new ArrayList<>(),
            namesEach =new ArrayList<>(),
            idsEach=new ArrayList<>(),
            postIdsEach=new ArrayList<>(),
            imageURLEach=new ArrayList<>();

    private ArrayList<String> messages=new ArrayList<>(),
            time=new ArrayList<>(),
            names =new ArrayList<>(),
            ids=new ArrayList<>(),
            postIds=new ArrayList<>(),
            imageURL=new ArrayList<>();

    private ArrayList<Integer> comments=new ArrayList<>(),
            likes=new ArrayList<>();

    private ArrayList<Integer> commentsEach=new ArrayList<>(),
            likesEach=new ArrayList<>();

    private LinearLayout errorMessage;
    private FloatingActionButton fab;

    public Forum() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fab=MainActivity.fab;
        errorMessage.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        Bundle params = new Bundle();
        params.putString("fields", "message,story,full_picture,likes.limit(0).summary(true),comments.limit(0).summary(true),from,created_time");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "bsccsitapp/feed", params, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                try {
                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        progressBar.setVisibility(View.GONE);
                        errorMessage.setVisibility(View.VISIBLE);
                    } else {
                        messages.clear();
                        names.clear();
                        ids.clear();
                        likes.clear();
                        comments.clear();

                        JSONArray array = response.getJSONObject().getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject arrayItem = array.getJSONObject(i);
                            if(!arrayItem.getJSONObject("from").getString("id").equals("764351733668743"))
                                try {
                                    try {
                                        messages.add(arrayItem.getString("message"));
                                    }catch (Exception e){
                                        messages.add("");
                                    }
                                    try {
                                        imageURL.add(arrayItem.getString("full_picture"));
                                    }catch (Exception e){
                                        imageURL.add("");
                                    }
                                    time.add(Singleton.convertToSimpleDate(arrayItem.getString("created_time")).toString());
                                    likes.add(arrayItem.getJSONObject("likes").getJSONObject("summary").getInt("total_count"));
                                    comments.add(arrayItem.getJSONObject("comments").getJSONObject("summary").getInt("total_count"));
                                    names.add(arrayItem.getJSONObject("from").getString("name"));
                                    ids.add(arrayItem.getJSONObject("from").getString("id"));
                                    postIds.add(arrayItem.getString("id"));
                                } catch (Exception ignored) {}
                        }
                        forumSemesterWise();
                        fillRecy();
                    }

                } catch (Exception ignored) {
                    progressBar.setVisibility(View.GONE);
                    errorMessage.setVisibility(View.VISIBLE);
                }
            }
        }).executeAsync();
    }

    private void forumSemesterWise() {
        int sem = getActivity().getSharedPreferences("loginInfo",Context.MODE_PRIVATE).getInt("semester",1);

        for(int i=0;i<messages.size();i++) {
            if(messages.get(i).contains("#"+ sem + "sem" )) {
                messagesEach.add(messages.get(i));
                timeEach.add(time.get(i));
                namesEach.add(names.get(i));
                idsEach.add(ids.get(i));
                postIdsEach.add(postIds.get(i));
                imageURLEach.add(imageURL.get(i));
                likesEach.add(likes.get(i));
                commentsEach.add(comments.get(i));
            }
        }

    }

    private void fillRecy() {
        fab.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        recyclerView.setAdapter(new ForumAdapter(getActivity(), namesEach,timeEach,idsEach,postIdsEach,messagesEach,imageURLEach,likesEach,commentsEach));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar= (ProgressBar) view.findViewById(R.id.progressbarForum);
        recyclerView= (RecyclerView) view.findViewById(R.id.recyclerForum);
        errorMessage= (LinearLayout) view.findViewById(R.id.errorMessageForum);
        errorMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActivityCreated(null);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forum, container, false);
    }
}
