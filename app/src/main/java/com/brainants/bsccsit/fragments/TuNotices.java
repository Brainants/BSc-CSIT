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
import com.brainants.bsccsit.adapters.NoticeAdapter;
import com.brainants.bsccsit.advance.Singleton;
import com.brainants.bsccsit.networking.NoticeDownloader;

import java.util.ArrayList;

public class TuNotices extends Fragment {

    private LinearLayout errorMsg;
    private View coreNotice;
    private SwipeRefreshLayout swipeNotice;
    private RecyclerView recyclerViewNotice;
    private ProgressBar progressBar;

    private ArrayList<String> mTitles = new ArrayList<>(),
            mShorts = new ArrayList<>(),
            mDetails = new ArrayList<>(),
            mDates = new ArrayList<>(),
            mAttachmentLinks = new ArrayList<>(),
            mAttachmentTitles = new ArrayList<>();

    private ArrayList<Integer> mIds = new ArrayList<>();


    public TuNotices() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadFromDb();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        errorMsg = (LinearLayout) view.findViewById(R.id.errorMessageNotice);
        this.coreNotice = view;
        swipeNotice = (SwipeRefreshLayout) view.findViewById(R.id.swipeNotice);
        recyclerViewNotice = (RecyclerView) view.findViewById(R.id.recyNotice);
        progressBar = (ProgressBar) view.findViewById(R.id.progressNotice);
        errorMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorMsg.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                fetchNoticesFromInternet(true);
            }
        });
        swipeNotice.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNoticesFromInternet(false);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tu_notices, container, false);
    }

    private void fetchNoticesFromInternet(final boolean isFirst) {
        NoticeDownloader downloader = new NoticeDownloader();
        downloader.doInBackground();
        downloader.setOnTaskCompleteListener(new NoticeDownloader.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean success) {
                if (!MainActivity.current.equals("TU Notices"))
                    return;
                swipeNotice.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                if (success)
                    loadFromDb();
                else if (!isFirst)
                    Snackbar.make(coreNotice, "Unable to update.", Snackbar.LENGTH_SHORT).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fetchNoticesFromInternet(false);
                            swipeNotice.setRefreshing(true);
                        }
                    }).show();

                else
                    errorMsg.setVisibility(View.VISIBLE);
            }
        });

    }


    private void loadFromDb() {
        mTitles.clear();
        mIds.clear();
        mDates.clear();
        mShorts.clear();
        mDetails.clear();
        mAttachmentTitles.clear();
        mAttachmentLinks.clear();
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM notices", null);
        int i = 0;
        while (cursor.moveToNext()) {
            i++;
            mIds.add(cursor.getInt(cursor.getColumnIndex("id")));
            mTitles.add(cursor.getString(cursor.getColumnIndex("title")));
            mShorts.add(cursor.getString(cursor.getColumnIndex("short_desc")));
            mDetails.add(cursor.getString(cursor.getColumnIndex("detail")));
            mDates.add(cursor.getString(cursor.getColumnIndex("date")));
            mAttachmentLinks.add(cursor.getString(cursor.getColumnIndex("attachment_link")));
            mAttachmentTitles.add(cursor.getString(cursor.getColumnIndex("attachment_title")));
        }
        cursor.close();
        if (i == 0) {
            progressBar.setVisibility(View.VISIBLE);
            fetchNoticesFromInternet(true);
        } else
            fillRecy();

    }

    private void fillRecy() {
        progressBar.setVisibility(View.GONE);
        swipeNotice.setVisibility(View.VISIBLE);
        NoticeAdapter adapter = new NoticeAdapter(getActivity(), mIds, mTitles, mShorts, mDates);
        recyclerViewNotice.setAdapter(adapter);
        recyclerViewNotice.setLayoutManager(new LinearLayoutManager(getActivity()));

    }
}