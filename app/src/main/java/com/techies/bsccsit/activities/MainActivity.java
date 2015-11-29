package com.techies.bsccsit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.techies.bsccsit.R;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Login chaina vaye login activity ma lanchha
        if (!getSharedPreferences("loginInfo",MODE_PRIVATE).getBoolean("loggedIn",false)){
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }

    }
}


