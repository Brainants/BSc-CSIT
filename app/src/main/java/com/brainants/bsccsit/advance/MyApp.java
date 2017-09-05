package com.brainants.bsccsit.advance;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.FacebookSdk;
import com.liulishuo.filedownloader.FileDownloader;

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
        mContext = getApplicationContext();
    }
}
