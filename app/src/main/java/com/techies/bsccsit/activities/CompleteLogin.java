package com.techies.bsccsit.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.techies.bsccsit.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CompleteLogin extends AppCompatActivity {

    TextView name,email, phoneNo;
    ActionProcessButton loginBtn;
    AppCompatSpinner semester,college;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_login);
        //view initilize gareko
        initializeView();

        Bundle bundle = getIntent().getExtras();

        //semester list bhoreko
        final String[] semsters = {"Select your semester","First Semester","Second Semester","Third Semester","Fourth Semester",
                "Fifth Semester","Sixth Semester","Seventh Semester","Eighth Semester"};
        ArrayAdapter<String> a =new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, semsters);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semester.setAdapter(a);

        //college list vorekko
        final String[] colleges = getResources().getStringArray(R.array.collegeList);
        ArrayAdapter<String> b =new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, colleges);
        b.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        college.setAdapter(b);

        semester.setLayoutParams(name.getLayoutParams());
        college.setLayoutParams(name.getLayoutParams());

        //login activiy bata aako name edit text ma halera seal gareko
        if(bundle.getString("name")!=null) {
            name.setText(bundle.getString("name"));
            name.setEnabled(false);
        }

        //login activiy bata aako email edit text ma halera seal gareko
        if(bundle.getString("email")!=null) {
            email.setText(bundle.getString("email"));
            email.setEnabled(false);
        }

        //complete button le upload garcha upload method bata
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setMode(ActionProcessButton.Mode.ENDLESS);
                loginBtn.setProgress(1);
                uploadDataToServer();
            }
        });
    }

    private void uploadDataToServer() {
        //under construction coz php file ready chaina
        RequestQueue queue= Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest("https://avaj.com.np/bsccsit/", new JSONObject(getParams()), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, null);
        queue.add(request);
    }

    protected HashMap<String, String> getParams() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("token", "AbCdEfGh123456");
        return params;
    }

    public void initializeView(){
        name= (TextView) findViewById(R.id.inputName);
        email= (TextView) findViewById(R.id.inputEmail);
        college= (AppCompatSpinner) findViewById(R.id.inputCollege);
        phoneNo= (TextView) findViewById(R.id.inputPhone);
        semester= (AppCompatSpinner) findViewById(R.id.inputSemester);
        loginBtn= (ActionProcessButton) findViewById(R.id.verifyLoginButton);
    }
}
