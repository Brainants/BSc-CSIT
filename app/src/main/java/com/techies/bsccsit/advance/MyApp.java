package com.techies.bsccsit.advance;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MyApp extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mContext = getApplicationContext();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }


    public static Context getContext() {
        return mContext;
    }
}
