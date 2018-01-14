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

    private UserSingleEventListAdapter adapter;
    private RecyclerView interstedEventsView;
    private ArrayList<Event> userInterestedEvents;
    private ArrayList<String> userEvents;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mEventDatabaseReference;
    private ChildEventListener mUserChildEventListener;
    private ChildEventListener mEventChildEventListener;
    private Event curEvent;
    private UserData user;
    Map<String, Event> intEvents;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_going_events, container, false);

        interstedEventsView = (RecyclerView) view.findViewById(R.id.going_recycler_view);
        userInterestedEvents = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        interstedEventsView.setLayoutManager(layoutManager);

        adapter = new UserSingleEventListAdapter(userInterestedEvents, getActivity());
        interstedEventsView.setAdapter(adapter);

        curEvent = new Event();
        userEvents = new ArrayList<>();
        user = new UserData();
        intEvents = new HashMap<>();

        mUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        mEventDatabaseReference = FirebaseDatabase.getInstance().getReference().child("events");

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        detatchDatabaseListener();
        curEvent = null;
        intEvents.clear();
        userInterestedEvents.clear();
        userEvents.clear();
    }

    private void detatchDatabaseListener() {
        if(mUserChildEventListener != null){
            mUserDatabaseReference.removeEventListener(mUserChildEventListener);
            mUserChildEventListener = null;
        }
        if(mEventChildEventListener != null){
            mEventDatabaseReference.removeEventListener(mEventChildEventListener);
            mEventChildEventListener = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        attachDatabaseListener();
    }

    private void attachDatabaseListener() {
        if(mUserChildEventListener == null){
            mUserChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try{
                        if(dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            user = dataSnapshot.getValue(UserData.class);
                            Map<String, String> mp = user.getGoing();
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
                            user = dataSnapshot.getValue(UserData.class);
                            Map<String, String> mp = user.getGoing();
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
                            user = dataSnapshot.getValue(UserData.class);
                            Map<String, String> mp = user.getGoing();
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
            mUserDatabaseReference.addChildEventListener(mUserChildEventListener);
        }
        if(mEventChildEventListener == null){
            mEventChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try{
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            curEvent = data.getValue(Event.class);
                            curEvent.setClubName(dataSnapshot.getKey());
                            curEvent.setEventId(data.getKey());
                            intEvents.put(data.getKey(), curEvent);
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
                            intEvents.put(data.getKey(), curEvent);
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
                            intEvents.put(data.getKey(), curEvent);
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
            mEventDatabaseReference.addChildEventListener(mEventChildEventListener);
        }
    }

    private void updateList() {
        userInterestedEvents.clear();
        if(userEvents != null){
            Toast.makeText(getActivity(), Integer.toString(userEvents.size()), Toast.LENGTH_SHORT).show();
            for(String id : userEvents){
                Toast.makeText(getActivity(), id, Toast.LENGTH_SHORT).show();
                if(intEvents.containsKey(id)){
                    userInterestedEvents.add(intEvents.get(id));
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
