package com.manan.dev.clubconnect.Adapters;

import android.content.Context;
import android.content.Intent;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Shubham on 11/15/2017.
 */

public class UserSingleEventListAdapter extends RecyclerView.Adapter<UserSingleEventListAdapter.ViewHolder>{

    ArrayList<Event> userSingleEventLists = new ArrayList<>();
    Context context;
    Event event = new Event();

    public UserSingleEventListAdapter(ArrayList<Event> userSingleEventLists, Context context) {
        this.userSingleEventLists = userSingleEventLists;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater l = LayoutInflater.from(context);
        View v = l.inflate(R.layout.activity_user_club_event_list,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event u = userSingleEventLists.get(position);

        Picasso.with(context).load(u.getPhotoID().getPosters().get(0)).fit().into(holder.posterUserEventClubList);
        holder.timeUserEventClubList.setText(Long.toString(u.getDays().get(0).getStartTime()));
        holder.dateUserEventClubList.setText(Long.toString(u.getDays().get(0).getDate()));

        // Put a onClick on this to Intent it to User Single Event Des Activity

        holder.userClubEventListCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent singleEventDetailIntent = new Intent();
                Bundle singleEventDetailBundle = new Bundle();
                singleEventDetailBundle.putString("Club Name",event.clubName);
                singleEventDetailBundle.putString("Event ID",event.eventId);
                singleEventDetailIntent.putExtras(singleEventDetailBundle);

            }
        });

    }

    @Override
    public int getItemCount() {
        return userSingleEventLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView posterUserEventClubList;
        TextView dateUserEventClubList,timeUserEventClubList;
        CardView userClubEventListCardView;

        public ViewHolder(View itemView) {
            super(itemView);

            userClubEventListCardView = (CardView) itemView.findViewById(R.id.cv_single_event_list);
            posterUserEventClubList = (ImageView) itemView.findViewById(R.id.poster_club_event);
            dateUserEventClubList = (TextView) itemView.findViewById(R.id.date_club_event);
            timeUserEventClubList = (TextView) itemView.findViewById(R.id.time_club_event);
        }
    }

}
