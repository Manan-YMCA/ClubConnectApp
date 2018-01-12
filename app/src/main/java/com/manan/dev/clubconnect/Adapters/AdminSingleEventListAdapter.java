package com.manan.dev.clubconnect.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.manan.dev.clubconnect.EditEventActivity;
import com.manan.dev.clubconnect.Models.Coordinator;
import com.manan.dev.clubconnect.Models.Event;
import com.manan.dev.clubconnect.R;
import com.manan.dev.clubconnect.user.EventsDetailsActivity;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yatindhingra on 11/01/18.
 */

public class AdminSingleEventListAdapter extends RecyclerView.Adapter<AdminSingleEventListAdapter.ViewHolder> {

    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();


    ArrayList<Event> userSingleEventLists = new ArrayList<>();
    ArrayList<Coordinator> coordinatorsAll = new ArrayList<>();
    Context context;

    public AdminSingleEventListAdapter(ArrayList<Event> userSingleEventLists, ArrayList<Coordinator> coordinatorsAll, Context context) {
        this.userSingleEventLists = userSingleEventLists;
        this.coordinatorsAll = coordinatorsAll;
        this.context = context;
    }

    @Override
    public AdminSingleEventListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater l = LayoutInflater.from(context);
        View v = l.inflate(R.layout.admin_single_event_list, parent, false);
        AdminSingleEventListAdapter.ViewHolder vh = new AdminSingleEventListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(AdminSingleEventListAdapter.ViewHolder holder, int position) {
        final Event u = userSingleEventLists.get(position);
        holder.posterAdminClubEvent.setColorFilter(Color.rgb(0xae, 0xd0, 0xff), PorterDuff.Mode.MULTIPLY);
        Picasso.with(context).load(u.getPhotoID().getPosters().get(0)).into(holder.posterAdminClubEvent);

        String timeDisplay, dateDisplay;
        SimpleDateFormat sdf1, sdf2;

//        long try1 = u.getDays().get(0).getStartTime();
//        Toast.makeText(context,Long.toString(try1),Toast.LENGTH_SHORT).show();

        cal1.setTimeInMillis(u.getDays().get(0).getDate());
        sdf1 = new SimpleDateFormat("EEEE, dd MMM");
        dateDisplay = sdf1.format(cal1.getTime());
        holder.dateAdminEventClubList.setText(dateDisplay);

        cal2.setTimeInMillis(u.getDays().get(0).getStartTime());
        sdf2 = new SimpleDateFormat("HH:mm");
        timeDisplay = sdf2.format(cal2.getTime());
        holder.timeAdminEventClubList.setText(timeDisplay);

        holder.eventNameAdminClubEventList.setText(u.getEventName());

        holder.editCurrEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singleEventDetailIntent = new Intent(context, EditEventActivity.class);
                Bundle singleEventDetailBundle = new Bundle();
                //setting event id
                singleEventDetailBundle.putString(EditEventActivity.REQ_PARA_EVENT_ID, u.getEventId());
                //setting event name
                singleEventDetailBundle.putString(EditEventActivity.REQ_PARA_EVENT_NAME, u.getEventName());
                //setting event description
                singleEventDetailBundle.putString(EditEventActivity.REQ_PARA_EVENT_DETAILS, u.getEventDesc());
                //setting event venue
                singleEventDetailBundle.putString(EditEventActivity.REQ_PARA_EVENT_VENUE, u.getEventVenue());
                //setting event posters
                singleEventDetailBundle.putStringArrayList(EditEventActivity.REQ_PARA_EVENT_POSTERS, u.getPhotoID().getPosters());

                //setting event days
                long[] days = new long[u.getDays().size()];
                long[] sTime = new long[u.getDays().size()];
                long[] eTime = new long[u.getDays().size()];
                for(int i = 0; i < u.getDays().size(); i++){
                    days[i] = u.getDays().get(i).getDate();
                    sTime[i] = u.getDays().get(i).getStartTime();
                    eTime[i] = u.getDays().get(i).getEndTime();
                }
                singleEventDetailBundle.putLongArray(EditEventActivity.REQ_PARA_EVENT_DATE, days);
                singleEventDetailBundle.putLongArray(EditEventActivity.REQ_PARA_EVENT_STIME, sTime);
                singleEventDetailBundle.putLongArray(EditEventActivity.REQ_PARA_EVENT_ETIME, eTime);

                //setting event Coordinators

                ArrayList<String> cName = new ArrayList<>();
                ArrayList<String> cEmail = new ArrayList<>();
                ArrayList<String> cPhone = new ArrayList<>();
                ArrayList<String> cPhoto = new ArrayList<>();
                Map<String, Coordinator> cordinatorsDetails = new HashMap<>();
                for(int i = 0; i < coordinatorsAll.size(); i++){
                    cordinatorsDetails.put(coordinatorsAll.get(i).getEmail(), coordinatorsAll.get(i));
                }
                for(String coordEmailId : u.getCoordinatorID()){
                    Coordinator coordinator = cordinatorsDetails.get(coordEmailId);
                    if(coordinator == null)
                        continue;
                    cName.add(coordinator.getName());
                    cEmail.add(coordinator.getEmail());
                    cPhone.add(coordinator.getPhone());
                    cPhoto.add(coordinator.getPhoto());
                }
                singleEventDetailBundle.putStringArrayList(EditEventActivity.REQ_PARA_EVENT_COORD_NAME, cName);
                singleEventDetailBundle.putStringArrayList(EditEventActivity.REQ_PARA_EVENT_COORD_EMAIL, cEmail);
                singleEventDetailBundle.putStringArrayList(EditEventActivity.REQ_PARA_EVENT_COORD_PHONE, cPhone);
                singleEventDetailBundle.putStringArrayList(EditEventActivity.REQ_PARA_EVENT_COORD_PHOTO, cPhoto);

                singleEventDetailIntent.putExtras(singleEventDetailBundle);
                context.startActivity(singleEventDetailIntent);
            }
        });

        holder.deleteCurrEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("events").child(u.clubName).child(u.eventId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, "event deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(context, "event deletion unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != userSingleEventLists ? userSingleEventLists.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView posterAdminClubEvent, deleteCurrEvent, editCurrEvent;
        TextView dateAdminEventClubList, timeAdminEventClubList;
        CardView AdminClubEventListCardView;
        TextView eventNameAdminClubEventList;

        public ViewHolder(View itemView) {
            super(itemView);
            AdminClubEventListCardView = (CardView) itemView.findViewById(R.id.cv_admin_single_event_list);
            posterAdminClubEvent = (ImageView) itemView.findViewById(R.id.iv_poster_club_event);
            deleteCurrEvent = (ImageView) itemView.findViewById(R.id.iv_delete_event);
            editCurrEvent = (ImageView) itemView.findViewById(R.id.iv_edit_event);
            dateAdminEventClubList = (TextView) itemView.findViewById(R.id.tv_date_club_event);
            timeAdminEventClubList = (TextView) itemView.findViewById(R.id.tv_time_club_event);
            eventNameAdminClubEventList = (TextView) itemView.findViewById(R.id.tv_event_name);
        }
    }
}
