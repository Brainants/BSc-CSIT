package com.techies.bsccsit.advance;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;

public class MyApp extends Application {
    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
