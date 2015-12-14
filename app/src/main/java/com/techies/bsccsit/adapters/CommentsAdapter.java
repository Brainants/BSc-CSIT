package com.techies.bsccsit.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devspark.robototextview.widget.RobotoTextView;
import com.facebook.login.widget.ProfilePictureView;
import com.techies.bsccsit.R;
import com.techies.bsccsit.advance.Singleton;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.VH> {

    private View Header;
    private ArrayList<String> posterId, names,times, message;
    private LayoutInflater inflater;

    public CommentsAdapter(Context context, View Header, ArrayList<String> posterId,
                           ArrayList<String> names,ArrayList<String> times,ArrayList<String> message){
        this.Header=Header;
        this.posterId=posterId;
        this.names=names;
        this.times=times;
        this.message=message;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==0)
            return new VH(Header);
        else
            return new VH(inflater.inflate(R.layout.comment_each,parent,false));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.profilePictureView.setProfileId(posterId.get(position));
        holder.name.setText(names.get(position));
        holder.time.setText(Singleton.convertToSimpleDate(times.get(position)));
        holder.comment.setText(message.get(position));
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
        ProfilePictureView profilePictureView;
        public VH(View itemView) {
            super(itemView);
            name= (RobotoTextView) itemView.findViewById(R.id.commenter);
            time= (RobotoTextView) itemView.findViewById(R.id.time);
            comment= (RobotoTextView) itemView.findViewById(R.id.comment);
            profilePictureView= (ProfilePictureView) itemView.findViewById(R.id.profilePhoto);
        }
    }
}
