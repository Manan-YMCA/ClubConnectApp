package com.manan.dev.clubconnect.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.manan.dev.clubconnect.Models.Event;
import com.manan.dev.clubconnect.R;
import com.manan.dev.clubconnect.User.EventsDetailsActivity;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Shubham on 11/15/2017.
 */

public class UserSingleEventListAdapter extends RecyclerView.Adapter<UserSingleEventListAdapter.ViewHolder>{

    public static final String CLUB_NAME = "ClubName";
    public static final String EVENT_ID = "EventId";
    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();


    ArrayList<Event> userSingleEventLists = new ArrayList<>();
    Context context;

    public UserSingleEventListAdapter(ArrayList<Event> userSingleEventLists, Context context) {
        this.userSingleEventLists = userSingleEventLists;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater l = LayoutInflater.from(context);
        View v = l.inflate(R.layout.user_club_single_event_list,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Event u = userSingleEventLists.get(position);

        holder.posterUserEventClubList.setColorFilter(Color.rgb(0xae,0xd0,0xff), PorterDuff.Mode.MULTIPLY);
        Picasso.with(context).load(u.getPhotoID().getPosters().get(0)).into(holder.posterUserEventClubList);

        String timeDisplay ,dateDisplay;
        SimpleDateFormat sdf1,sdf2;
//        long try1 = u.getDays().get(0).getStartTime();
//        Toast.makeText(context,Long.toString(try1),Toast.LENGTH_SHORT).show();
        cal1.setTimeInMillis(u.getDays().get(0).getDate());
        sdf1 = new SimpleDateFormat("EEEE, dd MMM");
        dateDisplay = sdf1.format(cal1.getTime());
        holder.dateUserEventClubList.setText(dateDisplay);

        cal2.setTimeInMillis(u.getDays().get(0).getStartTime());
        sdf2 = new SimpleDateFormat("HH:mm");
        timeDisplay = sdf2.format(cal2.getTime());
        holder.timeUserEventClubList.setText(timeDisplay);

        holder.eventNameUserClubEventList.setText(u.getEventName());

        holder.userClubEventListCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent singleEventDetailIntent = new Intent(context, EventsDetailsActivity.class);
                Bundle singleEventDetailBundle = new Bundle();
                singleEventDetailBundle.putString(CLUB_NAME,u.clubName);
                singleEventDetailBundle.putString(EVENT_ID,u.eventId);
                singleEventDetailIntent.putExtras(singleEventDetailBundle);
                context.startActivity(singleEventDetailIntent);

            }
        });

    }
    @Override
    public int getItemCount() {
        return (null != userSingleEventLists ? userSingleEventLists.size() : 0);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView posterUserEventClubList;
        TextView dateUserEventClubList,timeUserEventClubList;
        CardView userClubEventListCardView;
        TextView eventNameUserClubEventList;

        public ViewHolder(View itemView) {
            super(itemView);

            userClubEventListCardView = (CardView) itemView.findViewById(R.id.cv_single_event_list);
            posterUserEventClubList = (ImageView) itemView.findViewById(R.id.poster_club_event);
            dateUserEventClubList = (TextView) itemView.findViewById(R.id.date_club_event);
            timeUserEventClubList = (TextView) itemView.findViewById(R.id.time_club_event);
            eventNameUserClubEventList = (TextView) itemView.findViewById(R.id.tv_event_name);
        }
    }

}
