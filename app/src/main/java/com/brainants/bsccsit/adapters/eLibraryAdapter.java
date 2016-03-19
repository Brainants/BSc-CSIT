package com.brainants.bsccsit.adapters;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.brainants.bsccsit.R;
import com.brainants.bsccsit.advance.Singleton;
import com.devspark.robototextview.widget.RobotoTextView;

import java.io.File;
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
        if (eLibraryAdapter.checkExistance(fileType, fileName.get(position))) {
            holder.icon.setImageResource(R.drawable.open);
        } else
            holder.icon.setImageResource(R.drawable.download);
    }

    public static boolean checkExistance(String fileType, String fileName) {
        File file = new File(Environment.getExternalStorageDirectory() + "/BSc CSIT/" + Singleton.getSemester() + "/" + fileType + "/" + fileName);
        if (!file.exists()) {
            file.mkdir();
        }
        return file.exists();
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

        public VH(final View itemView) {
            super(itemView);
            name = (RobotoTextView) itemView.findViewById(R.id.fileTitle);
            source = (RobotoTextView) itemView.findViewById(R.id.fileSource);
            icon = (ImageView) itemView.findViewById(R.id.fileImage);
            coreFile = (RelativeLayout) itemView.findViewById(R.id.coreFile);

            coreFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onIconClick(itemView, getAdapterPosition());
                }
            });

        }
    }
}
