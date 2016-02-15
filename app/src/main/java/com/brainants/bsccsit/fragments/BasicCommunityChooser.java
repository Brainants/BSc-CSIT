package com.brainants.bsccsit.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.brainants.bsccsit.R;
import com.brainants.bsccsit.activities.MainActivity;
import com.brainants.bsccsit.adapters.FacebookSearchAdapter;
import com.brainants.bsccsit.advance.MyApp;
import com.brainants.bsccsit.advance.Singleton;
import com.brainants.bsccsit.networking.MyCommunitiesUploader;
import com.brainants.bsccsit.networking.PopularCommunitiesDownloader;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

public class BasicCommunityChooser extends Fragment {

    public static FacebookSearchAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private int following = Singleton.getFollowingArray().size() - 1;
    private MaterialDialog dialog;
    private ArrayList<String> names = new ArrayList<>(),
            extra = new ArrayList<>(),
            ids = new ArrayList<>();
    private LinearLayout error;
    private SharedPreferences.Editor editor;
    private View view;

    public BasicCommunityChooser() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        editor = getActivity().getSharedPreferences("loginInfo", Context.MODE_PRIVATE).edit();

        dialog = new MaterialDialog.Builder(getActivity())
                .content("Loading...")
                .progress(true, 0)
                .build();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.getContext().getSharedPreferences("community", Context.MODE_PRIVATE).edit().putBoolean("changedComm", true).apply();
                editor.putBoolean("loggedIn", true);
                editor.apply();

                final MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                        .progress(true, 0)
                        .content("Please wait...")
                        .build();
                dialog.show();

                MyCommunitiesUploader uploader = new MyCommunitiesUploader();
                uploader.doInBackground();
                uploader.setTaskCompleteListener(new MyCommunitiesUploader.OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(boolean success) {
                        dialog.dismiss();
                        if (success) {
                            Toast.makeText(getActivity(), "Welcome " + getActivity().getSharedPreferences("loginInfo", Context.MODE_PRIVATE).getString("FirstName", "") + "!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getContext(), MainActivity.class));
                            getActivity().finish();
                        } else {
                            Snackbar.make(view, "Unable to connect.", Snackbar.LENGTH_SHORT).setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    fab.callOnClick();
                                }
                            }).show();
                        }
                    }
                });

            }
        });
        fillFromDatabase();
    }

    private void fillFromDatabase() {
        int count = 0;
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM popularCommunities", null);
        while (cursor.moveToNext()) {
            count++;
            names.add(cursor.getString(cursor.getColumnIndex("Title")));
            extra.add(cursor.getString(cursor.getColumnIndex("ExtraText")));
            ids.add(cursor.getString(cursor.getColumnIndex("FbID")));
        }
        cursor.close();
        if (count == 0)
            downloadFromInternet();
        else
            fillRecyclerView();
    }

    private void downloadFromInternet() {
        dialog.show();
        PopularCommunitiesDownloader downloader = new PopularCommunitiesDownloader();
        downloader.doInBackground();
        downloader.setTaskCompleteListener(new PopularCommunitiesDownloader.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean success) {
                dialog.dismiss();
                if (success)
                    fillFromDatabase();
                else
                    error.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fillRecyclerView() {
        recyclerView.setVisibility(View.VISIBLE);
        adapter = new FacebookSearchAdapter(getActivity(), "my", names, extra, ids);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        adapter.setOnClickListener(new FacebookSearchAdapter.ClickListener() {
            @Override
            public void onClick(FancyButton view, int position) {
                if (Singleton.checkExistInFollowing(ids.get(position))) {
                    following--;
                    Singleton.getInstance().getDatabase().execSQL("DELETE FROM myCommunities WHERE FbID = " + ids.get(position));
                } else {
                    ContentValues values = new ContentValues();
                    values.put("Title", names.get(position));
                    values.put("FbID", ids.get(position));
                    following++;
                    values.put("ExtraText", extra.get(position));
                    Singleton.getInstance().getDatabase().insert("myCommunities", null, values);
                }
                finilizeFAB();
                adapter.notifyItemChanged(position);
            }
        });
    }

    private void finilizeFAB() {
        if (following >= 5)
            fab.show();
        else
            fab.hide();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.rectChooseCommu);
        fab = (FloatingActionButton) view.findViewById(R.id.doneRegButton);
        error = (LinearLayout) view.findViewById(R.id.errorMessageReg);
        fab.hide();
        error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFromInternet();
            }
        });
        this.view = view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_basic_community_chooser, container, false);
    }
}
