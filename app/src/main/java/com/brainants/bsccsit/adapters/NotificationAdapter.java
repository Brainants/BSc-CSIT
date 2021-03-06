package com.brainants.bsccsit.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brainants.bsccsit.R;
import com.brainants.bsccsit.activities.Notification;
import com.devspark.robototextview.widget.RobotoTextView;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.CustomViewHolder> {

    LayoutInflater inflater;
    ArrayList<String> title = new ArrayList<>(),
            desc = new ArrayList<>(),
            link = new ArrayList<>();

    private Notification context;

    public NotificationAdapter(Notification context, ArrayList<String> title, ArrayList<String> desc, ArrayList<String> link) {
        inflater = LayoutInflater.from(context);
        this.title = title;
        this.desc = desc;
        this.link = link;
        this.context = context;

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(inflater.inflate(R.layout.notification_each_post, parent, false));
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {
        holder.notifTitle.setText(title.get(position));
        holder.notifDesc.setText(desc.get(position));
        holder.coreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link.get(position)));
                context.startActivity(intent);
                context.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return title.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        RobotoTextView notifTitle, notifDesc;
        CardView coreLayout;

        public CustomViewHolder(View itemView) {
            super(itemView);

            notifTitle = (RobotoTextView) itemView.findViewById(R.id.notifTitle);
            notifDesc = (RobotoTextView) itemView.findViewById(R.id.notifDescription);
            coreLayout = (CardView) itemView;
            itemView.setClickable(true);

        }
    }


}
