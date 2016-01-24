package com.techies.bsccsit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.techies.bsccsit.R;
import com.techies.bsccsit.adapters.CommentsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class EachPost extends AppCompatActivity {

    private ArrayList<String> posterId = new ArrayList<>(), names = new ArrayList<>(),
            times = new ArrayList<>(), message = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private FloatingActionButton fab;
    private View headerView;
    private LoginManager manager;
    private CallbackManager callback;

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
        fab = (FloatingActionButton) findViewById(R.id.fabEachPost);
        fab.hide();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getSharedPreferences("misc", Context.MODE_PRIVATE).getBoolean("publishPermission", false)) {
                    new MaterialDialog.Builder(EachPost.this)
                            .title("Allow app to post.")
                            .content("Our fourm uses facebook so you must allow us to post on facebook.")
                            .positiveText("Proceed...")
                            .negativeText("I won't use forum")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    askPostPermission();
                                }
                            })
                            .show();
                } else {
                    new MaterialDialog.Builder(EachPost.this)
                            .title("Add a comment...")
                            .input("Comment here.", "", false, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(@NonNull final MaterialDialog dialog, CharSequence input) {
                                    final MaterialDialog materialDialog = new MaterialDialog.Builder(EachPost.this)
                                            .progress(true, 0)
                                            .content("Posting...")
                                            .build();
                                    materialDialog.show();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("message", input.toString());
                                    new GraphRequest(AccessToken.getCurrentAccessToken(), getIntent().getStringExtra("postID") + "/comments", bundle, HttpMethod.POST, new GraphRequest.Callback() {
                                        @Override
                                        public void onCompleted(GraphResponse response) {
                                            materialDialog.dismiss();
                                            if (response.getError() != null) {
                                                Toast.makeText(EachPost.this, "Unable to comment.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                dialog.dismiss();
                                                Snackbar.make(findViewById(R.id.coreEachPost), "Commented Successfully", Snackbar.LENGTH_SHORT).show();
                                                fetchFromInternet();
                                            }
                                        }
                                    }).executeAsync();
                                }
                            })
                            .positiveText("Comment")
                            .autoDismiss(false)
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .negativeText("Cancel")
                            .show();
                }

            }
        });
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
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        names.clear();
        posterId.clear();
        times.clear();
        message.clear();
        Bundle params = new Bundle();
        new GraphRequest(AccessToken.getCurrentAccessToken(), getIntent().getStringExtra("postID") + "/comments", params, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if (response.getError() != null) {
                    errorLayout.setVisibility(View.VISIBLE);
                } else {
                    JSONObject object = response.getJSONObject();
                    try {
                        JSONArray array = object.getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject eachObj = array.getJSONObject(i);
                            names.add(eachObj.getJSONObject("from").getString("name"));
                            posterId.add(eachObj.getJSONObject("from").getString("id"));
                            times.add(eachObj.getString("created_time"));
                            message.add(eachObj.getString("message"));
                        }
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        fab.show();
                        recyclerView.setLayoutManager(new LinearLayoutManager(EachPost.this));
                        recyclerView.setAdapter(new CommentsAdapter(EachPost.this, getIntent().getExtras(), posterId, names, times, message));
                    } catch (JSONException e) {
                    }
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
                getSharedPreferences("misc", Context.MODE_PRIVATE).edit().putBoolean("publishPermission", true).apply();
                fab.callOnClick();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callback.onActivityResult(requestCode, resultCode, data);
    }

}

