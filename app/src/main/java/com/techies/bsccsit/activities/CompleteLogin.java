package com.techies.bsccsit.activities;

import android.content.SharedPreferences;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.techies.bsccsit.R;
import com.techies.bsccsit.retrofit.MyApi;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class CompleteLogin extends AppCompatActivity {

    EditText name,email, phoneNo;
    ActionProcessButton loginBtn;
    AppCompatSpinner semester,college;
    private Bundle bundle;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_login);
        //view initilize gareko
        initializeView();

        bundle = getIntent().getExtras();

        editor = getSharedPreferences("loginInfo",MODE_PRIVATE).edit();

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
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://avaaj.com.np")  //call your base url
                .build();


        MyApi mylogin = restAdapter.create(MyApi.class); //this is how retrofit create your api
        mylogin.uploadUserData("1234115","Aawaz","3","1278","demo","demo",new Callback<String>() {

            @Override
            public void success(String s, retrofit.client.Response response) {

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                loginBtn.setErrorText("Failed...");
            }
        });
    }


    public void initializeView(){
        name= (EditText) findViewById(R.id.inputName);
        email= (EditText) findViewById(R.id.inputEmail);
        college= (AppCompatSpinner) findViewById(R.id.inputCollege);
        phoneNo= (EditText) findViewById(R.id.inputPhone);
        semester= (AppCompatSpinner) findViewById(R.id.inputSemester);
        loginBtn= (ActionProcessButton) findViewById(R.id.verifyLoginButton);
    }
}
