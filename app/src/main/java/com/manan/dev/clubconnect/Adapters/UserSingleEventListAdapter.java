package com.manan.dev.clubconnect.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.manan.dev.clubconnect.Models.UserSingleEventList;
import com.manan.dev.clubconnect.R;

import java.util.ArrayList;

/**
 * Created by Shubham on 11/15/2017.
 */

public class UserSingleEventListAdapter extends RecyclerView.Adapter<UserSingleEventListAdapter.ViewHolder>{

    ArrayList<UserSingleEventList> userSingleEventLists = new ArrayList<>();
    Context c;

    public UserSingleEventListAdapter(ArrayList<UserSingleEventList> userSingleEventLists, Context c) {
        this.userSingleEventLists = userSingleEventLists;
        this.c = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater l = LayoutInflater.from(c);
        View v = l.inflate(R.layout.activity_user_club_event_list,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        UserSingleEventList u = userSingleEventLists.get(position);
            

    }

    @Override
    public int getItemCount() {
        return userSingleEventLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView posterUserEventClubList;
        TextView dateUserEventClubList,timeUserEventClubList;

        public ViewHolder(View itemView) {
            super(itemView);

            posterUserEventClubList = (ImageView) itemView.findViewById(R.id.poster_club_event);
            dateUserEventClubList = (TextView) itemView.findViewById(R.id.date_club_event);
            timeUserEventClubList = (TextView) itemView.findViewById(R.id.time_club_event);
        }
    }
}
