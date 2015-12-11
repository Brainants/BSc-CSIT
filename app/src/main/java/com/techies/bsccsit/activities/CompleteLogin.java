package com.techies.bsccsit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.facebook.FacebookSdk;
import com.techies.bsccsit.R;
import com.techies.bsccsit.advance.Singleton;

import java.util.HashMap;
import java.util.Map;

public class CompleteLogin extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TextWatcher {

    private EditText name,email, phoneNo;
    private FloatingActionButton loginBtn;
    private AppCompatSpinner semester,college;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_login);
        //view initilize gareko
        FacebookSdk.sdkInitialize(this);

        initializeView();

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

        semester.setOnItemSelectedListener(this);
        college.setOnItemSelectedListener(this);

        phoneNo.addTextChangedListener(this);
        SharedPreferences pref = getSharedPreferences("loginInfo", MODE_PRIVATE);

        //login activiy bata aako name edit text ma halera seal gareko
        if(!pref.getString("FullName","").equals("")) {
            name.setText(pref.getString("FullName",""));
            name.setEnabled(false);
        }

        //login activiy bata aako email edit text ma halera seal gareko
        if(!pref.getString("email","").equals("")) {
            email.setText(pref.getString("email",""));
            email.setEnabled(false);
        }

        //complete button le upload garcha upload method bata
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDataToServer();
            }
        });
    }

    private void uploadDataToServer() {
        final MaterialDialog dialog = new MaterialDialog.Builder(CompleteLogin.this)
                .content("Registering...")
                .progress(true,0)
                .cancelable(false)
                .build();
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, "https://slim-bloodskate.c9users.io/app/api/register", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("success")) {
                    dialog.dismiss();
                    Toast.makeText(CompleteLogin.this, "Welcome "+getSharedPreferences("loginInfo",MODE_PRIVATE).getString("FirstName","")+"!", Toast.LENGTH_SHORT).show();
                    editor.putInt("semester",semester.getSelectedItemPosition());
                    editor.putString("phone_number",phoneNo.getText().toString());
                    editor.putString("college",college.getSelectedItem().toString());
                    editor.putBoolean("loggedIn",true);
                    editor.apply();
                    Answers.getInstance().logLogin(new LoginEvent()
                            .putMethod("Facebook")
                            .putSuccess(true));
                    startActivity(new Intent(CompleteLogin.this, MainActivity.class));
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Answers.getInstance().logLogin(new LoginEvent()
                        .putMethod("Facebook")
                        .putSuccess(false));
                Snackbar.make(findViewById(R.id.CompleteCore),"Unable to connect.",Snackbar.LENGTH_SHORT).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uploadDataToServer();
                    }
                }).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name.getText().toString());
                params.put("fbid", getSharedPreferences("loginInfo",MODE_PRIVATE).getString("UserID",""));
                params.put("semester", semester.getSelectedItemPosition()+"");
                params.put("phone_number", phoneNo.getText().toString());
                params.put("college", college.getSelectedItem().toString());
                params.put("email", email.getText().toString());
                params.put("gender", getSharedPreferences("loginInfo",MODE_PRIVATE).getString("Gender",""));
                params.put("location", getSharedPreferences("loginInfo",MODE_PRIVATE).getString("HomeTown",""));
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


    private void verifyText(){
        if (phoneNo.getText().toString().length()==10 &&
                semester.getSelectedItemPosition()>0 &&
                college.getSelectedItemPosition()>0 &&
                email.getText().toString().contains("@")&&
                email.getText().toString().contains(".")&&
                !email.getText().toString().contains(" "))
            loginBtn.show();
        else
            loginBtn.hide();
    }


    public void initializeView(){
        name= (EditText) findViewById(R.id.inputName);
        email= (EditText) findViewById(R.id.inputEmail);
        college= (AppCompatSpinner) findViewById(R.id.inputCollege);
        phoneNo= (EditText) findViewById(R.id.inputPhone);
        semester= (AppCompatSpinner) findViewById(R.id.inputSemester);
        loginBtn= (FloatingActionButton) findViewById(R.id.continueButton);
        loginBtn.show();
        loginBtn.hide();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        verifyText();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        verifyText();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
