package com.techies.bsccsit.advance;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;

import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.techies.bsccsit.R;
import com.techies.bsccsit.activities.MainActivity;
import com.techies.bsccsit.networking.EventsDownloader;
import com.techies.bsccsit.networking.MyCommunitiesUploader;
import com.techies.bsccsit.networking.NewsDownloader;
import com.techies.bsccsit.networking.NoticeDownloader;
import com.techies.bsccsit.networking.NotificationDownloader;
import com.techies.bsccsit.networking.PopularCommunitiesDownloader;
import com.techies.bsccsit.networking.TagsDownloader;
import com.techies.bsccsit.networking.eLibraryDownloader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

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
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
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
        final int previousNews = Singleton.getNewsCount();
        NewsDownloader newsDownloader = new NewsDownloader();
        newsDownloader.execute();
        newsDownloader.setTaskCompleteListener(new NewsDownloader.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean success) {
                if (success && (Singleton.getNewsCount() - previousNews) > 0 && Singleton.canShowNotif("news"))
                    notification("New news arrived.", "You may like to read them.", "New news added."
                            , notifNews, new Intent(MyApp.getContext(), MainActivity.class), MyApp.getContext());
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
                if (success) {
                    if (Singleton.getNotifCount() > previousNews) {
                        Singleton.setNotificationStatus(true);
                        NotifyNewNotification(previousNotification);
                    }
                }
            }
        });

        final int previousCommunity = Singleton.getCommunityCount();
        PopularCommunitiesDownloader communityDownloader = new PopularCommunitiesDownloader();
        communityDownloader.doInBackground();
        communityDownloader.setTaskCompleteListener(new PopularCommunitiesDownloader.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean success) {
                if (success && (Singleton.getCommunityCount() - previousCommunity) > 0 && Singleton.canShowNotif("community"))
                    notification("New communities arrived.", "You may like to follow them.", "New Communities added."
                            , notifCommunity, new Intent(MyApp.getContext(), MainActivity.class), MyApp.getContext());
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
                if (success && Singleton.getElibraryCount() > previousLibrary && Singleton.canShowNotif("elibrary"))
                    notification("E-Library Updated", "New PDFs has been added. Check them out.", "E-Library Updated"
                            , notifLibrary, new Intent(MyApp.getContext(), MainActivity.class), MyApp.getContext());
            }
        });

        final int previousNotice = Singleton.noticeCount();
        NoticeDownloader noticeDownloader = new NoticeDownloader();
        newsDownloader.doInBackground();
        noticeDownloader.setOnTaskCompleteListener(new NoticeDownloader.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean success) {
                if (success && Singleton.canShowNotif("notice")) {
                    if ((Singleton.noticeCount() - previousNotice) == 1) {
                        // todo latest notice dekhaune notification();
                    } else
                        notification(Singleton.noticeCount() - previousNotice + " new TU Notices.", "New notices has been detected. Check them out.", "New notices avilable.",
                                notifNotice, new Intent(MyApp.getContext(), MainActivity.class), MyApp.getContext());
                }
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

    private void NotifyNewNotification(int previousNotification) {
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM notifications", null);
        for (int i = 0; i < previousNotification; i++) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(cursor.getString(cursor.getColumnIndex("link"))));
            notification(cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getString(cursor.getColumnIndex("desc")),
                    cursor.getString(cursor.getColumnIndex("title")),
                    new Random().nextInt(30 - 5) + 5,
                    intent,
                    this);
        }
        cursor.close();
    }
}