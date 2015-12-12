package com.techies.bsccsit.advance;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BackgroundTaskHandler extends GcmTaskService {

    @Override
    public int onRunTask(TaskParams taskParams) {
        new NewsDownloader().execute();
        new EventsDownloader().execute();
        new CommunitiesDownloader().execute();
        return 1;
    }


    private static Date convertToSimpleDate(String created_time) {

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZZZZZ", Locale.US);
        try {
            return simpleDateFormat.parse(created_time);
        } catch (Exception e) {
            return null;
        }
    }

    class CommunitiesDownloader extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            String url="https://slim-bloodskate.c9users.io/app/api/allcomm";
            final JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Singleton.getInstance().getDatabase().execSQL("DELETE FROM popularCommunities");
                    ArrayList<String> ids=new ArrayList<>(),
                            names=new ArrayList<>(),
                            extra=new ArrayList<>();
                    ArrayList<Boolean> verified=new ArrayList<>();

                    ids.clear();
                    names.clear();
                    verified.clear();
                    extra.clear();
                    try{
                        ContentValues values=new ContentValues();
                        Singleton.getInstance().getDatabase().execSQL("DELETE FROM popularCommunities;");
                        for (int i=0;i<response.length();i++){
                            JSONObject object=response.getJSONObject(i);
                            ids.add(object.getString("fbid"));
                            names.add(object.getString("title"));
                            verified.add(Integer.parseInt(object.getString("isverified"))==1);
                            extra.add(object.getString("extra"));

                            values.clear();
                            values.put("FbID",ids.get(i));
                            values.put("Title",names.get(i));
                            values.put("IsVerified",verified.get(i)?1:0);
                            values.put("ExtraText",extra.get(i));
                            Singleton.getInstance().getDatabase().insert("popularCommunities",null,values);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            },null);
            Singleton.getInstance().getRequestQueue().add(request);
            return null;
        }
    }

    class NewsDownloader extends AsyncTask<Void,Void,Void> {

        ArrayList<String>  names=new ArrayList<>(),
                posterId=new ArrayList<>(),
                fullImage=new ArrayList<>(),
                message=new ArrayList<>(),
                created_time=new ArrayList<>();

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
                NewsFeedSorter();
                addToDatabase();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        public boolean NewsFeedSorter(){
            for (int i=0;i<names.size();i++){
                for (int j=i;j<created_time.size();j++)
                    if (convertToSimpleDate(created_time.get(i)).compareTo(convertToSimpleDate(created_time.get(j)))<0){
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
            }
            return true;
        }

        private  void addToDatabase(){
            ContentValues values=new ContentValues();
            Singleton.getInstance().getDatabase().execSQL("DELETE FROM news;");
            for (int i=0;i<names.size();i++){
                values.clear();
                values.put("names",names.get(i));
                values.put("posterId",posterId.get(i));
                values.put("fullImage",fullImage.get(i));
                values.put("message",message.get(i));
                values.put("created_time",created_time.get(i));
                Singleton.getInstance().getDatabase().insert("news",null,values);
            }
        }
    }

    class EventsDownloader extends AsyncTask<Void, Void, Void> {

        ArrayList<String> names=new ArrayList<>(),
                created_time=new ArrayList<>(),
                eventIDs=new ArrayList<>(),
                hosters=new ArrayList<>(),
                fullImage=new ArrayList<>();

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
                        try {
                            fullImage.add(eachPost.getJSONObject("cover").getString("source"));
                        } catch (Exception e){
                            fullImage.add("");
                        }
                        created_time.add(eachPost.getString("start_time"));
                    }
                }
                EventsSorter();
                addToDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void EventsSorter() {
            for (int i = 0; i < names.size(); i++) {
                for (int j = i; j < created_time.size(); j++)
                    if (convertToSimpleDate(created_time.get(i)).compareTo(convertToSimpleDate(created_time.get(j))) < 0){
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

        private  void addToDatabase(){
            ContentValues values=new ContentValues();
            Singleton.getInstance().getDatabase().execSQL("DELETE FROM events;");
            for (int i=0;i<names.size();i++){
                values.clear();
                values.put("names",names.get(i));
                values.put("eventIDs",eventIDs.get(i));
                values.put("fullImage",fullImage.get(i));
                values.put("hosters",hosters.get(i));
                values.put("created_time",created_time.get(i));
                Singleton.getInstance().getDatabase().insert("events",null,values);
            }

        }
    }
}