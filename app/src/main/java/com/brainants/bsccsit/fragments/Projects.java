package com.brainants.bsccsit.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.brainants.bsccsit.R;
import com.brainants.bsccsit.activities.AddProject;
import com.brainants.bsccsit.activities.MainActivity;
import com.brainants.bsccsit.adapters.ProjectAdapter;
import com.brainants.bsccsit.advance.Singleton;
import com.brainants.bsccsit.networking.ProjectsDownloader;

import java.util.ArrayList;

public class Projects extends Fragment {

    private RecyclerView recyclerView;

    private ArrayList<String> titles = new ArrayList<>(),
            projectID = new ArrayList<>(),
            tags = new ArrayList<>(),
            user = new ArrayList<>(),
            detail = new ArrayList<>();

    private ProgressBar progress;
    private LinearLayout error;
    private SwipeRefreshLayout swipeLayout;
    private View coreView;

    public Projects() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fillFromDatabase();

        FloatingActionButton fab = MainActivity.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), AddProject.class), 10);
            }
        });
        fab.show();
    }

    private void fillFromDatabase() {
        titles.clear();
        projectID.clear();
        tags.clear();
        detail.clear();
        user.clear();
        int count = 0;
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM projects", null);
        while (cursor.moveToNext()) {
            count++;
            titles.add(cursor.getString(cursor.getColumnIndex("title")));
            projectID.add(cursor.getString(cursor.getColumnIndex("projectID")));
            tags.add(cursor.getString(cursor.getColumnIndex("tags")));
            detail.add(cursor.getString(cursor.getColumnIndex("detail")));
            user.add(cursor.getString(cursor.getColumnIndex("users")));
        }
        cursor.close();
        if (count == 0) {
            progress.setVisibility(View.VISIBLE);
            downloadFromInternet(true);
        } else
            setToAdapter();
    }

    private void downloadFromInternet(final boolean first) {
        ProjectsDownloader downloader =
                new ProjectsDownloader();
        downloader.setTaskCompleteListener(new ProjectsDownloader.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean success) {
                if (!MainActivity.current.equals("Projects"))
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

    private void setToAdapter() {
        MainActivity.fab.show();
        progress.setVisibility(View.GONE);
        swipeLayout.setVisibility(View.VISIBLE);
        ProjectAdapter adapter = new ProjectAdapter(getActivity(), projectID, titles, user, tags, detail);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Singleton.getSpanCount(getContext())));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_projects, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        swipeLayout.setRefreshing(true);
        downloadFromInternet(false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewProject);
        progress = (ProgressBar) view.findViewById(R.id.progressProject);
        error = (LinearLayout) view.findViewById(R.id.errorMessageProject);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeProject);
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
