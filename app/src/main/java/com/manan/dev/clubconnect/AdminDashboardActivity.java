package com.manan.dev.clubconnect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.manan.dev.clubconnect.Adapters.AdminSingleEventListAdapter;
import com.manan.dev.clubconnect.Adapters.UserSingleEventListAdapter;
import com.manan.dev.clubconnect.Models.Coordinator;
import com.manan.dev.clubconnect.Models.Event;
import com.manan.dev.clubconnect.user.UserClubEventListActivity;

import java.io.IOException;
import java.util.ArrayList;

public class AdminDashboardActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String clubName;
    RecyclerView adminSingleEventRv;
    ArrayList<Event> userSingleEventListArrayList;
    AdminSingleEventListAdapter adminSingleEventListAdapter;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mCoordDatabaseReference;
    private ChildEventListener mChildEventListener;
    private ChildEventListener mCoordChildEventListener;
    private ArrayList<Coordinator> coordinatorsAll;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        mAuth = FirebaseAuth.getInstance();
        clubName = mAuth.getCurrentUser().getDisplayName();
        FirebaseMessaging.getInstance().subscribeToTopic(mAuth.getCurrentUser().getDisplayName());
        MovableFloatingActionButton addNewEventFab = findViewById(R.id.add_new_event_fab);
        //addNewEventFab.setBackgroundTintList(ColorStateList.valueOf(R.color.darkBlack));
        //.withAlpha(R.color.cardview_shadow_start_color));
        addNewEventFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, EditEventActivity.class));

            }
        });

        coordinatorsAll = new ArrayList<>();

        adminSingleEventRv = (RecyclerView) findViewById(R.id.rv_admin_club_list);

        //Add all the database from Firebase to the ArrayList here!!
        userSingleEventListArrayList = new ArrayList<>();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AdminDashboardActivity.this);
        adminSingleEventRv.setLayoutManager(layoutManager);

        adminSingleEventListAdapter = new AdminSingleEventListAdapter(userSingleEventListArrayList, coordinatorsAll, this);
        adminSingleEventRv.setAdapter(adminSingleEventListAdapter);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("events").child(clubName);
        mCoordDatabaseReference = FirebaseDatabase.getInstance().getReference().child("coordinators").child(clubName);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FirebaseInstanceId.getInstance().deleteInstanceId();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.action_request_notfiy:
                FirebaseUser usser = mAuth.getCurrentUser();
                if(usser==null) {
                    finish();
                    return true;
                }
                String cllubName = usser.getDisplayName();
                startActivity(new Intent(AdminDashboardActivity.this, ClubMembersRequest.class).putExtra("name",cllubName));
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
        coordinatorsAll.clear();
    }

    private void detatchDatabaseListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
        if(mCoordChildEventListener != null){
            mCoordDatabaseReference.removeEventListener(mCoordChildEventListener);
            mCoordChildEventListener = null;
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
                        if(obj.getAttendees() == null){
                            obj.setAttendees(new ArrayList<String>());
                        }
                        userSingleEventListArrayList.add(obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    adminSingleEventListAdapter = new AdminSingleEventListAdapter(userSingleEventListArrayList, coordinatorsAll, AdminDashboardActivity.this);
                    adminSingleEventRv.setAdapter(adminSingleEventListAdapter);

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

                    adminSingleEventListAdapter = new AdminSingleEventListAdapter(userSingleEventListArrayList, coordinatorsAll, AdminDashboardActivity.this);
                    adminSingleEventRv.setAdapter(adminSingleEventListAdapter);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    for (int i = 0; i < userSingleEventListArrayList.size(); i++)
                        if (userSingleEventListArrayList.get(i).getEventId().equals(dataSnapshot.getKey())) {
                            userSingleEventListArrayList.remove(i);
                            break;
                        }

                    adminSingleEventListAdapter = new AdminSingleEventListAdapter(userSingleEventListArrayList, coordinatorsAll, AdminDashboardActivity.this);
                    adminSingleEventRv.setAdapter(adminSingleEventListAdapter);
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

        if(mCoordChildEventListener == null){
            mCoordChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {
                        coordinatorsAll.add(dataSnapshot.getValue(Coordinator.class));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    try {

                        Coordinator coodinator = dataSnapshot.getValue(Coordinator.class);
                        for (Coordinator c : coordinatorsAll) {
                            assert coodinator != null;
                            if (c.getEmail().equals(coodinator.getEmail())) {
                                coordinatorsAll.remove(c);
                                break;
                            }
                        }
                        coordinatorsAll.add(coodinator);
                    } catch (Exception e) {
                        e.printStackTrace();
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
            mCoordDatabaseReference.addChildEventListener(mCoordChildEventListener);
        }
    }
}
