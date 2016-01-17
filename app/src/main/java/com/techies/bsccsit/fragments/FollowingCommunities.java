package com.techies.bsccsit.fragments;

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

import com.techies.bsccsit.R;
import com.techies.bsccsit.adapters.FacebookSearchAdapter;
import com.techies.bsccsit.advance.Singleton;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;


public class FollowingCommunities extends Fragment {

    public static FacebookSearchAdapter adapter;
    final ArrayList<String> names = new ArrayList<>(),
            extra = new ArrayList<>(),
            ids = new ArrayList<>();
    private RecyclerView recy;
    private View core;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_following_communities, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM myCommunities", null);

        adapter = new FacebookSearchAdapter(getActivity(), "my", names, extra, ids);
        recy.setAdapter(adapter);

        while (cursor.moveToNext()) {
            names.add(cursor.getString(cursor.getColumnIndex("Title")));
            extra.add(cursor.getString(cursor.getColumnIndex("ExtraText")));
            ids.add(cursor.getString(cursor.getColumnIndex("FbID")));
        }
        adapter = new FacebookSearchAdapter(getActivity(), "my", names, extra, ids);
        recy.setAdapter(adapter);
        recy.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        adapter.setOnClickListener(new FacebookSearchAdapter.ClickListener() {
            @Override
            public void onClick(FancyButton view, int position) {
                if (Singleton.getFollowingArray().size() <= 6) {
                    Snackbar.make(core, "You must follow at least 5 communities.", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Singleton.getInstance().getDatabase().execSQL("DELETE FROM myCommunities WHERE FbID = " + ids.get(position));
                Snackbar.make(core, names.get(position) + " removed Successfully.", Snackbar.LENGTH_SHORT).show();
                adapter.removeItem(position);
                PopularCommunities.adapter.notifyDataSetChanged();
                getActivity().getSharedPreferences("community", Context.MODE_PRIVATE).edit().putBoolean("changedComm", true).apply();
            }
        });
        cursor.close();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recy = (RecyclerView) view.findViewById(R.id.recyFollowing);
        this.core = view;
    }
}
