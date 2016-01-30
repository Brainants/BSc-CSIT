package com.brainants.bsccsit.advance;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.facebook.FacebookSdk;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MyApp extends Application {
    private static Context mContext;

    public static Context getContext() {

        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mContext = getApplicationContext();
        FacebookSdk.sdkInitialize(getApplicationContext());
        justForTst();
    }
    private void justForTst() {
        BackgroundTaskHandler.notification("News","news","news",12,new Intent(Intent.ACTION_VIEW, Uri.parse("bsccsit://main/elbrary")),this);
        BackgroundTaskHandler.notification("note","news","news",13,new Intent(Intent.ACTION_VIEW, Uri.parse("bsccsit://main/note")),this);
        BackgroundTaskHandler.notification("event","news","news",14,new Intent(Intent.ACTION_VIEW, Uri.parse("bsccsit://main/event")),this);
        BackgroundTaskHandler.notification("project","news","news",16,new Intent(Intent.ACTION_VIEW, Uri.parse("bsccsit://main/projects")),this);
    }
}
