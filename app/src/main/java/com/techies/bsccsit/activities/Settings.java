package com.techies.bsccsit.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.techies.bsccsit.R;
import com.techies.bsccsit.advance.Singleton;

import java.util.HashMap;
import java.util.Map;

public class Settings extends AppCompatActivity implements AdapterView.OnItemClickListener {
    Button changeSem;
    private AppCompatSpinner semester;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //// TODO: 1/23/2016 dgerau cgae faba chha

        changeSem = (Button) findViewById(R.id.semButton);
        textView = (TextView) findViewById(R.id.semester);
        semester = (AppCompatSpinner) findViewById(R.id.semesterChange);

        final String[] semsters = {"Select your semester", "First Semester", "Second Semester", "Third Semester", "Fourth Semester",
                "Fifth Semester", "Sixth Semester", "Seventh Semester", "Eighth Semester"};
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semsters);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semester.setAdapter(a);

        semester.setLayoutParams(textView.getLayoutParams());

        changeSem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (semester.getSelectedItemPosition() > 0) {
                    changeSemester();

                } else {
                    Snackbar.make(findViewById(R.id.settingsCore), "Please select the semester!!", Snackbar.LENGTH_SHORT).show();
                }

            }
        });

    }


    private void changeSemester() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://bsccsit.brainants.com/updateusersemester", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getSharedPreferences("loginInfo", MODE_PRIVATE).edit().putInt("semester", semester.getSelectedItemPosition()).apply();

                Snackbar.make(findViewById(R.id.settingsCore), "Your semester updated!! Thank you!", Snackbar.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(findViewById(R.id.settingsCore), "Unable to update semester", Snackbar.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fbid", getSharedPreferences("loginInfo", Context.MODE_PRIVATE).getString("UserID", ""));
                params.put("semester", String.valueOf(semester.getSelectedItemPosition()));
                return params;
            }
        };
        Singleton.getInstance().getRequestQueue().add(stringRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
