package com.techies.bsccsit.fragments;

import android.content.ContentValues;
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

import com.techies.bsccsit.R;
import com.techies.bsccsit.adapters.FacebookSearchAdapter;
import com.techies.bsccsit.advance.Singleton;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

public class PopularCommunities extends Fragment {

    private RecyclerView recyclerview;
    private View core;


    private ArrayList<String> names=new ArrayList<>(),
            extra=new ArrayList<>(),
            ids=new ArrayList<>();
    private ArrayList<Boolean> verified=new ArrayList<>();
    public static FacebookSearchAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_popular_communities, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM popularCommunities", null);
        while (cursor.moveToNext()) {
            names.add(cursor.getString(cursor.getColumnIndex("Title")));
            extra.add(cursor.getString(cursor.getColumnIndex("ExtraText")));
            ids.add(cursor.getString(cursor.getColumnIndex("FbID")));
            verified.add(cursor.getInt(cursor.getColumnIndex("IsVerified")) == 1);
        }
        cursor.close();
        fillRecyclerView();
    }


    private void fillRecyclerView(){
        adapter = new FacebookSearchAdapter(getActivity(),"my", names, extra, ids, verified);
        recyclerview.setAdapter(adapter);
        recyclerview.setLayoutManager(new GridLayoutManager(getActivity(),2));
        adapter.setOnClickListener(new FacebookSearchAdapter.ClickListener() {
            @Override
            public void onClick(FancyButton view, int position) {
                if (Singleton.checkExistInFollowing(ids.get(position))){
                    Singleton.getInstance().getDatabase().execSQL("DELETE FROM myCommunities WHERE FbID = "+ids.get(position));
                    Snackbar.make(core,names.get(position)+" removed Successfully.",Snackbar.LENGTH_SHORT).show();
                    FollowingCommunities.adapter.removeBySearch(ids.get(position));
                    if (FollowingCommunities.adapter.getItemCount()==0)
                        FollowingCommunities.errorLayout.setVisibility(View.VISIBLE);
                }else {
                    ContentValues values=new ContentValues();
                    values.put("Title",names.get(position));
                    values.put("FbID",ids.get(position));
                    values.put("isVerified",verified.get(position)?1:0);
                    values.put("ExtraText",extra.get(position));
                    Singleton.getInstance().getDatabase().insert("myCommunities",null,values);
                    Snackbar.make(core,names.get(position)+" added Successfully.",Snackbar.LENGTH_SHORT).show();
                    if (FollowingCommunities.adapter.getItemCount()>0)
                        FollowingCommunities.errorLayout.setVisibility(View.GONE);
                    FollowingCommunities.adapter.addItem(names.get(position),ids.get(position),extra.get(position),verified.get(position));
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
    }
}
