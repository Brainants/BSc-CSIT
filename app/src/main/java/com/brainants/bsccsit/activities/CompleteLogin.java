package com.brainants.bsccsit.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.brainants.bsccsit.R;
import com.brainants.bsccsit.fragments.BasicCommunityChooser;
import com.brainants.bsccsit.fragments.CompleteLoginForm;

public class CompleteLogin extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_login);
        SharedPreferences pref = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        FragmentManager fragmentManager=getSupportFragmentManager();
        if (pref.getBoolean("formFilled",false))
            fragmentManager.beginTransaction()
                    .replace(R.id.completeFragHolder,new BasicCommunityChooser()).commit();
        else
            fragmentManager.beginTransaction()
                    .replace(R.id.completeFragHolder,new CompleteLoginForm()).commit();

    }
}
