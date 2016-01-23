package com.techies.bsccsit.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.techies.bsccsit.R;
import com.techies.bsccsit.activities.MainActivity;
import com.techies.bsccsit.adapters.FacebookSearchAdapter;
import com.techies.bsccsit.advance.Singleton;
import com.techies.bsccsit.networking.PopularCommunitiesDownloader;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

public class PopularCommunities extends Fragment {

    public static FacebookSearchAdapter adapter;
    private RecyclerView recyclerview;
    private View core;
    private ArrayList<String> names=new ArrayList<>(),
            extra=new ArrayList<>(),
            ids=new ArrayList<>();
    private ProgressBar progress;
    private LinearLayout error;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_popular_communities, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fillFromDatabase();
    }

    private void fillFromDatabase(){
        int count=0;
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM popularCommunities", null);
        while (cursor.moveToNext()) {
            count++;
            names.add(cursor.getString(cursor.getColumnIndex("Title")));
            extra.add(cursor.getString(cursor.getColumnIndex("ExtraText")));
            ids.add(cursor.getString(cursor.getColumnIndex("FbID")));
        }
        cursor.close();
        if(count==0) {
            progress.setVisibility(View.VISIBLE);
            downloadFromInternet();
        }else
            fillRecyclerView();
    }

    private void fillRecyclerView(){
        recyclerview.setVisibility(View.VISIBLE);
        adapter = new FacebookSearchAdapter(getActivity(),"my", names, extra, ids);
        recyclerview.setAdapter(adapter);
        recyclerview.setLayoutManager(new GridLayoutManager(getActivity(),2));
        adapter.setOnClickListener(new FacebookSearchAdapter.ClickListener() {
            @Override
            public void onClick(FancyButton view, int position) {
                if (Singleton.checkExistInFollowing(ids.get(position))){
                    if (Singleton.getFollowingArray().size() <= 6) {
                        Snackbar.make(core, "You must follow at least 5 communities.", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    Singleton.getInstance().getDatabase().execSQL("DELETE FROM myCommunities WHERE FbID = "+ids.get(position));
                    Snackbar.make(core,names.get(position)+" removed Successfully.",Snackbar.LENGTH_SHORT).show();
                    FollowingCommunities.adapter.removeBySearch(ids.get(position));
                }else {
                    ContentValues values=new ContentValues();
                    values.put("Title",names.get(position));
                    values.put("FbID",ids.get(position));
                    values.put("ExtraText",extra.get(position));
                    Singleton.getInstance().getDatabase().insert("myCommunities",null,values);
                    Snackbar.make(core,names.get(position)+" added Successfully.",Snackbar.LENGTH_SHORT).show();
                    FollowingCommunities.adapter.addItem(names.get(position),ids.get(position),extra.get(position));
                    getActivity().getSharedPreferences("community", Context.MODE_PRIVATE).edit().putBoolean("changedComm",true).apply();
                }
                adapter.notifyItemChanged(position);
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.core=view;
        recyclerview= (RecyclerView) view.findViewById(R.id.popularRecy);
        progress= (ProgressBar) view.findViewById(R.id.progressCommunities);
        error= (LinearLayout) view.findViewById(R.id.errorMessageCommunities);
        recyclerview.setVisibility(View.GONE);
        error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                downloadFromInternet();
            }
        });

    }

    private void downloadFromInternet() {
        PopularCommunitiesDownloader downloader = new PopularCommunitiesDownloader();
        downloader.doInBackground();
        downloader.setTaskCompleteListener(new PopularCommunitiesDownloader.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean success) {
                if (!MainActivity.current.equals("Communities"))
                    return;
                progress.setVisibility(View.GONE);
                if(success)
                    fillFromDatabase();
                else
                    error.setVisibility(View.VISIBLE);
            }
        });
    }
}
