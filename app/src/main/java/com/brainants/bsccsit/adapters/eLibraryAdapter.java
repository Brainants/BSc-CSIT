package com.brainants.bsccsit.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.brainants.bsccsit.R;
import com.brainants.bsccsit.advance.MyApp;
import com.devspark.robototextview.widget.RobotoTextView;

import java.util.ArrayList;

public class eLibraryAdapter extends RecyclerView.Adapter<eLibraryAdapter.VH> {

    private final Context context;
    private LayoutInflater inflater;
    private ArrayList<String> titles, source, fileName;
    ClickListener clickListener;
    private String fileType;

    public eLibraryAdapter(Context context, String fileType, ArrayList<String> titles,
                           ArrayList<String> source, ArrayList<String> fileName) {
        inflater = LayoutInflater.from(context);
        this.titles = titles;
        this.source = source;
        this.fileName = fileName;
        this.context = context;
        this.fileType = fileType;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(inflater.inflate(R.layout.elibrary_each_item, parent, false));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.name.setText(titles.get(position));
        holder.source.setText("Source: " + source.get(position));
        if (eLibraryAdapter.downloadStatus(fileName.get(position)) == 0) {
            holder.progressBar.setVisibility(View.GONE);
            holder.icon.setImageResource(R.drawable.download);
        } else if (eLibraryAdapter.downloadStatus(fileName.get(position)) < 100) {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.progressBar.setMax(100);
            holder.progressBar.setProgress(eLibraryAdapter.downloadStatus(fileName.get(position)));
        } else {
            holder.progressBar.setVisibility(View.GONE);
            holder.icon.setImageResource(R.drawable.open);
        }
    }

    public static int downloadStatus(String fileName) {
        return MyApp.getContext().getSharedPreferences("download", Context.MODE_PRIVATE).getInt(fileName, 0);
    }

    public static void setDownloadStatus(String fileName, int progress) {
        MyApp.getContext().getSharedPreferences("download", Context.MODE_PRIVATE).edit().putInt(fileName, progress).apply();
    }

    public void setOnCLickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onIconClick(View view, int position);
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        RobotoTextView name, source;
        ImageView icon;
        RelativeLayout coreFile;
        ProgressBar progressBar;

        public VH(final View itemView) {
            super(itemView);
            name = (RobotoTextView) itemView.findViewById(R.id.fileTitle);
            source = (RobotoTextView) itemView.findViewById(R.id.fileSource);
            icon = (ImageView) itemView.findViewById(R.id.fileImage);
            coreFile = (RelativeLayout) itemView.findViewById(R.id.coreFile);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressDownload);

            coreFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onIconClick(itemView, getAdapterPosition());
                }
            });

        }
    }
}
