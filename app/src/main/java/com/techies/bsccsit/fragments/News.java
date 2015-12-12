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
import com.techies.bsccsit.adapters.NewsAdapter;
import com.techies.bsccsit.advance.Singleton;

import java.util.ArrayList;

public class News extends Fragment {

    private ArrayList<String>  names=new ArrayList<>(),
            posterId=new ArrayList<>(),
            fullImage=new ArrayList<>(),
            message=new ArrayList<>(),
            created_time=new ArrayList<>();

    private RecyclerView recyclerView;

    public News() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fillFromDatabase();
        setToAdapter();
    }

    private void fillFromDatabase() {
        Cursor cursor= Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM news",null);
        while(cursor.moveToNext()){
            names.add(cursor.getString(cursor.getColumnIndex("names")));
            posterId.add(cursor.getString(cursor.getColumnIndex("posterId")));
            fullImage.add(cursor.getString(cursor.getColumnIndex("fullImage")));
            message.add(cursor.getString(cursor.getColumnIndex("message")));
            created_time.add(cursor.getString(cursor.getColumnIndex("created_time")));
        }
        cursor.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    private void setToAdapter() {
        NewsAdapter adapter=new NewsAdapter(getActivity(),names,created_time,posterId,message,fullImage);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView= (RecyclerView) view.findViewById(R.id.recyclerViewNews);
    }
}
