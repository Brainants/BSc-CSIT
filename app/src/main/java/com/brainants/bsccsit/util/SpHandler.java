package com.brainants.bsccsit.util;


import android.content.Context;
import android.content.SharedPreferences;

import com.brainants.bsccsit.advance.MyApp;
import com.google.firebase.auth.FirebaseAuth;

public class SpHandler {

    private static SpHandler handler;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public static SpHandler getInstance() {
        if (handler == null)
            handler = new SpHandler();
        return handler;
    }

    private SpHandler() {
        sp = MyApp.getContext().getSharedPreferences("information", Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public boolean isUserLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public boolean hasUserUpdatedLogin() {
        return sp.getBoolean("info_added", false);
    }

}
