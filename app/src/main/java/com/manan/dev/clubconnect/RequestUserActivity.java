package com.manan.dev.clubconnect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.manan.dev.clubconnect.Adapters.RequestUserViewAdapter;
import com.manan.dev.clubconnect.Models.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestUserActivity extends AppCompatActivity {


    private RequestUserViewAdapter requestListRecyclerAdapter;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private DatabaseReference mDBRefCurEvent;
    private ChildEventListener mChildEventListCurEvent;

    private ArrayList<UserData> userIdArrayList;
    private ArrayList<String> userIdList;
    private Map<String, UserData> allUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_user);

        userIdArrayList = new ArrayList<>();
        userIdList = new ArrayList<>();
        allUsers = new HashMap<>();

        RecyclerView requestRecyclerView = findViewById(R.id.request_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        requestRecyclerView.setLayoutManager(mLayoutManager);
        requestListRecyclerAdapter = new RequestUserViewAdapter(userIdArrayList, this);
        requestRecyclerView.setAdapter(requestListRecyclerAdapter);


        String clubName = getIntent().getStringExtra("name");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("notification").child(clubName);
        mDBRefCurEvent = FirebaseDatabase.getInstance().getReference().child("users");


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                RequestUserActivity.this.finish();
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
        detachDatabaseListener();
        userIdArrayList.clear();
        userIdList.clear();
        allUsers.clear();
    }

    private void detachDatabaseListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
        if (mChildEventListCurEvent != null) {
            mDBRefCurEvent.removeEventListener(mChildEventListCurEvent);
            mChildEventListCurEvent = null;
        }
    }


    private void attachDatabaseListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {
                        String refValue = dataSnapshot.getValue(String.class);
                        userIdList.add(refValue);
                        modifyUserIdArrayList();
                        requestListRecyclerAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    try {
                        userIdList.remove(dataSnapshot.getValue(String.class));
                        modifyUserIdArrayList();
                        requestListRecyclerAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
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
        if (mChildEventListCurEvent == null) {
            mChildEventListCurEvent = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {
                        UserData u = dataSnapshot.getValue(UserData.class);
                        if (u != null)
                            allUsers.put(dataSnapshot.getKey(), u);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    modifyUserIdArrayList();
                    requestListRecyclerAdapter.notifyDataSetChanged();

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    try {
                        UserData u = dataSnapshot.getValue(UserData.class);
                        if (u != null)
                            allUsers.put(dataSnapshot.getKey(), u);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    modifyUserIdArrayList();
                    requestListRecyclerAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    try {
                        allUsers.remove(dataSnapshot.getKey());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    modifyUserIdArrayList();
                    requestListRecyclerAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mDBRefCurEvent.addChildEventListener(mChildEventListCurEvent);
        }


    }

    private void modifyUserIdArrayList() {
        userIdArrayList.clear();
        for (String key : userIdList)
            if (allUsers.containsKey(key))
                userIdArrayList.add(allUsers.get(key));
    }
}