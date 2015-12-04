package com.techies.bsccsit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.techies.bsccsit.R;

import org.json.JSONException;

import mehdi.sakout.fancybuttons.FancyButton;

public class LoginActivity extends AppCompatActivity {

    FancyButton button;
    LoginButton loginButton;
    CallbackManager callbackManager;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        button= (FancyButton) findViewById(R.id.loginButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.callOnClick();
            }
        });

        loginButton=new LoginButton(this);

        loginButton.setReadPermissions("public_profile", "email");

        callbackManager = CallbackManager.Factory.create();

        //login button ko kaam
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                //saving data to shared pref
                editor = getSharedPreferences("loginInfo",MODE_PRIVATE).edit();
                editor.putString("FirstName", Profile.getCurrentProfile().getFirstName());
                editor.putString("LastName", Profile.getCurrentProfile().getLastName());
                editor.putString("FullName", Profile.getCurrentProfile().getName());
                editor.putString("UserID", Profile.getCurrentProfile().getId());
                editor.putBoolean("loggedIn", true);

                Bundle bundle= new Bundle();
                bundle.putString("fields", "email");
                //user ko email taneko
                new GraphRequest(AccessToken.getCurrentAccessToken(), "me",bundle, HttpMethod.GET, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            //email save garera complete login activity ma name ani email pass gareko
                            editor.putString("email", response.getJSONObject().getString("email"));
                            editor.apply();
                            startActivity(new Intent(LoginActivity.this,CompleteLogin.class)
                                    .putExtra("name",Profile.getCurrentProfile().getName())
                                    .putExtra("userID",Profile.getCurrentProfile().getId())
                                    .putExtra("email",response.getJSONObject().getString("email")));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "User aborted the login process.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(LoginActivity.this, "Something went wrong. Please try again." + e, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
