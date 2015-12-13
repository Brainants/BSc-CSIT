package com.techies.bsccsit.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.squareup.picasso.Picasso;
import com.techies.bsccsit.R;
import com.techies.bsccsit.activities.FbEvent;
import com.techies.bsccsit.activities.FbPage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.VH> {
    private final Context context;
    LayoutInflater inflater;
    ArrayList<String> names, time, hoster, imageURL,eventsIds;

    public EventAdapter(Context context, ArrayList<String> names, ArrayList<String> eventsIds, ArrayList<String> time
            , ArrayList<String> hoster, ArrayList<String> imageURL) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.names = names;
        this.time = time;
        this.eventsIds=eventsIds;
        this.hoster = hoster;
        this.imageURL = imageURL;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(inflater.inflate(R.layout.events_each_post, parent, false));
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {
        holder.nameHolder.setText(names.get(position));

        holder.monthHolder.setText(getMonth(time.get(position)));
        holder.dayHolder.setText(getDay(time.get(position)));

        holder.hosterHolder.setText("Hosted by: " + hoster.get(position));

        if (imageURL.get(position).equals(""))
            holder.imageHolder.setVisibility(View.GONE);
        else {
            Picasso.with(context).load(imageURL.get(position)).into(holder.imageHolder);
            holder.imageHolder.setVisibility(View.VISIBLE);
        }
    }


    public String getMonth(String created_time) {
        Date date = convertToSimpleDate(created_time);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        switch (month) {
            case 0:
                return "JAN";
            case 1:
                return "FEB";
            case 2:
                return "MAR";
            case 3:
                return "APR";
            case 4:
                return "MAY";
            case 5:
                return "JUN";
            case 6:
                return "JUL";
            case 7:
                return "AUG";
            case 8:
                return "SEP";
            case 9:
                return "OCT";
            case 10:
                return "NOV";
            case 11:
                return "DEC";
            default:
                return "";
        }
    }

    public String getDay(String created_time) {
        Date date = convertToSimpleDate(created_time);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH) + "";

    }

    private Date convertToSimpleDate(String created_time) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZZZZZ", Locale.US);
        try {
            return simpleDateFormat.parse(created_time);
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public int getItemCount() {
        return names.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        RobotoTextView nameHolder, monthHolder, dayHolder, hosterHolder;
        ImageView imageHolder, addToSchedule;

        public VH(View itemView) {
            super(itemView);
            nameHolder = (RobotoTextView) itemView.findViewById(R.id.nameOfEvent);
            monthHolder = (RobotoTextView) itemView.findViewById(R.id.monthTxt);
            dayHolder = (RobotoTextView) itemView.findViewById(R.id.dayTxt);
            hosterHolder = (RobotoTextView) itemView.findViewById(R.id.eventHoster);
            addToSchedule = (ImageView) itemView.findViewById(R.id.imageOfPoster);
            imageHolder= (ImageView) itemView.findViewById(R.id.eventCoverImage);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, FbEvent.class)
                            .putExtra("eventID",eventsIds.get(getAdapterPosition()))
                            .putExtra("eventName",names.get(getAdapterPosition()))
                            .putExtra("imageURL",imageURL.get(getAdapterPosition()))
                            .putExtra("eventHost","Hosted By: "+ hoster.get(getAdapterPosition())));
                }
            });
        }
    }
}
