package com.techies.bsccsit.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.techies.bsccsit.R;
import com.techies.bsccsit.activities.MainActivity;
import com.techies.bsccsit.adapters.ForumAdapter;
import com.techies.bsccsit.advance.Singleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class Forum extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private String semTag;
    private ArrayList<String> messagesEach = new ArrayList<>(),
            timeEach = new ArrayList<>(),
            namesEach = new ArrayList<>(),
            idsEach = new ArrayList<>(),
            postIdsEach = new ArrayList<>(),
            imageURLEach = new ArrayList<>();

    private ArrayList<String> messages = new ArrayList<>(),
            time = new ArrayList<>(),
            names = new ArrayList<>(),
            ids = new ArrayList<>(),
            postIds = new ArrayList<>(),
            imageURL = new ArrayList<>();

    private ArrayList<Integer> comments = new ArrayList<>(),
            likes = new ArrayList<>();

    private ArrayList<Integer> commentsEach = new ArrayList<>(),
            likesEach = new ArrayList<>();

    private LinearLayout errorMessage;
    private FloatingActionButton fab;
    private LoginManager manager;
    private CallbackManager callback;
    private View coreView;

    public Forum() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fab = MainActivity.fab;
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
                            if (!arrayItem.getJSONObject("from").getString("id").equals("764351733668743"))
                                try {
                                    try {
                                        messages.add(arrayItem.getString("message"));
                                    } catch (Exception e) {
                                        messages.add("");
                                    }
                                    try {
                                        imageURL.add(arrayItem.getString("full_picture"));
                                    } catch (Exception e) {
                                        imageURL.add("");
                                    }
                                    time.add(Singleton.convertToSimpleDate(arrayItem.getString("created_time")).toString());
                                    likes.add(arrayItem.getJSONObject("likes").getJSONObject("summary").getInt("total_count"));
                                    comments.add(arrayItem.getJSONObject("comments").getJSONObject("summary").getInt("total_count"));
                                    names.add(arrayItem.getJSONObject("from").getString("name"));
                                    ids.add(arrayItem.getJSONObject("from").getString("id"));
                                    postIds.add(arrayItem.getString("id"));
                                } catch (Exception ignored) {
                                }
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
        int sem = getActivity().getSharedPreferences("loginInfo", Context.MODE_PRIVATE).getInt("semester", 1);

        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).contains("#" + sem + "sem")) {
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

    private void sendPostRequestThroughGraph(String message) {
        final MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .content("Posting...")
                .progress(true, 0)
                .cancelable(false)
                .build();
        dialog.show();

        Bundle params = new Bundle();
        params.putString("message", message + "\n#" + Singleton.getSemester());
        final String requestId = "bsccsitapp/feed";
        new GraphRequest(AccessToken.getCurrentAccessToken(), requestId, params, HttpMethod.POST, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                dialog.dismiss();
                if (graphResponse.getError() != null) {
                    Snackbar.make(MainActivity.coordinatorLayout, "Unable to post.", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(MainActivity.coordinatorLayout, "Post successful.", Snackbar.LENGTH_SHORT).show();
                    getContext().getSharedPreferences("misc", Context.MODE_PRIVATE).edit().putString("message", "").apply();
                }
            }
        }).executeAsync();
    }

    private void askPostPermission() {
        manager = LoginManager.getInstance();
        manager.logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
        callback = CallbackManager.Factory.create();
        manager.registerCallback(callback, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                new MaterialDialog.Builder(getActivity())
                        .title("Write a post")
                        .input("Your message here.", getContext().getSharedPreferences("misc", Context.MODE_PRIVATE).getString("message", ""), false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                getContext().getSharedPreferences("misc", Context.MODE_PRIVATE).edit().putString("message", input.toString()).apply();
                                sendPostRequestThroughGraph(input.toString());
                            }
                        })
                        .positiveText("Post")
                        .negativeText("Cancel")
                        .build()
                        .show();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callback.onActivityResult(requestCode, resultCode, data);
    }


    private void fillRecy() {
        fab.show();
        progressBar.setVisibility(View.GONE);
        recyclerView.setAdapter(new ForumAdapter(getActivity(), namesEach, timeEach, idsEach, postIdsEach, messagesEach, imageURLEach, likesEach, commentsEach));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(Singleton.getSpanCount(getContext()), StaggeredGridLayoutManager.VERTICAL));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPostPermission();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.coreView = view;
        progressBar = (ProgressBar) view.findViewById(R.id.progressbarForum);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerForum);
        errorMessage = (LinearLayout) view.findViewById(R.id.errorMessageForum);
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
