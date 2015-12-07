package com.techies.bsccsit.advance;

import android.app.Application;

import com.facebook.FacebookSdk;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
