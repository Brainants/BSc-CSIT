package com.techies.bsccsit.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.techies.bsccsit.R;
import com.techies.bsccsit.adapters.NewsAdapter;
import com.techies.bsccsit.advance.Singleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
        new DataDownloader().execute();
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

    public boolean NewsFeedSorter(){
        for (int i=0;i<names.size();i++){
            for (int j=i;j<created_time.size();j++)
                if (convertToSimpleDate(created_time.get(i)).compareTo(convertToSimpleDate(created_time.get(j)))<0)
                    swap(i,j);
        }
        setToAdapter();
        return true;
    }

    public void swap(int i,int j){
        String name,poster,image,newMessage,time;
        name=names.get(j);
        poster=posterId.get(j);
        image=fullImage.get(j);
        newMessage=message.get(j);
        time=created_time.get(j);

        names.remove(j);
        posterId.remove(j);
        fullImage.remove(j);
        created_time.remove(j);
        message.remove(j);

        names.add(j,names.get(i));
        posterId.add(j,posterId.get(i));
        fullImage.add(j,fullImage.get(i));
        created_time.add(j,created_time.get(i));
        message.add(j,message.get(i));

        names.remove(i);
        posterId.remove(i);
        fullImage.remove(i);
        created_time.remove(i);
        message.remove(i);


        names.add(i,name);
        posterId.add(i,poster);
        fullImage.add(i,image);
        created_time.add(i,time);
        message.add(i,newMessage);
    }

    private static Date convertToSimpleDate(String created_time) {

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZZZZZ", Locale.US);
        try {
            return simpleDateFormat.parse(created_time);
        } catch (Exception e) {
            return null;
        }
    }

    class DataDownloader extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            Bundle param=new Bundle();
            param.putString("ids", Singleton.getFollowingList());
            param.putString("fields","id,from,created_time,message,story");
            new GraphRequest(AccessToken.getCurrentAccessToken(), "posts", param, HttpMethod.GET, new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    if (response.getError()!=null){
                        Log.d("Debug","Unable " + response.getError().toString());
                    } else {
                        Log.d("Debug",response.getJSONObject().toString());
                        parseTheResponse(response.getJSONObject());

                    }
                }
            }).executeAndWait();
            return null;
        }

        private void parseTheResponse(JSONObject object) {
            ArrayList<String> ids=Singleton.getFollowingArray();
            try {
                for (int i = 0; i < ids.size(); i++) {
                    JSONObject eachPage = object.getJSONObject(ids.get(i));
                    JSONArray array=eachPage.getJSONArray("data");
                    for (int j=0;j<array.length();j++){
                        JSONObject eachPost=array.getJSONObject(j);
                        try {
                            eachPost.getString("story");
                        }catch (Exception e){
                            names.add(eachPost.getJSONObject("from").getString("name"));
                            posterId.add(eachPost.getJSONObject("from").getString("id"));

                            try {
                                fullImage.add(eachPost.getString("full_picture"));
                            }catch (Exception ex) {
                                fullImage.add("");
                            }

                            try {
                                message.add(eachPost.getString("message"));
                            }catch (Exception ex) {
                                message.add("");
                            }

                            created_time.add(eachPost.getString("created_time"));
                        }
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            NewsFeedSorter();
        }
    }

}
