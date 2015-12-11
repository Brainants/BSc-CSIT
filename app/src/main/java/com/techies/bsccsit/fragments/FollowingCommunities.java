package com.techies.bsccsit.fragments;

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

import com.techies.bsccsit.R;
import com.techies.bsccsit.adapters.FacebookSearchAdapter;
import com.techies.bsccsit.advance.Singleton;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;


public class FollowingCommunities extends Fragment {

    private RecyclerView recy;
    public static LinearLayout errorLayout;
    public static FacebookSearchAdapter adapter;
    private View core;

    final ArrayList<String> names=new ArrayList<>(),
            extra=new ArrayList<>(),
            ids=new ArrayList<>();
    ArrayList<Boolean> verified=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_following_communities, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Cursor cursor=Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM myCommunities",null);

        adapter=new FacebookSearchAdapter(getActivity(),"my", names, extra, ids, verified);
        recy.setAdapter(adapter);

        if(cursor.getCount()!=0) {
            errorLayout.setVisibility(View.GONE);

            while (cursor.moveToNext()) {
                names.add(cursor.getString(cursor.getColumnIndex("Title")));
                extra.add(cursor.getString(cursor.getColumnIndex("ExtraText")));
                ids.add(cursor.getString(cursor.getColumnIndex("FbID")));
                verified.add(cursor.getInt(cursor.getColumnIndex("IsVerified")) == 1);
            }
            adapter=new FacebookSearchAdapter(getActivity(),"my", names, extra, ids, verified);
            recy.setAdapter(adapter);
            recy.setLayoutManager(new GridLayoutManager(getActivity(),2));
            adapter.setOnClickListener(new FacebookSearchAdapter.ClickListener() {
                @Override
                public void onClick(FancyButton view, int position) {
                    Singleton.getInstance().getDatabase().execSQL("DELETE FROM myCommunities WHERE FbID = "+ids.get(position));
                    Snackbar.make(core,names.get(position)+" removed Successfully.",Snackbar.LENGTH_SHORT).show();
                    adapter.removeItem(position);
                    if(adapter.getItemCount()==0)
                        errorLayout.setVisibility(View.VISIBLE);
                    PopularCommunities.adapter.notifyDataSetChanged();
                }
            });
        }else {
            errorLayout.setVisibility(View.VISIBLE);
        }
        cursor.close();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recy= (RecyclerView) view.findViewById(R.id.recyFollowing);
        errorLayout= (LinearLayout) view.findViewById(R.id.errorMessageFollowing);
        this.core=view;
    }
}
