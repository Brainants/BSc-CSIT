package com.brainants.bsccsit.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brainants.bsccsit.R;
import com.brainants.bsccsit.activities.NoticeDetails;
import com.brainants.bsccsit.advance.Singleton;
import com.devspark.robototextview.widget.RobotoTextView;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.CustomViewHolder> {

    LayoutInflater inflater;
    private ArrayList<String> mTitles = new ArrayList<>(),
            mShorts = new ArrayList<>(),
            mDates = new ArrayList<>();
    private ArrayList<Integer> mIds = new ArrayList<>();
    private Context context;


    public NoticeAdapter(Context context, ArrayList<Integer> mIds, ArrayList<String> mTitles, ArrayList<String> mShorts, ArrayList<String> mDates) {
        this.context = context;
        this.mIds = mIds;
        this.mTitles = mTitles;
        this.mShorts = mShorts;
        this.mDates = mDates;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(inflater.inflate(R.layout.each_notice, parent, false));
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.title.setText(mTitles.get(position));
        holder.shortDesc.setText(Html.fromHtml(mShorts.get(position)));
        holder.date.setText(Singleton.convertDate(mDates.get(position)));
    }

    @Override
    public int getItemCount() {
        return mTitles.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RobotoTextView title, shortDesc, date;

        public CustomViewHolder(View itemView) {
            super(itemView);
            title = (RobotoTextView) itemView.findViewById(R.id.titleNotice);
            shortDesc = (RobotoTextView) itemView.findViewById(R.id.shortDesc);
            date = (RobotoTextView) itemView.findViewById(R.id.date);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, NoticeDetails.class)
                    .putExtra("notice_id", mIds.get(getAdapterPosition()));
            context.startActivity(intent);

        }
    }
}