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

import com.afollestad.materialdialogs.MaterialDialog;
import com.techies.bsccsit.R;
import com.techies.bsccsit.adapters.eLibraryAdapter;
import com.techies.bsccsit.advance.Singleton;

import java.util.ArrayList;

public class eLibraryPagerFragment extends Fragment {

    private ArrayList<String> Title=new ArrayList<>(),
            Source=new ArrayList<>(),
            Link=new ArrayList<>(),
            FileName=new ArrayList<>();
    private RecyclerView recy;
    private String[] types={"syllabus","notes","old_question","solutions"};


    public eLibraryPagerFragment(){
        // Required empty public constructor
    }

    public static eLibraryPagerFragment newInstance(int page) {
        eLibraryPagerFragment fragmentFirst = new eLibraryPagerFragment();
        Bundle args = new Bundle();
        args.putInt("position", page);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recy= (RecyclerView) view.findViewById(R.id.recyELibrary);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        filFromDatabase(types[getArguments().getInt("position")]);
    }

    private void filFromDatabase(String type) {
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM eLibrary WHERE Tag = '"+type+"';",null);
        while (cursor.moveToNext()){
            Title.add(cursor.getString(cursor.getColumnIndex("Title")));
            Source.add(cursor.getString(cursor.getColumnIndex("Source")));
            Link.add(cursor.getString(cursor.getColumnIndex("Link")));
            FileName.add(cursor.getString(cursor.getColumnIndex("FileName")));
        }
        fillAdapter();
        cursor.close();
    }

    private void fillAdapter() {
        recy.setLayoutManager(new LinearLayoutManager(getActivity()));
        eLibraryAdapter adapter=new eLibraryAdapter(getActivity(),types[getArguments().getInt("position")],Title,Source,FileName);
        recy.setAdapter(adapter);
        adapter.setOnCLickListener(new eLibraryAdapter.ClickListener() {
            @Override
            public void onIconClick(View view, int position) {
                MaterialDialog dialog= new MaterialDialog.Builder(getActivity())
                        .title("Downloading...")
                        .progress(false,100)
                        .build();
                //// TODO: 12/15/2015 downloader
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_e_library_pager, container, false);
    }
}

