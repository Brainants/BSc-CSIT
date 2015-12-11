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
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.techies.bsccsit.R;
import com.techies.bsccsit.adapters.FacebookSearchAdapter;
import com.techies.bsccsit.advance.Singleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

public class PopularCommunities extends Fragment {

    private RecyclerView recyclerview;
    private LinearLayout errorLayout;
    private View core;
    private ProgressBar progress;


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

        if (cursor.getCount() != 0) {
            loadDataFromDatabase(cursor);
        }else {
            loadDataFromInternet(true);
        }
    }

    private void loadDataFromInternet(final boolean isFisrt) {
        if (isFisrt)
            progress.setVisibility(View.VISIBLE);
        else
            Snackbar.make(core,"Updating list....",Snackbar.LENGTH_SHORT).show();

        String url="https://slim-bloodskate.c9users.io/app/api/allcomm";
        final JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (isFisrt) {
                    progress.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                }
                Singleton.getInstance().getDatabase().execSQL("DELETE FROM popularCommunities");
                ids.clear();
                names.clear();
                verified.clear();
                extra.clear();
                try{
                    for (int i=0;i<response.length();i++){
                        JSONObject object=response.getJSONObject(i);
                        ids.add(object.getString("fbid"));
                        names.add(object.getString("title"));
                        verified.add(Integer.parseInt(object.getString("isverified"))==1);
                        extra.add(object.getString("extra"));

                        ContentValues values=new ContentValues();
                        values.put("FbID",ids.get(i));
                        values.put("Title",names.get(i));
                        values.put("IsVerified",verified.get(i)?1:0);
                        values.put("ExtraText",extra.get(i));
                        Singleton.getInstance().getDatabase().insert("popularCommunities",null,values);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

                fillRecyclerView();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (isFisrt)
                    errorLayout.setVisibility(View.VISIBLE);
                else
                    Snackbar.make(core,"Unable to update communities.",Snackbar.LENGTH_SHORT).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadDataFromInternet(false);
                        }
                    }).show();
            }
        });
        Singleton.getInstance().getRequestQueue().add(request);
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

    private void loadDataFromDatabase(Cursor cursor) {
            while (cursor.moveToNext()) {
                names.add(cursor.getString(cursor.getColumnIndex("Title")));
                extra.add(cursor.getString(cursor.getColumnIndex("ExtraText")));
                ids.add(cursor.getString(cursor.getColumnIndex("FbID")));
                verified.add(cursor.getInt(cursor.getColumnIndex("IsVerified")) == 1);
            }
        cursor.close();
        fillRecyclerView();
        loadDataFromInternet(false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.core=view;
        recyclerview= (RecyclerView) view.findViewById(R.id.popularRecy);
        errorLayout= (LinearLayout) view.findViewById(R.id.errorMessageCommunities);
        progress= (ProgressBar) view.findViewById(R.id.progressCommunities);
        errorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                loadDataFromInternet(true);
            }
        });
    }
}
