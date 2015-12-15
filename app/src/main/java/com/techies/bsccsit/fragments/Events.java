package com.techies.bsccsit.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
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

import com.techies.bsccsit.R;
import com.techies.bsccsit.adapters.EventAdapter;
import com.techies.bsccsit.advance.BackgroundTaskHandler;
import com.techies.bsccsit.advance.Singleton;

import java.util.ArrayList;
import java.util.Date;

public class Events extends Fragment {

    ArrayList<String> names=new ArrayList<>(),
            created_time=new ArrayList<>(),
            eventIDs=new ArrayList<>(),
            hosters=new ArrayList<>(),
            fullImage=new ArrayList<>();

    private RecyclerView recyclerView;
    private ProgressBar progress;
    private LinearLayout error;
    private SwipeRefreshLayout swipeLayout;
    private View view;
    private int noOfUpcoming;
    private Date current;

    public Events() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewEvent);
        progress= (ProgressBar) view.findViewById(R.id.progressEvent);
        error= (LinearLayout) view.findViewById(R.id.errorMessageEvent);
        swipeLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipeEvents);
        swipeLayout.setVisibility(View.GONE);
        this.view=view;
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

    @Override
    public void onResume() {
        super.onResume();
    }

    private void fillFromDatabase() {
        int count=0;
        noOfUpcoming=0;
        names.clear();
        eventIDs.clear();
        fullImage.clear();
        hosters.clear();
        created_time.clear();
        Cursor cursor= Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM events",null);
        while(cursor.moveToNext()){
            count++;
            names.add(cursor.getString(cursor.getColumnIndex("names")));
            eventIDs.add(cursor.getString(cursor.getColumnIndex("eventIDs")));
            fullImage.add(cursor.getString(cursor.getColumnIndex("fullImage")));
            hosters.add(cursor.getString(cursor.getColumnIndex("hosters")));
            created_time.add(cursor.getString(cursor.getColumnIndex("created_time")));
            if (BackgroundTaskHandler.convertToSimpleDate(cursor.getString(cursor.getColumnIndex("created_time")))
                    .compareTo(current)>0)
                noOfUpcoming++;
        }
        cursor.close();
        if(count==0) {
            progress.setVisibility(View.VISIBLE);
            downloadFromInternet(true);
        }else
            setToAdapter();
    }

    private void downloadFromInternet(final boolean first) {
        BackgroundTaskHandler.EventsDownloader downloader =
                new BackgroundTaskHandler.EventsDownloader();
        downloader.setTaskCompleteListener(new BackgroundTaskHandler.EventsDownloader.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean success) {
                progress.setVisibility(View.GONE);
                swipeLayout.setRefreshing(false);
                if(success)
                    fillFromDatabase();
                else if (first)
                    error.setVisibility(View.VISIBLE);
                else
                    Snackbar.make(view,"Unable to update.",Snackbar.LENGTH_SHORT).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            swipeLayout.setRefreshing(true);
                            downloadFromInternet(false);
                        }
                    }).show();

            }
        });
        downloader.execute();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        current = new Date();
        current.setTime(System.currentTimeMillis());
        fillFromDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    private void setToAdapter() {
        progress.setVisibility(View.GONE);
        swipeLayout.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(new EventAdapter(getActivity(),noOfUpcoming,names,eventIDs,created_time,hosters,fullImage));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
