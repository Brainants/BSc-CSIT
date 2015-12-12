package com.techies.bsccsit.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techies.bsccsit.R;
import com.techies.bsccsit.adapters.EventAdapter;
import com.techies.bsccsit.advance.Singleton;

import java.util.ArrayList;

public class Events extends Fragment {

    ArrayList<String> names=new ArrayList<>(),
            created_time=new ArrayList<>(),
            eventIDs=new ArrayList<>(),
            hosters=new ArrayList<>(),
            fullImage=new ArrayList<>();
    private RecyclerView recyclerView;

    public Events() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewEvent);
    }

    private void fillFromDatabase() {
        Cursor cursor= Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM events",null);
        while(cursor.moveToNext()){
            names.add(cursor.getString(cursor.getColumnIndex("names")));
            eventIDs.add(cursor.getString(cursor.getColumnIndex("eventIDs")));
            fullImage.add(cursor.getString(cursor.getColumnIndex("fullImage")));
            hosters.add(cursor.getString(cursor.getColumnIndex("hosters")));
            created_time.add(cursor.getString(cursor.getColumnIndex("created_time")));
        }
        cursor.close();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fillFromDatabase();
        setToAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }
    private void setToAdapter() {
        EventAdapter adapter = new EventAdapter(getActivity(), names, created_time, hosters, fullImage);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
