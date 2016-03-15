package com.brainants.bsccsit.advance;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.brainants.bsccsit.R;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.Random;

public class MyIntentService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (!Singleton.canShowNotif(data.getString("tag", "UNKNOWN")))
            return;
        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(this);
        Uri sound = Uri.parse("android.resource://"
                + getPackageName() + "/" + R.raw.notification);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(data.getString("message").replace("{{name}}", Singleton.getName()));
        bigText.setBigContentTitle(data.getString("title").replace("{{name}}", Singleton.getName()));

        notificationCompat.setStyle(bigText);

        notificationCompat.setAutoCancel(true)
                .setTicker(data.getString("title").replace("{{name}}", Singleton.getName()))
                .setWhen(System.currentTimeMillis())
                .setContentTitle(data.getString("title").replace("{{name}}", Singleton.getName()))
                .setSmallIcon(R.drawable.logo_small)
                .setVibrate(new long[]{100, 100})
                .setLights(Color.BLUE, 3000, 3000)
                .setSound(sound)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentText(data.getString("message").replace("{{name}}", Singleton.getName()));

        if (Integer.parseInt(data.getString("show")) == 1) {
            ContentValues values = new ContentValues();
            values.put("title", data.getString("title").replace("{{name}}", Singleton.getName()));
            values.put("desc", data.getString("message").replace("{{name}}", Singleton.getName()));
            values.put("link", data.getString("link"));
            values.put("show", 1);
            Singleton.getInstance().getDatabase().insert("notifications", null, values);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(Intent.ACTION_VIEW, Uri.parse(data.getString("link"))), PendingIntent.FLAG_UPDATE_CURRENT);
        notificationCompat.setContentIntent(pendingIntent);
        NotificationManager notificationManagerCompat = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManagerCompat.notify(new Random(3000).nextInt(), notificationCompat.build());
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, getClass().getName());
        wl.acquire();
        try {
            Thread.sleep(7000);
        } catch (InterruptedException ignored) {
        }
        wl.release();
    }
}
