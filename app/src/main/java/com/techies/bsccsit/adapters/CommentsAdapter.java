package com.techies.bsccsit.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.facebook.login.widget.ProfilePictureView;
import com.squareup.picasso.Picasso;
import com.techies.bsccsit.R;
import com.techies.bsccsit.advance.Singleton;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.VH> {

    private View Header;
    private ArrayList<String> posterId, names,times, message;
    private LayoutInflater inflater;
    private Context context;
    private Bundle headerBundle;

    public CommentsAdapter(Context context, Bundle headerBundle, ArrayList<String> posterId,
                           ArrayList<String> names, ArrayList<String> times, ArrayList<String> message){
        this.posterId=posterId;
        this.names=names;
        this.context = context;
        this.headerBundle = headerBundle;
        this.times=times;
        this.message=message;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==0)
            return new VH(inflater.inflate(R.layout.news_each_post, parent, false));
        else
            return new VH(inflater.inflate(R.layout.comment_each,parent,false));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (position == 0) {
            holder.timePost.setText(Singleton.convertToSimpleDate(headerBundle.getString("time")));
            holder.namePost.setText(headerBundle.getString("name"));
            holder.messagePost.setText(headerBundle.getString("message"));
            holder.profilePictureView.setProfileId(headerBundle.getString("userId"));
            if (headerBundle.getString("imageURL").equals(""))
                holder.imageViewPost.setVisibility(View.GONE);
            else
                Picasso.with(context).load(headerBundle.getString("imageURL")).into(holder.imageViewPost);
        } else {
            position--;
            holder.name.setText(names.get(position));
            holder.time.setText(Singleton.convertToSimpleDate(times.get(position)));
            holder.comment.setText(message.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return names.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class VH extends RecyclerView.ViewHolder {
        RobotoTextView name,time,comment;

        TextView namePost, messagePost, timePost;
        ImageView imageViewPost;
        ProfilePictureView profilePictureView;


        public VH(View itemView) {
            super(itemView);
            name= (RobotoTextView) itemView.findViewById(R.id.commenter);
            time= (RobotoTextView) itemView.findViewById(R.id.time);
            comment= (RobotoTextView) itemView.findViewById(R.id.comment);

            namePost = (TextView) itemView.findViewById(R.id.nameOfPoster);
            timePost = (TextView) itemView.findViewById(R.id.timeOfPost);
            messagePost = (TextView) itemView.findViewById(R.id.messageOfPost);
            imageViewPost = (ImageView) itemView.findViewById(R.id.imageOfPost);
            profilePictureView = (ProfilePictureView) itemView.findViewById(R.id.imageOfPoster);

        }
    }
}
