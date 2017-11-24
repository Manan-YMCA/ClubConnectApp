package com.manan.dev.clubconnect.User;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.manan.dev.clubconnect.Adapters.UserSingleEventListAdapter;
import com.manan.dev.clubconnect.Models.Event;
import com.manan.dev.clubconnect.R;

import java.util.ArrayList;

import static com.manan.dev.clubconnect.Adapters.UserSingleEventListAdapter.CLUB_NAME;

public class UserClubEventListActivity extends AppCompatActivity {

    RecyclerView userSingleEventListRV;
    ArrayList<Event> userSingleEventListArrayList;
    UserSingleEventListAdapter userSingleEventListAdapter;
    private DatabaseReference mDatabaseReference;
    String clubName;
    private ChildEventListener mChildEventListener;

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
        Log.d("Name",clubName);
//        Toast.makeText(this, clubName, Toast.LENGTH_SHORT).show();

        userSingleEventListRV = (RecyclerView) findViewById(R.id.rv_user_club_list);

        //Add all the database from Firebase to the ArrayList here!!
        userSingleEventListArrayList = new ArrayList<>();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        userSingleEventListRV.setLayoutManager(layoutManager);

        userSingleEventListAdapter = new UserSingleEventListAdapter(userSingleEventListArrayList, this);
        userSingleEventListRV.setAdapter(userSingleEventListAdapter);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("events").child(clubName);
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
    }

    private void detatchDatabaseListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
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
                        userSingleEventListArrayList.add(obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    userSingleEventListAdapter = new UserSingleEventListAdapter(userSingleEventListArrayList, UserClubEventListActivity.this);
                    userSingleEventListRV.setAdapter(userSingleEventListAdapter);

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    try {
                        Log.d("onAdded", dataSnapshot.toString());

                        Event obj = dataSnapshot.getValue(Event.class);
                        obj.setEventId(dataSnapshot.getKey());
                        obj.setClubName(clubName);

                        for (int i = 0; i < userSingleEventListArrayList.size(); i++)
                            if (userSingleEventListArrayList.get(i).getEventId().equals(dataSnapshot.getKey())) {
                                userSingleEventListArrayList.set(i, obj);
                                break;
                            }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    userSingleEventListAdapter = new UserSingleEventListAdapter(userSingleEventListArrayList, UserClubEventListActivity.this);
                    userSingleEventListRV.setAdapter(userSingleEventListAdapter);
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
    }

}
