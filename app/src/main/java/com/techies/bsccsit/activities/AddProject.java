package com.techies.bsccsit.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.devspark.robototextview.widget.RobotoTextView;
import com.techies.bsccsit.R;
import com.techies.bsccsit.advance.Singleton;
import com.techies.bsccsit.networking.TagsDownloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class AddProject extends AppCompatActivity implements TextWatcher {

    BootstrapEditText projectTitle, projectDesc, noOfUsers;
    LinearLayout tagHolder;
    HorizontalScrollView tagHolderScroll;
    RobotoTextView noTagMessage;
    FancyButton tagChooser;
    FloatingActionButton doneFab;

    Integer[] selectedPos = {};
    CharSequence[] selectedTxt = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        setSupportActionBar((Toolbar) findViewById(R.id.addProjectToolbar));

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Add Project");

        projectTitle = (BootstrapEditText) findViewById(R.id.projectTitle);
        projectDesc = (BootstrapEditText) findViewById(R.id.projectDetail);
        noOfUsers = (BootstrapEditText) findViewById(R.id.noOfRequiredUsers);
        tagHolder = (LinearLayout) findViewById(R.id.tagHolderAddProject);
        tagHolderScroll = (HorizontalScrollView) findViewById(R.id.horizontalViewProject);
        noTagMessage = (RobotoTextView) findViewById(R.id.noTagsText);
        tagChooser = (FancyButton) findViewById(R.id.tagChoser);
        doneFab = (FloatingActionButton) findViewById(R.id.projectDone);

        doneFab.hide();

        projectTitle.addTextChangedListener(this);
        projectDesc.addTextChangedListener(this);
        noOfUsers.addTextChangedListener(this);


        final String[] languages = getAllTags();

        tagChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (languages.length == 0) {
                    final MaterialDialog dialog = new MaterialDialog.Builder(AddProject.this)
                            .content("Fetching all tags...")
                            .progress(true, 0)
                            .build();
                    dialog.show();
                    TagsDownloader downloader = new TagsDownloader();
                    downloader.doInBackground();
                    downloader.setOnTaskCompleteListener(new TagsDownloader.ClickListener() {
                        @Override
                        public void onTaskComplete(boolean success) {
                            dialog.dismiss();
                            if (success)
                                tagChooser.callOnClick();
                            else
                                Snackbar.make(findViewById(R.id.addProjectCood), "Unable to fetch tags.", Snackbar.LENGTH_SHORT).setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        tagChooser.callOnClick();
                                    }
                                });

                        }
                    });
                } else {
                    new MaterialDialog.Builder(AddProject.this)
                            .title("Select your tag")
                            .items(languages)
                            .itemsCallbackMultiChoice(selectedPos, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    selectedPos = which.clone();
                                    selectedTxt = text.clone();
                                    if (which.length == 0) {
                                        noTagMessage.setVisibility(View.VISIBLE);
                                        tagHolderScroll.setVisibility(View.GONE);
                                    } else {
                                        noTagMessage.setVisibility(View.GONE);
                                        tagHolderScroll.setVisibility(View.VISIBLE);
                                    }

                                    LinearLayout.LayoutParams params = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    params.setMargins(2, 0, 2, 0);

                                    tagHolder.removeAllViews();

                                    for (int i = 0; i < text.length; i++)
                                        tagHolder.addView(Singleton.getTagView(AddProject.this, text[i].toString()), params);
                                    ValidateEveryThing();
                                    return true;
                                }
                            })
                            .positiveText("Done")
                            .negativeText("Cancel")
                            .show();
                }
            }
        });
        doneFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog dialog = new MaterialDialog.Builder(AddProject.this)
                        .content("Please wait...")
                        .progress(true, 0)
                        .build();
                dialog.show();
                StringRequest request = new StringRequest(Request.Method.POST, "http://bsccsit.brainants.com/addproject", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        if (response.toLowerCase().contains("success")) {
                            Snackbar.make(MainActivity.coordinatorLayout, "Project created successfully.", Snackbar.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Snackbar.make(findViewById(R.id.addProjectCood), response, Snackbar.LENGTH_SHORT).setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    doneFab.callOnClick();
                                }
                            }).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(findViewById(R.id.addProjectCood), "Unable to connect.", Snackbar.LENGTH_SHORT).setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                doneFab.callOnClick();
                            }
                        }).show();
                        dialog.dismiss();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("user_id", getSharedPreferences("loginInfo", MODE_PRIVATE).getString("UserID", ""));
                        params.put("title", projectTitle.getText().toString());
                        params.put("description", projectDesc.getText().toString());
                        params.put("required_users", String.valueOf(Integer.parseInt(noOfUsers.getText().toString()) == 0 ? 1 : Integer.parseInt(noOfUsers.getText().toString())));
                        params.put("tags", getTags());
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
        });
    }

    private void ValidateEveryThing() {
        if (projectTitle.getText().length() > 4 && projectDesc.getText().length() > 10 &&
                noOfUsers.getText().length() > 0 && selectedTxt.length > 0)
            doneFab.show();
        else
            doneFab.hide();

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        ValidateEveryThing();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public String getTags() {
        String tags = "";
        for (CharSequence aSelectedTxt : selectedTxt) {
            tags = tags + aSelectedTxt.toString() + ",";
        }
        return tags.substring(0, tags.length() - 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private String[] getAllTags() {

        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM tags", null);
        String[] tags = new String[cursor.getCount()];
        while (cursor.moveToNext()) {
            tags[cursor.getPosition()] = cursor.getString(cursor.getColumnIndex("tag_name"));
        }
        return tags;
    }
}

