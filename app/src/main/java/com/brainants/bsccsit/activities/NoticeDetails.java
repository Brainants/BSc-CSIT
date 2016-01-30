package com.brainants.bsccsit.activities;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.brainants.bsccsit.R;
import com.brainants.bsccsit.advance.Singleton;

import mehdi.sakout.fancybuttons.FancyButton;

public class NoticeDetails extends AppCompatActivity {

    FancyButton download;
    private int noticeId;
    private String attachmentTitle,attachmentLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_details);

        TextView noticeTitle = (TextView) findViewById(R.id.noticeTitle);
        TextView noticeDate = (TextView) findViewById(R.id.noticeDate);
        WebView noticeContent = (WebView) findViewById(R.id.noticeContent);
        download = (FancyButton) findViewById(R.id.downloadAttachment);

        noticeId= getIntent().getIntExtra("notice_id",1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Notice");

        SQLiteDatabase database = Singleton.getInstance().getDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM notice WHERE id = '" + noticeId + "'",null);

        while(cursor.moveToNext()) {
            noticeTitle.setText(cursor.getString(cursor.getColumnIndex("title")));
            noticeDate.setText(cursor.getString(cursor.getColumnIndex("date")));

            attachmentTitle = cursor.getString(cursor.getColumnIndex("attachment_title"));
            attachmentLink = cursor.getString(cursor.getColumnIndex("attachment_link"));

        }


        noticeContent.loadDataWithBaseURL("", getIntent().getStringExtra("noticeDetail"), "text/html", "UTF-8", "");

        if (!attachmentTitle.equals("")) {
            download.setVisibility(View.VISIBLE);
            download.setText("Download attachment");
        }

        final String finalAttachmentLink = attachmentLink;
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(finalAttachmentLink));
                request.setTitle(attachmentTitle);
                request.setDescription("brainants.com");
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, attachmentTitle);

                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
                Snackbar.make(findViewById(R.id.coreNoticeDetails), "Download Started", Snackbar.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            super.onBackPressed();
        return true;
    }
}