package com.techies.bsccsit.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.techies.bsccsit.R;
import com.techies.bsccsit.advance.Singleton;

import java.util.HashMap;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class Settings extends AppCompatActivity {

    FancyButton changeSem, refreshElibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar((Toolbar) findViewById(R.id.settingToolbar));
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Settings");

        changeSem = (FancyButton) findViewById(R.id.changeSemester);
        refreshElibrary = (FancyButton) findViewById(R.id.emptyLibrary);
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
    }


    private void changeSemester(final int sem, final MaterialDialog choser) {
        final MaterialDialog progress = new MaterialDialog.Builder(this)
                .content("Please wait...")
                .progress(true, 0)
                .build();
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://bsccsit.brainants.com/updateusersemester", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.dismiss();
                choser.dismiss();
                getSharedPreferences("loginInfo", MODE_PRIVATE).edit().putInt("semester", sem).apply();
                Snackbar.make(findViewById(R.id.settingsCore), "Semester updated.", Snackbar.LENGTH_SHORT).show();
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
                params.put("fbid", getSharedPreferences("loginInfo", Context.MODE_PRIVATE).getString("UserID", ""));
                params.put("semester", sem + "");
                return params;
            }
        };
        Singleton.getInstance().getRequestQueue().add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
