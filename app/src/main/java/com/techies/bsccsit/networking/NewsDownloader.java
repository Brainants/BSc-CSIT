package com.techies.bsccsit.networking;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.techies.bsccsit.advance.BackgroundTaskHandler;
import com.techies.bsccsit.advance.Singleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsDownloader extends AsyncTask<Void, Void, Void> {
    boolean success = false;
    ArrayList<String> names = new ArrayList<>(),
            posterId = new ArrayList<>(),
            fullImage = new ArrayList<>(),
            message = new ArrayList<>(),
            created_time = new ArrayList<>();
    private OnTaskCompleted listener;

    @Override
    public Void doInBackground(Void... params) {
        Bundle param = new Bundle();
        param.putString("ids", Singleton.getFollowingList());
        param.putString("fields", "id,from,created_time,full_picture,message,story");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "posts", param, HttpMethod.GET, new GraphRequest.Callback() {
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

        names.clear();
        posterId.clear();
        fullImage.clear();
        message.clear();
        created_time.clear();
        try {
            for (int i = 0; i < ids.size(); i++) {
                JSONObject eachPage = object.getJSONObject(ids.get(i));
                JSONArray array = eachPage.getJSONArray("data");
                for (int j = 0; j < array.length(); j++) {
                    JSONObject eachPost = array.getJSONObject(j);
                    try {
                        eachPost.getString("story");
                    } catch (Exception e) {
                        names.add(eachPost.getJSONObject("from").getString("name"));
                        posterId.add(eachPost.getJSONObject("from").getString("id"));

                        try {
                            fullImage.add(eachPost.getString("full_picture"));
                        } catch (Exception ex) {
                            fullImage.add("");
                        }

                        try {
                            message.add(eachPost.getString("message"));
                        } catch (Exception ex) {
                            message.add("");
                        }

                        created_time.add(eachPost.getString("created_time"));
                    }
                }
            }
            NewsFeedSorter();
            addToDatabase();
        } catch (Exception e) {
        }
    }

    public boolean NewsFeedSorter() {
        for (int i = 0; i < names.size(); i++) {
            for (int j = i; j < created_time.size(); j++)
                if (BackgroundTaskHandler.convertToSimpleDate(created_time.get(i)).compareTo(BackgroundTaskHandler.convertToSimpleDate(created_time.get(j))) < 0) {
                    String name, poster, image, newMessage, time;
                    name = names.get(j);
                    poster = posterId.get(j);
                    image = fullImage.get(j);
                    newMessage = message.get(j);
                    time = created_time.get(j);

                    names.remove(j);
                    posterId.remove(j);
                    fullImage.remove(j);
                    created_time.remove(j);
                    message.remove(j);

                    names.add(j, names.get(i));
                    posterId.add(j, posterId.get(i));
                    fullImage.add(j, fullImage.get(i));
                    created_time.add(j, created_time.get(i));
                    message.add(j, message.get(i));

                    names.remove(i);
                    posterId.remove(i);
                    fullImage.remove(i);
                    created_time.remove(i);
                    message.remove(i);


                    names.add(i, name);
                    posterId.add(i, poster);
                    fullImage.add(i, image);
                    created_time.add(i, time);
                    message.add(i, newMessage);
                }
        }
        return true;
    }

    private void addToDatabase() {
        ContentValues values = new ContentValues();
        Singleton.getInstance().getDatabase().execSQL("DELETE FROM news;");
        for (int i = 0; i < names.size(); i++) {
            values.clear();
            values.put("names", names.get(i));
            values.put("posterId", posterId.get(i));
            values.put("fullImage", fullImage.get(i));
            values.put("message", message.get(i));
            values.put("created_time", created_time.get(i));
            Singleton.getInstance().getDatabase().insert("news", null, values);
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
