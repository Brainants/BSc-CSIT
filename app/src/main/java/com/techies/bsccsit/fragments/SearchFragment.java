package com.techies.bsccsit.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.techies.bsccsit.R;
import com.techies.bsccsit.adapters.FacebookSearchAdapter;
import com.techies.bsccsit.advance.Singleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

public class SearchFragment extends Fragment {
    EditText searchText;
    RecyclerView recyclerView;
    FancyButton searchBtn;
    View coreView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_facebook_search, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.coreView =view;
        searchText = (EditText) view.findViewById(R.id.searchText);
        recyclerView = (RecyclerView) view.findViewById(R.id.searchRecy);
        searchBtn= (FancyButton) view.findViewById(R.id.btnFbSearch);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchText.getText().length() != 0) {
                    searchUsingFacebook();
                } else {
                    Snackbar.make(view.findViewById(R.id.searchCore),"Please add some text.",Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

     private void searchUsingFacebook() {
        Bundle bundle = new Bundle();
        Toast.makeText(getActivity(), "Searching...", Toast.LENGTH_SHORT).show();
        bundle.putString("fields", "is_verified,id,name,privacy,likes");
         bundle.putString("q", searchText.getText().toString());
         bundle.putString("type","page");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "search", bundle, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                try {
                    final ArrayList<String> names = new ArrayList<>(),
                            ids = new ArrayList<>(),
                            extras = new ArrayList<>();
                    final ArrayList<Boolean> verified = new ArrayList<>();

                    if (response.getError() == null) {
                        JSONObject core = new JSONObject(response.getRawResponse());
                        JSONArray array = core.getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            names.add(object.getString("name"));
                            ids.add(object.getString("id"));
                            verified.add(object.getBoolean("is_verified"));
                            extras.add(object.getInt("likes") + " likes");

                        }

                        final FacebookSearchAdapter adapter = new FacebookSearchAdapter(getActivity()   , names, extras, ids, verified);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        adapter.setOnClickListener(new FacebookSearchAdapter.ClickListener() {
                            @Override
                            public void onClick(FancyButton view, int position) {
                                ContentValues values=new ContentValues();
                                values.put("Title",names.get(position));
                                values.put("ImageLink","https://graph.facebook.com/"+ids.get(position)+"/picture?type=large");
                                values.put("FbID",ids.get(position));
                                values.put("isVerified",verified.get(position)?1:0);
                                values.put("ExtraText",extras.get(position));
                                Singleton.getInstance().getDatabase().insert("myCommunities",null,values);
                                Snackbar.make(coreView,names.get(position)+" added Successfully.",Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }

        }).executeAsync();

    }
}
