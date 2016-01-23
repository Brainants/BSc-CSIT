package com.techies.bsccsit.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devspark.robototextview.widget.RobotoTextView;
import com.techies.bsccsit.R;
import com.techies.bsccsit.activities.NoticeDetails;
import com.techies.bsccsit.advance.Singleton;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.CustomViewHolder> {

    LayoutInflater inflater;
    private ArrayList<String> mTitles = new ArrayList<>(),
            mShorts = new ArrayList<>(),
            mDetails = new ArrayList<>(),
            mDates = new ArrayList<>(),
            mAttachmentLinks = new ArrayList<>(),
            mAttachmentTitles = new ArrayList<>();
    private ArrayList<Integer> mIds = new ArrayList<>();
    private Context context;


    public NoticeAdapter(Context context, ArrayList<Integer> mIds, ArrayList<String> mTitles, ArrayList<String> mShorts, ArrayList<String> mDetails, ArrayList<String> mDates,
                         ArrayList<String> mAttachmentLinks, ArrayList<String> mAttachmentTitles) {
        this.context = context;
        this.mIds = mIds;
        this.mTitles = mTitles;
        this.mShorts = mShorts;
        this.mDetails = mDetails;
        this.mDates = mDates;
        this.mAttachmentLinks = mAttachmentLinks;
        this.mAttachmentTitles = mAttachmentTitles;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(inflater.inflate(R.layout.each_notice, parent, false));
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.title.setText(mTitles.get(position));
        holder.shortDesc.setText(mShorts.get(position));
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
                    .putExtra("noticeTitle", mTitles.get(getAdapterPosition()))
                    .putExtra("noticeDetail", mDetails.get(getAdapterPosition()))
                    .putExtra("noticeDate", mDates.get(getAdapterPosition()))
                    .putExtra("noticeAttachmentLink", mAttachmentLinks.get(getAdapterPosition()))
                    .putExtra("noticeAttachmentTitle", mAttachmentTitles.get(getAdapterPosition()));
            context.startActivity(intent);

        }
    }
}