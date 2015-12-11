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
import com.techies.bsccsit.adapters.EventAdapter;
import com.techies.bsccsit.adapters.NewsAdapter;
import com.techies.bsccsit.advance.Singleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Events extends Fragment {

    ArrayList<String> names, created_time, eventIDs, hosters, fullImage;
    private RecyclerView recyclerView;

    public Events() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewEvent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    class DataDownloader extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Bundle param = new Bundle();
            param.putString("ids", Singleton.getFollowingList());
            param.putString("fields", "id,name,start_time,cover,owner");
            new GraphRequest(AccessToken.getCurrentAccessToken(), "events", param, HttpMethod.GET, new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    if (response.getError() != null) {
                        Log.d("Debug", "Unable " + response.getError().toString());
                    } else {
                        Log.d("Debug", response.getJSONObject().toString());
                        parseTheResponse(response.getJSONObject());
                    }
                }
            }).executeAndWait();
            return null;
        }

        private void parseTheResponse(JSONObject object) {
            ArrayList<String> ids = Singleton.getFollowingArray();
            try {
                for (int i = 0; i < ids.size(); i++) {
                    JSONObject eachPage = object.getJSONObject(ids.get(i));
                    JSONArray array = eachPage.getJSONArray("data");
                    for (int j = 0; j < array.length(); j++) {
                        JSONObject eachPost = array.getJSONObject(j);
                        eventIDs.add(eachPost.getString("id"));
                        names.add(eachPost.getString("name"));
                        hosters.add(eachPost.getJSONObject("owner").getString("name"));
                        fullImage.add(eachPost.getJSONObject("cover").getString("source"));
                        created_time.add(eachPost.getString("start_time"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            EventsSorter();
        }
    }

    private void EventsSorter() {
        for (int i = 0; i < names.size(); i++) {
            for (int j = i; j < created_time.size(); j++)
                if (convertToSimpleDate(created_time.get(i)).compareTo(convertToSimpleDate(created_time.get(j))) < 0)
                    swap(i, j);
        }
        setToAdapter();
    }

    private void setToAdapter() {
        EventAdapter adapter = new EventAdapter(getActivity(), names, created_time, hosters, fullImage);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private static Date convertToSimpleDate(String created_time) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZZZZZ", Locale.US);
        try {
            return simpleDateFormat.parse(created_time);
        } catch (Exception e) {
            return null;
        }
    }

    public void swap(int i, int j) {
        String name, hoster, image, id, time;
        name = names.get(j);
        time = created_time.get(j);
        id = eventIDs.get(j);
        hoster = hosters.get(j);
        image = fullImage.get(j);

        names.remove(j);
        created_time.remove(j);
        eventIDs.remove(j);
        hosters.remove(j);
        fullImage.remove(j);

        names.add(j, names.get(i));
        hosters.add(j, hosters.get(i));
        fullImage.add(j, fullImage.get(i));
        created_time.add(j, created_time.get(i));
        eventIDs.add(j, eventIDs.get(i));

        names.remove(i);
        created_time.remove(i);
        eventIDs.remove(i);
        hosters.remove(i);
        fullImage.remove(i);


        names.add(i, name);
        created_time.add(i, time);
        fullImage.add(i, image);
        hosters.add(i, hoster);
        eventIDs.add(i, id);
    }
}
