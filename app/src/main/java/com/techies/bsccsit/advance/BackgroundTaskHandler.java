package com.techies.bsccsit.advance;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;

import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.techies.bsccsit.R;
import com.techies.bsccsit.activities.FbEvent;
import com.techies.bsccsit.networking.EventsDownloader;
import com.techies.bsccsit.networking.MyCommunitiesUploader;
import com.techies.bsccsit.networking.NewsDownloader;
import com.techies.bsccsit.networking.PopularCommunitiesDownloader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BackgroundTaskHandler extends GcmTaskService {

    public static Date convertToSimpleDate(String created_time) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZZZZZ", Locale.US);
        try {
            return simpleDateFormat.parse(created_time);
        } catch (Exception e) {
            return null;
        }
    }

    public static void notification(String newsTitle, String content, String ticker, int notifyNumber, Intent intent1, Context context) {
        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(context);
        notificationCompat.setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(ticker)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(newsTitle)
                .setContentText(content);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationCompat.setContentIntent(pendingIntent);
        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManagerCompat.notify(notifyNumber, notificationCompat.build());
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, context.getClass().getName());
        wl.acquire();
        try {
            Thread.sleep(7000);
        } catch (InterruptedException ignored) {
        }
        wl.release();
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        NewsDownloader newsDownloader = new NewsDownloader();
        newsDownloader.execute();
        newsDownloader.setTaskCompleteListener(new NewsDownloader.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean success) {

            }
        });

        final int previous = Singleton.getEventNo();
        EventsDownloader eventDownloader = new EventsDownloader();
        eventDownloader.execute();
        eventDownloader.setTaskCompleteListener(new EventsDownloader.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean success) {
                if (success && previous < Singleton.getEventNo()) {
                    notification(Singleton.getLatestEventName(),
                            "Hosted by: " + Singleton.getLatestEventHost(),
                            "New event found!!!", 5,
                            new Intent(MyApp.getContext(), FbEvent.class)
                                    .putExtra("eventID", Singleton.getLatestEventId())
                                    .putExtra("eventName", Singleton.getLatestEventName())
                                    .putExtra("eventHost", "Hosted By: " + Singleton.getLatestEventHost()),
                            BackgroundTaskHandler.this);
                }
            }
        });

        PopularCommunitiesDownloader communityDownloader = new PopularCommunitiesDownloader();
        communityDownloader.doInBackground();
        communityDownloader.setTaskCompleteListener(new PopularCommunitiesDownloader.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean success) {

            }
        });

        MyCommunitiesUploader myCommunitiesUploader = new MyCommunitiesUploader();

        if (getSharedPreferences("community", Context.MODE_PRIVATE).getBoolean("changedComm", false))
            myCommunitiesUploader.doInBackground();
        return 1;
    }
}