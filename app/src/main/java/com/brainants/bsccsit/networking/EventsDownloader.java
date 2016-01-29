package com.brainants.bsccsit.networking;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.brainants.bsccsit.advance.BackgroundTaskHandler;
import com.brainants.bsccsit.advance.Singleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventsDownloader extends AsyncTask<Void, Void, Void> {
    ArrayList<String> names = new ArrayList<>(),
            created_time = new ArrayList<>(),
            eventIDs = new ArrayList<>(),
            hosters = new ArrayList<>(),
            fullImage = new ArrayList<>();
    boolean success = false;
    private OnTaskCompleted listener;

    @Override
    public Void doInBackground(Void... params) {
        Bundle param = new Bundle();
        param.putString("ids", Singleton.getFollowingList());
        param.putString("fields", "id,name,start_time,cover,owner");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "events", param, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if (response.getError() == null) {
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
                    try {
                        fullImage.add(eachPost.getJSONObject("cover").getString("source"));
                    } catch (Exception e) {
                        fullImage.add("");
                    }
                    created_time.add(eachPost.getString("start_time"));
                }
            }
            EventsSorter();
            addToDatabase();
        } catch (Exception ignored) {
        }
    }

    private void EventsSorter() {
        for (int i = 0; i < names.size(); i++) {
            for (int j = i; j < created_time.size(); j++)
                if (BackgroundTaskHandler.convertToSimpleDate(created_time.get(i)).compareTo(BackgroundTaskHandler.convertToSimpleDate(created_time.get(j))) < 0) {
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
    }

    private void addToDatabase() {
        ContentValues values = new ContentValues();
        Singleton.getInstance().getDatabase().execSQL("DELETE FROM events;");
        for (int i = 0; i < names.size(); i++) {
            values.clear();
            values.put("names", names.get(i));
            values.put("eventIDs", eventIDs.get(i));
            values.put("fullImage", fullImage.get(i));
            values.put("hosters", hosters.get(i));
            values.put("created_time", created_time.get(i));
            Singleton.getInstance().getDatabase().insert("events", null, values);
        }
        success = true;
    }

    public void setTaskCompleteListener(OnTaskCompleted listener) {
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onTaskCompleted(success);
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(boolean success);
    }
}
