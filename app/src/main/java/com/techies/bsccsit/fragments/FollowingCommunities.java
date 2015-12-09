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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.techies.bsccsit.R;
import com.techies.bsccsit.adapters.FacebookSearchAdapter;
import com.techies.bsccsit.advance.Singleton;

import java.util.ArrayList;


public class FollowingCommunities extends Fragment {

    private RecyclerView recy;
    LinearLayout errorLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_following_communities, container, false);
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Cursor cursor=Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM myCommunities",null);

            if(cursor.getCount()!=0) {
                errorLayout.setVisibility(View.GONE);
                ArrayList<String> names=new ArrayList<>(),
                        extra=new ArrayList<>(),
                        ids=new ArrayList<>();
                ArrayList<Boolean> verified=new ArrayList<>();

                while (cursor.moveToNext()) {
                    names.add(cursor.getString(cursor.getColumnIndex("Title")));
                    extra.add(cursor.getString(cursor.getColumnIndex("ExtraText")));
                    ids.add(cursor.getString(cursor.getColumnIndex("FbID")));
                    verified.add(cursor.getInt(cursor.getColumnIndex("IsVerified")) == 1);
                }
                recy.setAdapter(new FacebookSearchAdapter(getActivity(), names, extra, ids, verified));
                recy.setLayoutManager(new LinearLayoutManager(getActivity()));
            }else {
                errorLayout.setVisibility(View.VISIBLE);
            }
            cursor.close();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recy= (RecyclerView) view.findViewById(R.id.recyFollowing);
        errorLayout= (LinearLayout) view.findViewById(R.id.errorMessageFollowing);
    }
}
