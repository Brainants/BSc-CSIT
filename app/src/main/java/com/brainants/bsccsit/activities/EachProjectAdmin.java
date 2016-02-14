package com.brainants.bsccsit.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.brainants.bsccsit.R;
import com.brainants.bsccsit.advance.Singleton;
import com.brainants.bsccsit.networking.TagsDownloader;
import com.devspark.robototextview.widget.RobotoTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

public class EachProjectAdmin extends AppCompatActivity {
    RobotoTextView title, detail, seekingCount, membersHeader, requestsHeader;
    LinearLayout tagHolder, userHolder, errorLayout, requestHolder, tagChanger;
    NestedScrollView mScrollView;
    ProgressBar loading;
    String project_id;
    int seekingCountInt;
    HorizontalScrollView horizontalScrollViewUser, horizontalScrollViewRequest;
    private boolean isChanged = false;
    private Integer[] selectedPos;
    private CharSequence[] selectedTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_project_admin);
        setSupportActionBar((Toolbar) findViewById(R.id.eachProjectAdminToolbar));

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        title = (RobotoTextView) findViewById(R.id.eachProjectTitle);
        detail = (RobotoTextView) findViewById(R.id.eachProjectDetail);
        tagHolder = (LinearLayout) findViewById(R.id.tagHolderEachProject);
        userHolder = (LinearLayout) findViewById(R.id.usersHolderProject);
        requestsHeader = (RobotoTextView) findViewById(R.id.requestHeader);
        requestHolder = (LinearLayout) findViewById(R.id.requestHolderProject);
        horizontalScrollViewRequest = (HorizontalScrollView) findViewById(R.id.horizontalRequest);
        errorLayout = (LinearLayout) findViewById(R.id.errorMessageEachProject);
        mScrollView = (NestedScrollView) findViewById(R.id.nestedScrollProject);
        loading = (ProgressBar) findViewById(R.id.progressbarEachProject);
        seekingCount = (RobotoTextView) findViewById(R.id.seekingCount);
        membersHeader = (RobotoTextView) findViewById(R.id.membersHeader);
        tagChanger = (LinearLayout) findViewById(R.id.tagChanger);
        horizontalScrollViewUser = (HorizontalScrollView) findViewById(R.id.horizontalUser);
        errorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFromInternet();
            }
        });

        try {
            project_id = getIntent().getStringExtra("project_id");
        }catch (Exception e){
            project_id=getIntent().getData().getQueryParameter("project_id");
        }
        loadFromInternet();
    }

    private void loadFromInternet() {
        mScrollView.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        horizontalScrollViewUser.setVisibility(View.VISIBLE);
        horizontalScrollViewRequest.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(Request.Method.POST, "http://bsccsit.brainants.com/getproject", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    mScrollView.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);
                    JSONObject coreObject = new JSONObject(response);
                    title.setText(coreObject.getString("title"));
                    setTitle(coreObject.getString("title"));
                    detail.setText(coreObject.getString("description"));
                    fillTags(coreObject.getString("tags"));
                    selectedTxt = coreObject.getString("tags").split(",");
                    selectedPos = getPositions(selectedTxt);
                    fillUsers(coreObject.getJSONArray("members"));
                    fillRequest(coreObject.getJSONArray("requests"));
                    seekingCountInt = coreObject.getInt("required_users") - coreObject.getInt("num_users");
                    if ((coreObject.getInt("required_users") - coreObject.getInt("num_users")) != 0)
                        seekingCount.setText("Seeking for " + (coreObject.getInt("required_users") - coreObject.getInt("num_users")) + " members.");
                    else {
                        seekingCount.setText("Team is full.");
                    }
                    handleEdit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mScrollView.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("project_id", project_id);
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

    private Integer[] getPositions(CharSequence[] selectedTxt) {
        Integer[] selectedPos = new Integer[selectedTxt.length];
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM tags", null);
        int i = 0;
        for (int j = 0; j < selectedTxt.length; j++) {
            while (cursor.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndex("tag_name")).equals(selectedTxt[j])) {
                    selectedPos[i] = cursor.getPosition();
                    i++;
                }
            }
        }

        return new Integer[0];
    }

    private void handleEdit() {
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(EachProjectAdmin.this)
                        .title("Change name")
                        .input("Project Name", title.getText().toString(), false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                title.setText(input);
                                isChanged = true;
                                invalidateOptionsMenu();
                            }
                        })
                        .positiveText("Change")
                        .negativeText("Cancel")
                        .show();
            }
        });

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(EachProjectAdmin.this)
                        .title("Change detail")
                        .inputRange(10, 100)
                        .input("Project Detail", detail.getText().toString(), false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                detail.setText(input);
                                isChanged = true;
                                invalidateOptionsMenu();
                            }
                        })
                        .positiveText("Change")
                        .negativeText("Cancel")
                        .show();
            }
        });

        seekingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(EachProjectAdmin.this)
                        .title("Seeking for")
                        .inputRange(1, 1)
                        .inputType(InputType.TYPE_NUMBER_FLAG_SIGNED)
                        .input("No of users", String.valueOf(seekingCountInt), false, new MaterialDialog.InputCallback() {

                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                seekingCountInt = Integer.parseInt(input.toString());
                                if (Integer.parseInt(input.toString()) == 0)
                                    seekingCount.setText("Team is full");
                                else
                                    seekingCount.setText("Seeking for " + input + " new users");
                                isChanged = true;
                                invalidateOptionsMenu();
                            }
                        })
                        .positiveText("Change")
                        .negativeText("Cancel")
                        .show();
            }
        });

        tagChanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] languages = AddProject.getAllTags();
                if (languages.length == 0) {
                    final MaterialDialog dialog = new MaterialDialog.Builder(EachProjectAdmin.this)
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
                                tagChanger.callOnClick();
                            else
                                Snackbar.make(findViewById(R.id.addProjectCood), "Unable to fetch tags.", Snackbar.LENGTH_SHORT).setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        tagChanger.callOnClick();
                                    }
                                });

                        }
                    });
                } else {
                    selectedPos = getPositions(selectedTxt);
                    new MaterialDialog.Builder(EachProjectAdmin.this)
                            .title("Select your tag")
                            .items(languages)
                            .itemsCallbackMultiChoice(selectedPos, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    selectedPos = which.clone();
                                    selectedTxt = text.clone();
                                    fillTags(selectedTxt);
                                    isChanged = true;
                                    invalidateOptionsMenu();
                                    return true;
                                }
                            })
                            .positiveText("Done")
                            .negativeText("Cancel")
                            .show();
                }
            }
        });
    }

    private void fillRequest(final JSONArray requests) throws Exception {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 4, 0);
        requestHolder.removeAllViews();
        for (int i = 0; i < requests.length(); i++) {
            final RelativeLayout eachUser = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.user_widget, null);

            RobotoTextView name = (RobotoTextView) eachUser.findViewById(R.id.name);
            CircleImageView userView = (CircleImageView) eachUser.findViewById(R.id.image);
            name.setText(requests.getJSONObject(i).getString("name"));
            Picasso.with(this).load("https://graph.facebook.com/" + requests.getJSONObject(i).getString("user_id") + "/picture?type=large").into(userView);
            final int finalI = i;
            eachUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        new MaterialDialog.Builder(EachProjectAdmin.this)
                                .title(requests.getJSONObject(finalI).getString("name"))
                                .items(new String[]{"View Profile", "Accept Request", "Reject Request"})
                                .itemsCallback(new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                        try {
                                            if (which == 0) {
                                                startActivity(new Intent(EachProjectAdmin.this, UserProfile.class).putExtra("userID", requests.getJSONObject(finalI).getString("user_id")));
                                                dialog.dismiss();
                                            } else if (which == 1)
                                                acceptRequest(requests.getJSONObject(finalI), dialog);
                                            else
                                                cancelRequest(requests.getJSONObject(finalI), dialog);
                                        } catch (Exception e) {
                                        }
                                    }
                                })
                                .show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            requestHolder.addView(eachUser, params);
        }
        if (requests.length() == 0) {
            requestsHeader.setText("No requests");
            horizontalScrollViewRequest.setVisibility(View.GONE);
        }
    }

    private void cancelRequest(final JSONObject jsonObject, final MaterialDialog dialogPrevious) {
        final MaterialDialog dialog = new MaterialDialog.Builder(EachProjectAdmin.this)
                .content("Please wait...")
                .progress(true, 0)
                .build();
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, "http://bsccsit.brainants.com/cancelrequest", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                dialogPrevious.dismiss();
                if (response.toLowerCase().contains("true")) {
                    loadFromInternet();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(EachProjectAdmin.this, "Unable to connect.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                try {
                    params.put("project_id", String.valueOf(jsonObject.getInt("project_id")));
                    params.put("user_id", String.valueOf(jsonObject.getInt("user_id_id")));
                } catch (JSONException e) {
                }
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

    private void acceptRequest(final JSONObject jsonObject, final MaterialDialog dialogPrevious) throws Exception {
        final MaterialDialog dialog = new MaterialDialog.Builder(EachProjectAdmin.this)
                .content("Please wait...")
                .progress(true, 0)
                .build();
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, "http://bsccsit.brainants.com/accept", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                dialogPrevious.dismiss();
                if (response.toLowerCase().contains("success")) {
                    loadFromInternet();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(EachProjectAdmin.this, "Unable to connect.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                try {
                    params.put("id", String.valueOf(jsonObject.getInt("id")));
                } catch (JSONException e) {
                }
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

    private void fillUsers(final JSONArray users) throws Exception {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(4, 0, 4, 0);
        userHolder.removeAllViews();
        for (int i = 0; i < users.length(); i++) {
            RelativeLayout eachUser = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.user_widget, null);

            RobotoTextView name = (RobotoTextView) eachUser.findViewById(R.id.name);
            CircleImageView userView = (CircleImageView) eachUser.findViewById(R.id.image);
            name.setText(users.getJSONObject(i).getString("name"));
            Picasso.with(this).load("https://graph.facebook.com/" + users.getJSONObject(i).getString("id") + "/picture?type=large").into(userView);
            final int finalI = i;
            eachUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        startActivity(new Intent(EachProjectAdmin.this, UserProfile.class).putExtra("userID", users.getJSONObject(finalI).getString("id")));
                    } catch (JSONException e) {
                    }
                }
            });
            userHolder.addView(eachUser, params);
        }
        if (users.length() == 0) {
            membersHeader.setText("No members");
            horizontalScrollViewUser.setVisibility(View.GONE);
        }
    }

    private void fillTags(String tags) {
        String[] tag = tags.split(",");
        tagHolder.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (final String eachTagString : tag) {
            FancyButton eachTag = (FancyButton) LayoutInflater.from(this).inflate(R.layout.tag_widget, null);
            params.setMargins(4, 0, 4, 0);
            eachTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(EachProjectAdmin.this, ProjectByTag.class)
                            .putExtra("tag", eachTagString));
                }
            });
            eachTag.setText(eachTagString);
            tagHolder.addView(eachTag, params);
        }
    }

    private void fillTags(CharSequence[] tag) {
        tagHolder.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (CharSequence eachTagString : tag) {
            FancyButton eachTag = (FancyButton) LayoutInflater.from(this).inflate(R.layout.tag_widget, null);
            params.setMargins(4, 0, 4, 0);
            eachTag.setText(eachTagString.toString());
            tagHolder.addView(eachTag, params);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        else if (item.getItemId() == R.id.saveChanges)
            uploadChanges(false);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_project, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isChanged)
            menu.findItem(R.id.saveChanges).setVisible(true);
        else
            menu.findItem(R.id.saveChanges).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (isChanged) {
            new MaterialDialog.Builder(this)
                    .title("Discard chnages?")
                    .content("You have some unsaved changes. Would you like to save it?")
                    .positiveText("Save")
                    .negativeText("Discard")
                    .negativeColor(ContextCompat.getColor(this, R.color.unfollowColor))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            uploadChanges(true);
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            finish();
                        }
                    })
                    .show();
        } else
            super.onBackPressed();
    }

    private void uploadChanges(final boolean shouldFinish) {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content("Updating...")
                .progress(true,0)
                .cancelable(false)
                .build();
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,"http://bsccsit.brainants.com/updateproject", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                if (shouldFinish) {
                    finish();
                    Toast.makeText(EachProjectAdmin.this, "Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(findViewById(R.id.projectEachCoordinator), "Updated.", Snackbar.LENGTH_SHORT).show();
                    isChanged = false;
                    invalidateOptionsMenu();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(findViewById(R.id.projectEachCoordinator), "Unable to update.", Snackbar.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", project_id);
                params.put("title", title.getText().toString());
                params.put("required_users", userHolder.getChildCount() + seekingCountInt + "");
                params.put("description", detail.getText().toString());
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
        Singleton.getInstance().getRequestQueue().add(request);
    }

    public String getTags() {
        String tags = "";
        for (CharSequence aSelectedTxt : selectedTxt) {
            tags = tags + aSelectedTxt.toString() + ",";
        }
        return tags.substring(0, tags.length() - 1);
    }
}
