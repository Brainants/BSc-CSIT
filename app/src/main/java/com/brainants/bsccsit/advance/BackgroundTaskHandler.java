package com.brainants.bsccsit.advance;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;

import com.brainants.bsccsit.R;
import com.brainants.bsccsit.networking.EventsDownloader;
import com.brainants.bsccsit.networking.GCMRegIdUploader;
import com.brainants.bsccsit.networking.MyCommunitiesUploader;
import com.brainants.bsccsit.networking.NewsDownloader;
import com.brainants.bsccsit.networking.NoticeDownloader;
import com.brainants.bsccsit.networking.NotificationDownloader;
import com.brainants.bsccsit.networking.PopularCommunitiesDownloader;
import com.brainants.bsccsit.networking.TagsDownloader;
import com.brainants.bsccsit.networking.eLibraryDownloader;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BackgroundTaskHandler extends GcmTaskService {

    private static int notifLibrary = 1;
    private static int notifNotice = 2;
    private static int notifCommunity = 3;
    private static int notifNews = 4;

    public static Date convertToSimpleDate(String created_time) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZZZZZ", Locale.US);
        try {
            return simpleDateFormat.parse(created_time);
        } catch (Exception e) {
            return null;
        }
    }

    public static void notification(String title, String content, String ticker, int notifyNumber, Intent intent1, Context context) {
        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(context);
        Uri sound = Uri.parse("android.resource://"
                + context.getPackageName() + "/" + R.raw.notification);
        notificationCompat.setAutoCancel(true)
                .setTicker(ticker)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate(new long[]{100, 100})
                .setLights(Color.BLUE, 3000, 3000)
                .setSound(sound)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
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
        new GCMRegIdUploader().doInBackground();

        final int previousNews = Singleton.getNewsCount();
        NewsDownloader newsDownloader = new NewsDownloader();
        if (previousNews != 0)
            newsDownloader.execute();
        newsDownloader.setTaskCompleteListener(new NewsDownloader.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean success) {
            }
        });

        EventsDownloader eventDownloader = new EventsDownloader();
        eventDownloader.execute();
        eventDownloader.setTaskCompleteListener(new EventsDownloader.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean success) {
                //// TODO: 1/23/2016 event naya aako notificarion
            }
        });

        final int previousNotification = Singleton.getNotifCount();
        NotificationDownloader NotifDownloader = new NotificationDownloader();
        NotifDownloader.doInBackground();
        NotifDownloader.setOnTaskCompleteListener(new NotificationDownloader.ClickListener() {
            @Override
            public void OnTaskCompleted(boolean success) {

            }
        });

        final int previousCommunity = Singleton.getCommunityCount();
        PopularCommunitiesDownloader communityDownloader = new PopularCommunitiesDownloader();
        communityDownloader.doInBackground();
        communityDownloader.setTaskCompleteListener(new PopularCommunitiesDownloader.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean success) {
            }
        });

        MyCommunitiesUploader myCommunitiesUploader = new MyCommunitiesUploader();
        if (getSharedPreferences("community", Context.MODE_PRIVATE).getBoolean("changedComm", false)) {
            myCommunitiesUploader.doInBackground();
            myCommunitiesUploader.setTaskCompleteListener(new MyCommunitiesUploader.OnTaskCompleted() {
                @Override
                public void onTaskCompleted(boolean success) {

                }
            });
        }

        final int previousLibrary = Singleton.eLibraryCount();
        eLibraryDownloader downloader = new eLibraryDownloader();
        downloader.doInBackground();
        downloader.setTaskCompleteListener(new eLibraryDownloader.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean success) {
            }
        });

        final int previousNotice = Singleton.noticeCount();
        NoticeDownloader noticeDownloader = new NoticeDownloader();
        newsDownloader.doInBackground();
        noticeDownloader.setOnTaskCompleteListener(new NoticeDownloader.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean success) {

            }
        });

        TagsDownloader tagsDownloader = new TagsDownloader();
        tagsDownloader.doInBackground();
        tagsDownloader.setOnTaskCompleteListener(new TagsDownloader.ClickListener() {
            @Override
            public void onTaskComplete(boolean success) {

            }
        });

        return 1;
    }
}