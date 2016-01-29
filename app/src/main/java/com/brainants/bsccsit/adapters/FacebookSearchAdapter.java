package com.brainants.bsccsit.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devspark.robototextview.widget.RobotoTextView;
import com.squareup.picasso.Picasso;
import com.brainants.bsccsit.R;
import com.brainants.bsccsit.activities.FbPage;
import com.brainants.bsccsit.advance.Singleton;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

public class FacebookSearchAdapter extends RecyclerView.Adapter<FacebookSearchAdapter.ViewHolder>{

    private final Context context;
    private final String allOrmy;
    ArrayList<String> names=new ArrayList<>(),
            ids=new ArrayList<>(),
            extra=new ArrayList<>();
    ClickListener clickListener;
    private LayoutInflater inflater;


    public FacebookSearchAdapter(Context context,String appOrmy ,ArrayList<String> names,ArrayList<String> extra,ArrayList<String> ids){
        this.context=context;
        this.names=names;
        this.ids=ids;
        this.extra=extra;
        this.allOrmy=appOrmy;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.fb_search_each_item,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Picasso.with(context).load("https://graph.facebook.com/"+ids.get(position)+"/picture?type=large").into(holder.profilePictureView);
        holder.nameView.setText(names.get(position));

            holder.extraDetail.setText(extra.get(position));
        if ((allOrmy.equals("my") && Singleton.checkExistInFollowing(ids.get(position))) ||
                (allOrmy.equals("all") && Singleton.checkExistInPopular(ids.get(position))) ){
            holder.addToDB.setIconResource(R.drawable.cross);
            holder.addToDB.setText("Unfollow");
            holder.addToDB.setBackgroundColor(ContextCompat.getColor(context,R.color.unfollowColor));
            holder.addToDB.setFocusBackgroundColor(ContextCompat.getColor(context,R.color.unfollowColorTrans));
        }else {
            holder.addToDB.setIconResource(R.drawable.plus);
            holder.addToDB.setText("Follow");
            holder.addToDB.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimary));
            holder.addToDB.setFocusBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimaryTrans));
        }
    }

    public void setOnClickListener(ClickListener clickListener){
        this.clickListener=clickListener;
    }

    public void removeItem(int position) {
        names.remove(position);
        ids.remove(position);
        extra.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(String name,String id,String extr) {
        names.add(name);
        ids.add(id);
        extra.add(extr);
        notifyItemInserted(extra.size()-1);
    }
    public void removeBySearch(String id){
        for (int i=0;i<ids.size();i++){
            if (ids.get(i).equals(id))
                removeItem(i);
        }

    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public interface ClickListener {
        void onClick(FancyButton view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profilePictureView;
        RobotoTextView nameView,extraDetail;
        FancyButton addToDB;
        public ViewHolder(View itemView) {
            super(itemView);
            profilePictureView= (CircleImageView) itemView.findViewById(R.id.profileImage);
            nameView= (RobotoTextView) itemView.findViewById(R.id.nameSearch);
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
                            .putExtra("name",names.get(getAdapterPosition()))
                            .putExtra("details",extra.get(getAdapterPosition())));
                }
            });
        }
    }
}
