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

    public static void changedFollowing(boolean changed){
                MyApp.getContext().getSharedPreferences("basic",MODE_PRIVATE).edit().putBoolean("changedCommunity",changed).apply();
    }
}
