package com.techies.bsccsit.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.techies.bsccsit.R;
import com.techies.bsccsit.activities.EachPost;
import com.techies.bsccsit.activities.ImageViewActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.VH>{
    private final Context context;
    LayoutInflater inflater;
    ArrayList<String> names, time, ids,postIds, message, imageURL;
    ArrayList<Integer> likes, comments;

    public ForumAdapter(Context context, ArrayList<String> names, ArrayList<String> time
            , ArrayList<String> ids, ArrayList<String> postIds, ArrayList<String> message, ArrayList<String> imageURL
            , ArrayList<Integer> likes, ArrayList<Integer> comments){
        inflater=LayoutInflater.from(context);
        this.context=context;
        this.names=names;
        this.time=time;
        this.ids=ids;
        this.message=message;
        this.imageURL=imageURL;
        this.likes=likes;
        this.postIds=postIds;
        this.comments=comments;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(inflater.inflate(R.layout.forum_each_post,parent,false));
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {
        holder.nameHolder.setText(names.get(position));
        holder.timeHolder.setText(time.get(position));
        holder.messageHolder.setText(message.get(position));
        holder.likesHolder.setText(likes.get(position).toString());
        holder.commentsHolder.setText(comments.get(position).toString());

        if(imageURL.get(position).equals(""))
            holder.imageHolder.setVisibility(View.GONE);
        else{
            Picasso.with(context).load(imageURL.get(position)).into(holder.imageHolder);
            holder.imageHolder.setVisibility(View.VISIBLE);
            holder.imageHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, ImageViewActivity.class)
                            .putExtra("ImageURL",imageURL.get(position))
                            .putExtra("like",likes.get(position)+"")
                            .putExtra("comment",comments.get(position)+"")
                            .putExtra("desc",message.get(position)));
                }
            });
        }

        Picasso.with(context).load("https://graph.facebook.com/"+ids.get(position)+"/picture?type=large").into(holder.profilePicHolder);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        TextView nameHolder, timeHolder, messageHolder, likesHolder, commentsHolder;
        ImageView imageHolder;
        CircleImageView profilePicHolder;

        public VH(View itemView) {
            super(itemView);
            nameHolder= (TextView) itemView.findViewById(R.id.nameOfPoster);
            timeHolder= (TextView) itemView.findViewById(R.id.timeOfPost);
            messageHolder= (TextView) itemView.findViewById(R.id.messageOfPost);
            likesHolder= (TextView) itemView.findViewById(R.id.likeOfPost);
            commentsHolder= (TextView) itemView.findViewById(R.id.commentOfPost);
            imageHolder= (ImageView) itemView.findViewById(R.id.imageOfPost);
            profilePicHolder= (CircleImageView) itemView.findViewById(R.id.imageOfPoster);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, EachPost.class).
                            putExtra("postID",postIds.get(getAdapterPosition())).
                            putExtra("userId",ids.get(getAdapterPosition())).
                            putExtra("message",message.get(getAdapterPosition())).
                            putExtra("imageURL",imageURL.get(getAdapterPosition())).
                            putExtra("name",names.get(getAdapterPosition())).
                            putExtra("time",time.get(getAdapterPosition())));
                }
            });
        }
    }
}
