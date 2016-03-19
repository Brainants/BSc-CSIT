package com.brainants.bsccsit.advance;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.liulishuo.filedownloader.FileDownloader;

import io.fabric.sdk.android.Fabric;

public class MyApp extends Application {
    private static Context mContext;
    public static int INTENT_SUCCESS = 100;

    public static Context getContext() {

        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(getBaseContext());
        FileDownloader.init(this);
        Fabric.with(this, new Crashlytics());
        mContext = getApplicationContext();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
