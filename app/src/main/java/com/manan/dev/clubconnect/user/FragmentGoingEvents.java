package com.manan.dev.clubconnect.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.manan.dev.clubconnect.Adapters.UserSingleEventListAdapter;
import com.manan.dev.clubconnect.Models.Event;
import com.manan.dev.clubconnect.Models.UserData;
import com.manan.dev.clubconnect.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yatindhingra on 14/01/18.
 */

public class FragmentGoingEvents extends Fragment {

    private UserSingleEventListAdapter GoingAdapter;
    private RecyclerView GoingEventsView;
    private ArrayList<Event> userGoingEvents;
    private ArrayList<String> userEvents;
    private DatabaseReference mUserGoingDatabaseReference;
    private DatabaseReference mEventGoingDatabaseReference;
    private ChildEventListener mUserGoingChildEventListener;
    private ChildEventListener mEventGoingChildEventListener;
    private Event curEvent;
    private UserData userGoing;
    Map<String, Event> GoingEvents;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_going_events, container, false);

        GoingEventsView = (RecyclerView) view.findViewById(R.id.going_recycler_view);
        userGoingEvents = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        GoingEventsView.setLayoutManager(layoutManager);

        GoingAdapter = new UserSingleEventListAdapter(userGoingEvents, getActivity());
        GoingEventsView.setAdapter(GoingAdapter);

        curEvent = new Event();
        userEvents = new ArrayList<>();
        userGoing = new UserData();
        GoingEvents = new HashMap<>();

        mUserGoingDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        mEventGoingDatabaseReference = FirebaseDatabase.getInstance().getReference().child("events");

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        detatchDatabaseListener();
        curEvent = null;
        GoingEvents.clear();
        userGoingEvents.clear();
        userEvents.clear();
    }

    private void detatchDatabaseListener() {
        if(mUserGoingChildEventListener != null){
            mUserGoingDatabaseReference.removeEventListener(mUserGoingChildEventListener);
            mUserGoingChildEventListener = null;
        }
        if(mEventGoingChildEventListener != null){
            mEventGoingDatabaseReference.removeEventListener(mEventGoingChildEventListener);
            mEventGoingChildEventListener = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        attachDatabaseListener();
    }

    private void attachDatabaseListener() {
        if(mUserGoingChildEventListener == null){
            mUserGoingChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try{
                        if(dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            userGoing = dataSnapshot.getValue(UserData.class);
                            Map<String, String> mp = userGoing.getGoing();
                            userEvents.addAll(mp.keySet());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    try{
                        if(dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            userGoing = dataSnapshot.getValue(UserData.class);
                            Map<String, String> mp = userGoing.getGoing();
                            userEvents.addAll(mp.keySet());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    try{
                        if(dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            userGoing = dataSnapshot.getValue(UserData.class);
                            Map<String, String> mp = userGoing.getGoing();
                            userEvents.addAll(mp.keySet());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mUserGoingDatabaseReference.addChildEventListener(mUserGoingChildEventListener);
        }
        if(mEventGoingChildEventListener == null){
            mEventGoingChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try{
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            curEvent = data.getValue(Event.class);
                            curEvent.setClubName(dataSnapshot.getKey());
                            curEvent.setEventId(data.getKey());
                            GoingEvents.put(data.getKey(), curEvent);
                        }
                        updateList();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    try{
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            curEvent = data.getValue(Event.class);
                            curEvent.setClubName(dataSnapshot.getKey());
                            curEvent.setEventId(data.getKey());
                            GoingEvents.put(data.getKey(), curEvent);
                        }
                        updateList();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    try{
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            curEvent = data.getValue(Event.class);
                            curEvent.setClubName(dataSnapshot.getKey());
                            curEvent.setEventId(data.getKey());
                            GoingEvents.put(data.getKey(), curEvent);
                        }
                        updateList();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mEventGoingDatabaseReference.addChildEventListener(mEventGoingChildEventListener);
        }
    }

    private void updateList() {
        userGoingEvents.clear();
        if(userEvents != null){
            //Toast.makeText(getActivity(), Integer.toString(userEvents.size()), Toast.LENGTH_SHORT).show();
            for(String id : userEvents){
                //Toast.makeText(getActivity(), id, Toast.LENGTH_SHORT).show();
                if(GoingEvents.containsKey(id)){
                    userGoingEvents.add(GoingEvents.get(id));
                }
            }
        }
        GoingAdapter.notifyDataSetChanged();
    }
}
