package com.techies.bsccsit.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.devspark.robototextview.widget.RobotoTextView;
import com.techies.bsccsit.R;
import com.techies.bsccsit.activities.EachProject;
import com.techies.bsccsit.activities.EachProjectAdmin;
import com.techies.bsccsit.advance.MyApp;
import com.techies.bsccsit.advance.Singleton;

import java.util.ArrayList;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.VH> {
    private final Context context;
    private final ArrayList<String> user;
    private LayoutInflater inflater;
    private ArrayList<String> projectIds;
    private ArrayList<String> titles;
    private ArrayList<String> tags;
    private ArrayList<String> detail;

    public ProjectAdapter(Context context, ArrayList<String> projectIds,
                          ArrayList<String> titles,
                          ArrayList<String> user,
                          ArrayList<String> tags,
                          ArrayList<String> detail) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.projectIds = projectIds;
        this.titles = titles;
        this.tags = tags;
        this.user = user;
        this.detail = detail;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(inflater.inflate(R.layout.each_project, parent, false));
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {
        holder.title.setText(titles.get(position));
        holder.detail.setText(detail.get(position));
        String[] tagArray = tags.get(position).split("\\s*,\\s*");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(4, 0, 4, 0);
        holder.tagsHolder.removeAllViews();
        for (String tag : tagArray) {
            holder.tagsHolder.addView(Singleton.getTagView(context, tag), params);
        }
        holder.core.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.get(position).equals(MyApp.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE).getString("UserID", "")))
                    context.startActivity(new Intent(context, EachProjectAdmin.class)
                            .putExtra("project_id", projectIds.get(position)));
                else
                    context.startActivity(new Intent(context, EachProject.class)
                            .putExtra("project_id", projectIds.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return projectIds.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        RobotoTextView title, detail;
        LinearLayout tagsHolder;
        RelativeLayout core;

        public VH(View itemView) {
            super(itemView);
            title = (RobotoTextView) itemView.findViewById(R.id.projectName);
            detail = (RobotoTextView) itemView.findViewById(R.id.projectDetail);
            tagsHolder = (LinearLayout) itemView.findViewById(R.id.tagsHoldr);
            core = (RelativeLayout) itemView;
        }
    }
}