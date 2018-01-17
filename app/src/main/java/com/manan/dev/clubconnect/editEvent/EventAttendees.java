package com.manan.dev.clubconnect.editEvent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.manan.dev.clubconnect.Adapters.AdminAttendeesAdapter;
import com.manan.dev.clubconnect.EditEventActivity;
import com.manan.dev.clubconnect.Models.UserData;
import com.manan.dev.clubconnect.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EventAttendees extends AppCompatActivity {

    private AdminAttendeesAdapter adapter;
    private ArrayList<UserData> userData;
    private ArrayList<String> userID;
    private Map<String, UserData> userMap;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private RecyclerView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_attendees);

        userID = getIntent().getStringArrayListExtra(EditEventActivity.REQ_PARA_EVENT_ATTENDEES);
        userData = new ArrayList<>();
        userMap = new HashMap<>();
        //Toast.makeText(EventAttendees.this, userID.get(0), Toast.LENGTH_SHORT).show();

        cardView = findViewById(R.id.attendees_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        cardView.setLayoutManager(mLayoutManager);
        adapter = new AdminAttendeesAdapter(EventAttendees.this.getApplicationContext(), userData);
        cardView.setAdapter(adapter);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");

    }

    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseListener();
    }


    @Override
    protected void onPause() {
        super.onPause();
        detatchDatabaseListener();
        userData.clear();
        userMap.clear();
    }

    private void detatchDatabaseListener() {
        if(mChildEventListener != null){
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }


    private void attachDatabaseListener() {
        if(mChildEventListener == null){
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {
                        UserData data = dataSnapshot.getValue(UserData.class);
                        data.UID = dataSnapshot.getKey();
                        userMap.put(dataSnapshot.getKey(), data);
                        updateList();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    try {
                        UserData data = dataSnapshot.getValue(UserData.class);
                        data.UID = dataSnapshot.getKey();
                        userMap.put(dataSnapshot.getKey(), data);
                        updateList();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    try {
                        UserData data = dataSnapshot.getValue(UserData.class);
                        data.UID = dataSnapshot.getKey();
                        userMap.put(dataSnapshot.getKey(), data);
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
            mDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void updateList() {
        userData.clear();
        if(userMap != null){
            for(String key : userID) {
                if (userMap.containsKey(key)) {
                    userData.add(userMap.get(key));
                }
            }

            Toast.makeText(EventAttendees.this, Integer.toString(userData.size()) + " " + userData.get(0).UID, Toast.LENGTH_SHORT).show();

            Set<String> mp = userMap.keySet();

            Log.d("tagger", Integer.toString(mp.size()));
            for(String item : mp){
                Log.d("tagger", userMap.get(item).UID);
            }
        }

        adapter.notifyDataSetChanged();

    }
}
