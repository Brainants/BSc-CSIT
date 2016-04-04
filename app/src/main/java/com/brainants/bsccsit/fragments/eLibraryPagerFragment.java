package com.brainants.bsccsit.fragments;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.brainants.bsccsit.R;
import com.brainants.bsccsit.activities.MainActivity;
import com.brainants.bsccsit.adapters.eLibraryAdapter;
import com.brainants.bsccsit.advance.Singleton;
import com.devspark.robototextview.widget.RobotoTextView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

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
    eLibraryAdapter adapter;


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
        adapter = new eLibraryAdapter(getActivity(), types[getArguments().getInt("position")], Title, Source, FileName);
        recy.setAdapter(adapter);
        if (Title.size() == 0)
            nofilesMsg.setVisibility(View.VISIBLE);
        adapter.setOnCLickListener(new eLibraryAdapter.ClickListener() {
            @Override
            public void onIconClick(final View view, final int position) {
                new TedPermission(getActivity())
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                onClickHandler(view, position);
                            }

                            @Override
                            public void onPermissionDenied(ArrayList<String> arrayList) {
                                Snackbar.make(MainActivity.coordinatorLayout, "Unable to get the permission.", Snackbar.LENGTH_SHORT).show();
                            }
                        })
                        .setDeniedMessage("It seems that you rejected the permission request.\n\nPlease turn on Storage permissions from settings to proceed.")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }
        });
    }

    public void onClickHandler(final View view, final int position) {

        if (eLibraryAdapter.downloadStatus(FileName.get(position)) == 100) {

            File file = new File(Environment.getExternalStorageDirectory() + "/BSc CSIT/" + Singleton.getSemester() + "/" + types[getArguments().getInt("position")] +
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
        } else if (eLibraryAdapter.downloadStatus(FileName.get(position)) > 0) {
            FileDownloader.getImpl().pause(Singleton.getInstance().getDownloadID(FileName.get(position)));
        } else {
            final String url = Link.get(position);
            int downloadId = FileDownloader
                    .getImpl()
                    .create(url)
                    .setPath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Bsc CSIT/" + Singleton.getSemester() + "/" + types[getArguments().getInt("position")] + "/" + FileName.get(position))
                    .setListener(new FileDownloadListener() {

                        @Override
                        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                        }

                        @Override
                        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {

                        }

                        @Override
                        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressDownload);
                            progressBar.setMax(totalBytes);
                            progressBar.setProgress(soFarBytes);
                            Log.d("Debug", totalBytes + " " + soFarBytes);

                            eLibraryAdapter.setDownloadStatus(FileName.get(position), (int) ((float) soFarBytes / (float) totalBytes));
                        }

                        @Override
                        protected void blockComplete(BaseDownloadTask task) {
                        }

                        @Override
                        protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                        }

                        @Override
                        protected void completed(BaseDownloadTask task) {
                            adapter.notifyItemChanged(position);
                        }

                        @Override
                        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        }

                        @Override
                        protected void error(BaseDownloadTask task, Throwable e) {
                        }

                        @Override
                        protected void warn(BaseDownloadTask task) {
                        }
                    }).start();
            Singleton.getInstance().setDownloadId(downloadId, FileName.get(position));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_e_library_pager, container, false);
    }
}

