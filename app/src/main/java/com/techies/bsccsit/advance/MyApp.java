package com.techies.bsccsit.advance;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;

public class MyApp extends Application {
    private static MyApp sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    public static MyApp getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

}
