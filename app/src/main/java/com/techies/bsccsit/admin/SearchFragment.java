package com.techies.bsccsit.admin;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import java.util.HashMap;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class SearchFragment extends Fragment {
    final ArrayList<String> names = new ArrayList<>(),
            ids = new ArrayList<>(),
            extras = new ArrayList<>();
    EditText searchText;
    RecyclerView recyclerView;
    FancyButton searchBtn;
    View coreView;
    private FacebookSearchAdapter adapter;

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
        bundle.putString("fields", "id,category,name");
         bundle.putString("q", searchText.getText().toString());
         bundle.putString("type","page");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "search", bundle, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(final GraphResponse response) {
                try {
                    if (response.getError() == null) {
                        JSONObject core = new JSONObject(response.getRawResponse());
                        JSONArray array = core.getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            names.add(object.getString("name"));
                            ids.add(object.getString("id"));
                            extras.add(object.getString("category"));
                        }

                        adapter = new FacebookSearchAdapter(getActivity(),"all", names, extras, ids);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
                        adapter.setOnClickListener(new FacebookSearchAdapter.ClickListener() {
                            @Override
                            public void onClick(FancyButton view, final int position) {
                                if (Singleton.checkExistInPopular(ids.get(position))){
                                    methodToDelete(position);
                                }else {
                                    methodToAdd(position);
                                    }
                                adapter.notifyItemChanged(position);
                            }
                        });
                    }

                } catch (Exception ignored) {
                }
            }

        }).executeAsync();

    }

    private void methodToDelete(final int position) {
        final MaterialDialog dialog= new MaterialDialog.Builder(getActivity())
                .content("Adding...")
                .progress(true,0)
                .build();
        dialog.show();
        String url="https://slim-bloodskate.c9users.io/app/api/delcomm";
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Singleton.getInstance().getDatabase().execSQL("DELETE FROM popularCommunities WHERE FbID = "+ids.get(position));
                adapter.notifyItemChanged(position);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Snackbar.make(coreView,"Unable to delete "+names.get(position),Snackbar.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("fbid", ids.get(position));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        Singleton.getInstance().getRequestQueue().add(request);
    }

    private void methodToAdd(final int position) {
        final MaterialDialog dialog= new MaterialDialog.Builder(getActivity())
                .content("Adding...")
                .progress(true,0)
                .build();
        dialog.show();
        String url="https://slim-bloodskate.c9users.io/app/api/addcomm";
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ContentValues values=new ContentValues();
                values.put("FbID",ids.get(position));
                values.put("Title",names.get(position));
                values.put("ExtraText",extras.get(position));
                Singleton.getInstance().getDatabase().insert("popularCommunities",null,values);
                dialog.dismiss();
                adapter.notifyItemChanged(position);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Snackbar.make(coreView,"Unable to add "+names.get(position),Snackbar.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("fbid", ids.get(position));
                params.put("title", names.get(position));
                params.put("extraText", extras.get(position));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        Singleton.getInstance().getRequestQueue().add(request);
    }
}
