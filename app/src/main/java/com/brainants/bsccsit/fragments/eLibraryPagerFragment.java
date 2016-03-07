package com.brainants.bsccsit.fragments;


import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.brainants.bsccsit.R;
import com.brainants.bsccsit.adapters.eLibraryAdapter;
import com.brainants.bsccsit.advance.Singleton;
import com.devspark.robototextview.widget.RobotoTextView;

import java.io.File;
import java.util.ArrayList;

public class eLibraryPagerFragment extends Fragment {

    private ArrayList<String> Title = new ArrayList<>(),
            Source = new ArrayList<>(),
            Link = new ArrayList<>(),
            FileName = new ArrayList<>();
    private RecyclerView recy;
    private String[] types = {"syllabus", "notes", "old_question", "solutions"};
    private View core;
    private RobotoTextView nofilesMsg;


    public eLibraryPagerFragment() {
        // Required empty public constructor
    }

    public static eLibraryPagerFragment newInstance(int page) {
        eLibraryPagerFragment fragmentFirst = new eLibraryPagerFragment();
        Bundle args = new Bundle();
        args.putInt("position", page);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        core = view;
        nofilesMsg = (RobotoTextView) view.findViewById(R.id.nofilesMsg);
        nofilesMsg.setText(Html.fromHtml("No files found.<br>Mail us at <font color=#FFC107>info@brainants.com</font> to add files."));
        recy = (RecyclerView) view.findViewById(R.id.recyELibrary);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nofilesMsg.setVisibility(View.GONE);
        filFromDatabase(types[getArguments().getInt("position")]);
    }

    private void filFromDatabase(String type) {
        Cursor cursor = Singleton.getInstance().getDatabase().rawQuery("SELECT * FROM eLibrary WHERE Tag = '" + type + "';", null);
        while (cursor.moveToNext()) {
            Title.add(cursor.getString(cursor.getColumnIndex("Title")));
            Source.add(cursor.getString(cursor.getColumnIndex("Source")));
            Link.add(cursor.getString(cursor.getColumnIndex("Link")));
            FileName.add(cursor.getString(cursor.getColumnIndex("FileName")));
        }
        fillAdapter();
        cursor.close();
    }

    private void fillAdapter() {
        recy.setLayoutManager(new GridLayoutManager(getActivity(), Singleton.getSpanCount(getContext())));
        eLibraryAdapter adapter = new eLibraryAdapter(getActivity(), types[getArguments().getInt("position")], Title, Source, FileName);
        recy.setAdapter(adapter);
        if (Title.size() == 0)
            nofilesMsg.setVisibility(View.VISIBLE);
        adapter.setOnCLickListener(new eLibraryAdapter.ClickListener() {
            @Override
            public void onIconClick(View view, int position) {
                if (eLibraryAdapter.checkExistance(types[getArguments().getInt("position")], FileName.get(position))) {
                    if (!Singleton.isPermissionGiven()) {
                        Singleton.requestPermission((AppCompatActivity) getActivity());
                        return;
                    }
                    File file = new File(Environment.getExternalStorageDirectory() + "/" + Singleton.getSemester() + "/" + types[getArguments().getInt("position")] +
                            "/" + FileName.get(position));
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        Snackbar.make(core.findViewById(R.id.coreLibrary), "No reader found.", Snackbar.LENGTH_LONG).setAction("Download", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String appPackageName = "com.adobe.reader";
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            }
                        }).show();
                    }

                } else {
                    String url = Link.get(position);
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setDescription("Source: " + Source.get(position));
                    request.setTitle(FileName.get(position));

                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalFilesDir(getActivity(), Singleton.getSemester() + "/" + types[getArguments().getInt("position")],
                            FileName.get(position));

                    DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                    manager.enqueue(request);

                    Snackbar.make(core.findViewById(R.id.coreLibrary), "Download is in Queue.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_e_library_pager, container, false);
    }

    public static class Receiver extends BroadcastReceiver {

        public Receiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Download Completed.", Toast.LENGTH_SHORT).show();
        }
    }
}

