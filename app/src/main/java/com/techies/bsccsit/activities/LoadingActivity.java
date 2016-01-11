package com.techies.bsccsit.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.techies.bsccsit.R;
import com.techies.bsccsit.advance.BackgroundTaskHandler;
import com.techies.bsccsit.advance.Singleton;
import com.techies.bsccsit.fragments.NewsEvents;

public class LoadingActivity extends AppCompatActivity {
    ShimmerTextView loading;
    LinearLayout error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        loading= (ShimmerTextView) findViewById(R.id.loadingFirst);
        error= (LinearLayout) findViewById(R.id.errorFirst);

        Shimmer shimmer = new Shimmer();
        shimmer.start(loading);

        if (Singleton.getFollowingArray().size() - 1 == 0) {
            BackgroundTaskHandler.MyCommunitiesDownloader downloader = new BackgroundTaskHandler.MyCommunitiesDownloader();
            downloader.doInBackground();
            downloader.setTaskCompleteListener(new BackgroundTaskHandler.MyCommunitiesDownloader.OnTaskCompleted() {
                @Override
                public void onTaskCompleted(boolean success) {
                    if (success) {
                        startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                        finish();
                    } else {
                        error.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);
                    }
                }
            });
        }

    }
}
