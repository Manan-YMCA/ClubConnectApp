package com.manan.dev.clubconnect.user;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.manan.dev.clubconnect.Adapters.UserSingleEventListAdapter;
import com.manan.dev.clubconnect.Models.Event;
import com.manan.dev.clubconnect.Models.UserData;
import com.manan.dev.clubconnect.R;

import java.util.ArrayList;

import static com.manan.dev.clubconnect.Adapters.UserSingleEventListAdapter.CLUB_NAME;

public class UserClubEventListActivity extends AppCompatActivity {

    RecyclerView userSingleEventListRV;
    ArrayList<Event> userSingleEventListArrayList , allEvents;
    UserSingleEventListAdapter userSingleEventListAdapter;
    private DatabaseReference mDatabaseReference;
    String clubName;
    private ChildEventListener mChildEventListener;
    private ChildEventListener mprivateChildEventListener;
    private String userId;
    private UserData user;
    private DatabaseReference mPrivateDatabaseEvents;
    private ArrayList<String> myClubs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_club_event_list);

        clubName = "kachra";
        try {
            clubName = getIntent().getExtras().getString(CLUB_NAME, "kachra");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("Name", clubName);
//        Toast.makeText(this, clubName, Toast.LENGTH_SHORT).show();

        userSingleEventListRV = (RecyclerView) findViewById(R.id.rv_user_club_list);

        //Add all the database from Firebase to the ArrayList here!!
        userSingleEventListArrayList = new ArrayList<>();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        userSingleEventListRV.setLayoutManager(layoutManager);

        userSingleEventListAdapter = new UserSingleEventListAdapter(userSingleEventListArrayList, this);
        userSingleEventListRV.setAdapter(userSingleEventListAdapter);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("events").child(clubName);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user = new UserData();
        allEvents = new ArrayList<>();
        myClubs = new ArrayList<>();
        mPrivateDatabaseEvents = FirebaseDatabase.getInstance().getReference().child("users");


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                UserClubEventListActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        userSingleEventListArrayList.clear();
        allEvents.clear();
    }

    private void detatchDatabaseListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
        if(mprivateChildEventListener != null){
            mPrivateDatabaseEvents.removeEventListener(mprivateChildEventListener);
        }
    }

    private void attachDatabaseListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {
                        Log.d("onAdded", dataSnapshot.toString());
                        Event obj = dataSnapshot.getValue(Event.class);
                        obj.setEventId(dataSnapshot.getKey());
                        obj.setClubName(clubName);
                        allEvents.add(obj);
                        updateList();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    try {
                        //Log.d("onAdded", dataSnapshot.toString());
                        Event obj = dataSnapshot.getValue(Event.class);
                        obj.setEventId(dataSnapshot.getKey());
                        obj.setClubName(clubName);
                        allEvents.add(obj);
                        updateList();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    for (int i = 0; i < userSingleEventListArrayList.size(); i++)
                        if (userSingleEventListArrayList.get(i).getEventId().equals(dataSnapshot.getKey())) {
                            userSingleEventListArrayList.remove(i);
                            break;
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
        if(mprivateChildEventListener == null){
            mprivateChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getKey().equals(userId)) {
                        user = dataSnapshot.getValue(UserData.class);
                        if (user != null && user.getEmailId() != null) {
                            Log.d("user id", user.getEmailId());
                        } else
                            Log.d("user id", "null");
                        updateList();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getKey().equals(userId)) {
                        user = dataSnapshot.getValue(UserData.class);
                        if (user != null && user.getEmailId() != null) {
                            Log.d("user id", user.getEmailId());
                        } else
                            Log.d("user id", "null");
                        updateList();
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mPrivateDatabaseEvents.addChildEventListener(mprivateChildEventListener);
        }
    }

    private void updateList() {
        userSingleEventListArrayList.clear();
        if(user.getMyClubs() != null)
            myClubs.addAll(user.getMyClubs());

        for(Event ev : allEvents){
            if(ev.getPrivate()){
                if(myClubs.contains(ev.getClubName())){
                    userSingleEventListArrayList.add(ev);
                }
            } else if(!ev.getPrivate()){
                userSingleEventListArrayList.add(ev);
            }
        }
        userSingleEventListAdapter.notifyDataSetChanged();
    }

}
