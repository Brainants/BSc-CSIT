package com.techies.bsccsit.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.facebook.login.widget.ProfilePictureView;
import com.techies.bsccsit.R;
import com.techies.bsccsit.activities.FbPage;
import com.techies.bsccsit.advance.Singleton;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

public class FacebookSearchAdapter extends RecyclerView.Adapter<FacebookSearchAdapter.ViewHolder>{

    private final Context context;
    private LayoutInflater inflater;

    ArrayList<String> names=new ArrayList<>(),
            ids=new ArrayList<>(),
            extra=new ArrayList<>();
    ArrayList<Boolean> verified=new ArrayList<>();
    ClickListener clickListener;


    public FacebookSearchAdapter(Context context,ArrayList<String> names,ArrayList<String> extra,ArrayList<String> ids,ArrayList<Boolean> verified){
        this.context=context;
        this.names=names;
        this.ids=ids;
        this.extra=extra;
        this.verified=verified;

        inflater=LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.fb_search_each_item,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.profilePictureView.setProfileId(ids.get(position));
        holder.nameView.setText(names.get(position));

        if(verified.get(position))
            holder.isVerified.setVisibility(View.VISIBLE);
        else
            holder.isVerified.setVisibility(View.GONE);

            holder.extraDetail.setText(extra.get(position));
        if (Singleton.checkExist(ids.get(position))){
            holder.addToDB.setIconResource(R.drawable.cross);
            holder.addToDB.setBackgroundColor(ContextCompat.getColor(context,R.color.unfollowColor));
            holder.addToDB.setFocusBackgroundColor(ContextCompat.getColor(context,R.color.unfollowColorTrans));
        }else {
            holder.addToDB.setIconResource(R.drawable.plus);
            holder.addToDB.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimary));
            holder.addToDB.setFocusBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimaryTrans));
        }
    }

    public void setOnClickListener(ClickListener clickListener){
        this.clickListener=clickListener;
    }

    public interface ClickListener{
        void onClick(FancyButton view,int position);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ProfilePictureView profilePictureView;
        RobotoTextView nameView,extraDetail;
        ImageView isVerified;
        FancyButton addToDB;
        public ViewHolder(View itemView) {
            super(itemView);
            profilePictureView= (ProfilePictureView) itemView.findViewById(R.id.profileImage);
            nameView= (RobotoTextView) itemView.findViewById(R.id.nameSearch);
            isVerified= (ImageView) itemView.findViewById(R.id.isVerified);
            addToDB= (FancyButton) itemView.findViewById(R.id.viewProfile);
            extraDetail= (RobotoTextView) itemView.findViewById(R.id.extraDetail);
            addToDB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(addToDB,getAdapterPosition());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, FbPage.class)
                            .putExtra("id",ids.get(getAdapterPosition()))
                            .putExtra("name",names.get(getAdapterPosition())));
                }
            });
        }
    }
}
