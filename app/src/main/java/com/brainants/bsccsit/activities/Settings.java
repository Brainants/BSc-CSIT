package com.brainants.bsccsit.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
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
import com.brainants.bsccsit.advance.MyApp;
import com.brainants.bsccsit.advance.Singleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class Settings extends AppCompatActivity {

    private SharedPreferences.Editor notifEditor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar((Toolbar) findViewById(R.id.settingToolbar));
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Settings");

        SharedPreferences notif = getSharedPreferences("notification", MODE_PRIVATE);
        notifEditor = notif.edit();

        FancyButton changeSem = (FancyButton) findViewById(R.id.changeSemester);
        FancyButton refreshElibrary = (FancyButton) findViewById(R.id.emptyLibrary);

        SwitchCompat news = (SwitchCompat) findViewById(R.id.newsSwitch);
        SwitchCompat event = (SwitchCompat) findViewById(R.id.eventSwitch);
        SwitchCompat elibrary = (SwitchCompat) findViewById(R.id.elibrarySwitch);
        SwitchCompat notice = (SwitchCompat) findViewById(R.id.noticeSwitch);
        SwitchCompat community = (SwitchCompat) findViewById(R.id.communitiesSwitch);

        news.setChecked(notif.getBoolean("news", true));
        elibrary.setChecked(notif.getBoolean("elibrary", true));
        event.setChecked(notif.getBoolean("event", true));
        notice.setChecked(notif.getBoolean("notice", true));
        community.setChecked(notif.getBoolean("community", true));

        news.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notifEditor.putBoolean("news", isChecked).apply();
            }
        });
        event.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notifEditor.putBoolean("event", isChecked).apply();
            }
        });
        elibrary.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notifEditor.putBoolean("elibrary", isChecked).apply();
            }
        });
        notice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notifEditor.putBoolean("notice", isChecked).apply();
            }
        });
        community.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notifEditor.putBoolean("community", isChecked).apply();
            }
        });

        final String[] semsters = {"First Semester", "Second Semester", "Third Semester", "Fourth Semester",
                "Fifth Semester", "Sixth Semester", "Seventh Semester", "Eighth Semester"};

        changeSem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(Settings.this)
                        .title("Choose your semester")
                        .items(semsters)
                        .itemsCallbackSingleChoice(getSharedPreferences("loginInfo", MODE_PRIVATE).getInt("semester", 1) - 1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                changeSemester(which + 1, dialog);
                                return true;
                            }
                        })
                        .positiveText("Change")
                        .negativeText("Cancel")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .autoDismiss(false)
                        .build()
                        .show();
            }
        });
        refreshElibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Environment.getExternalStorageDirectory() + "/" + Singleton.getSemester() + "/");
                deleteDirectory(file);
                Snackbar.make(findViewById(R.id.settingsCore), "E-Library refreshed.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void changeSemester(final int sem, final MaterialDialog choser) {
        final MaterialDialog progress = new MaterialDialog.Builder(this)
                .content("Please wait...")
                .progress(true, 0)
                .cancelable(false)
                .build();
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.updateSemester), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.dismiss();
                choser.dismiss();
                JSONObject object = null;
                try {
                    object = new JSONObject(response);
                    if (!object.getBoolean("error")) {
                        Singleton.getInstance().getDatabase().delete("eLibrary", null, null);
                        getSharedPreferences("loginInfo", MODE_PRIVATE).edit().putInt("semester", sem).apply();
                        Snackbar.make(findViewById(R.id.settingsCore), "Semester updated.", Snackbar.LENGTH_SHORT).show();
                        Singleton.getInstance().getDatabase().delete("eLibrary", null, null);
                        setResult(MyApp.INTENT_SUCCESS);
                    } else {
                        Snackbar.make(findViewById(R.id.settingsCore), "Something went wrong. Try again later.", Snackbar.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                Toast.makeText(Settings.this, "Unable to update semester", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", getSharedPreferences("loginInfo", Context.MODE_PRIVATE).getString("UserID", ""));
                params.put("semester", sem + "");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Singleton.getInstance().getRequestQueue().add(stringRequest);
    }

    public boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}