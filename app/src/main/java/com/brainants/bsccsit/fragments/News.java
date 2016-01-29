package com.brainants.bsccsit.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.brainants.bsccsit.R;
import com.brainants.bsccsit.activities.MainActivity;
import com.brainants.bsccsit.adapters.NewsAdapter;
import com.brainants.bsccsit.advance.Singleton;
import com.brainants.bsccsit.networking.NewsDownloader;

import java.util.ArrayList;

public class News extends Fragment {

    private ArrayList<String> names = new ArrayList<>(),
            posterId = new ArrayList<>(),
            fullImage = new ArrayList<>(),
            message = new ArrayList<>(),
            created_time = new ArrayList<>();

    private RecyclerView recyclerView;
    private ProgressBar progress;
    private LinearLayout error;
    private SwipeRefreshLayout swipeLayout;
    private View coreView;

    public News() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fillFromDatabase();
    }

    private void fillFromDatabase() {
        names.clear();
        posterId.clear();
        fullImage.clear();
        message.clear();
        created_time.clear();
        int count = 0;
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM news", null);
        while (cursor.moveToNext()) {
            count++;
            names.add(cursor.getString(cursor.getColumnIndex("names")));
            posterId.add(cursor.getString(cursor.getColumnIndex("posterId")));
            fullImage.add(cursor.getString(cursor.getColumnIndex("fullImage")));
            message.add(cursor.getString(cursor.getColumnIndex("message")));
            created_time.add(cursor.getString(cursor.getColumnIndex("created_time")));
        }
        cursor.close();
        if (count == 0) {
            progress.setVisibility(View.VISIBLE);
            downloadFromInternet(true);
        } else
            setToAdapter();
    }

    private void downloadFromInternet(final boolean first) {
        NewsDownloader downloader =
                new NewsDownloader();
        downloader.setTaskCompleteListener(new NewsDownloader.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean success) {
                if (!MainActivity.current.equals("Home"))
                    return;
                swipeLayout.setRefreshing(false);
                progress.setVisibility(View.GONE);

                if (success)
                    fillFromDatabase();
                else if (!first)
                    Snackbar.make(coreView, "Unable to update.", Snackbar.LENGTH_SHORT).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            downloadFromInternet(false);
                            swipeLayout.setRefreshing(true);
                        }
                    }).show();
                else
                    error.setVisibility(View.VISIBLE);
            }
        });
        downloader.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    private void setToAdapter() {
        progress.setVisibility(View.GONE);
        swipeLayout.setVisibility(View.VISIBLE);
        NewsAdapter adapter = new NewsAdapter(getActivity(), names, created_time, posterId, message, fullImage);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewNews);
        progress = (ProgressBar) view.findViewById(R.id.progressNews);
        error = (LinearLayout) view.findViewById(R.id.errorMessageNews);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeNews);
        swipeLayout.setVisibility(View.GONE);
        this.coreView = view;
        error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                downloadFromInternet(true);
            }
        });
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadFromInternet(false);
            }
        });
    }
}
