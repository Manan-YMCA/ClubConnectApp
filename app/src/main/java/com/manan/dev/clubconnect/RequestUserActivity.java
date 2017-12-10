package com.manan.dev.clubconnect;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.manan.dev.clubconnect.Adapters.RequestUserViewAdapter;
import com.manan.dev.clubconnect.Adapters.UserSingleEventListAdapter;
import com.manan.dev.clubconnect.Models.Coordinator;
import com.manan.dev.clubconnect.Models.Event;
import com.manan.dev.clubconnect.Models.UserData;
import com.manan.dev.clubconnect.User.UserClubEventListActivity;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.manan.dev.clubconnect.Adapters.UserSingleEventListAdapter.CLUB_NAME;

public class RequestUserActivity extends AppCompatActivity {

    private StorageReference firebaseStorage;
    private String clubName;


    private RecyclerView requestRecyclerView;
    private RequestUserViewAdapter requestlistRecyclerAdapter;
    private ArrayList<UserData> userIdArrayList;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private DatabaseReference mDBRefCurEvent;
    private ChildEventListener mChildEventListCurEvent;
    private List<String> userIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firebaseStorage = FirebaseStorage.getInstance().getReference();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_user);


        clubName = getIntent().getStringExtra("name");

        Toast.makeText(this, clubName, Toast.LENGTH_SHORT).show();

        requestRecyclerView = (RecyclerView) findViewById(R.id.request_recycler_view);

        userIdArrayList = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        requestRecyclerView.setLayoutManager(mLayoutManager);

String photo="https://firebasestorage.googleapis.com/v0/b/club-connect-29c71.appspot.com/o/Designing%20Competition_0.1239154461?alt=media&token=3c172732-d314-4e58-b901-7182db7067ad";
        UserData dummy = new UserData("9911526283",null,null,null,photo,"shubham",null,null,null,null,0);

        userIdArrayList.add(dummy);
        userIdArrayList.add(dummy);

        requestlistRecyclerAdapter = new RequestUserViewAdapter(userIdArrayList, this);
        requestRecyclerView.setAdapter(requestlistRecyclerAdapter);

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
          //  Toast.makeText(this,"OnResume ",Toast.LENGTH_SHORT).show();

            attachDatabaseListener();
        }
    @Override
    protected void onPause() {
        super.onPause();
   //     Toast.makeText(this,"Onpause ",Toast.LENGTH_SHORT).show();
        detatchDatabaseListener();
        userIdArrayList.clear();
    }

    private void detatchDatabaseListener() {
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
       // Toast.makeText(this,"AttachData ",Toast.LENGTH_SHORT).show();

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                public String refValue;

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {


                        refValue= dataSnapshot.getValue().toString();
                        Log.d("onAdded", refValue);
                        userIdList.add(refValue);


//                        for(DataSnapshot data : dataSnapshot.getChildren())
//                        {
//                            Log.d("onAdded", "onChildAdded: ");
//                            String obj=data.toString();
//                            Toast.makeText(RequestUserActivity.this,obj,Toast.LENGTH_LONG).show();
//                            userIdList.add(obj);
//                        }




                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    requestlistRecyclerAdapter = new RequestUserViewAdapter(userIdArrayList, RequestUserActivity.this);
//                    requestRecyclerView.setAdapter(requestlistRecyclerAdapter);

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    try {
                        Log.d("onAdded", dataSnapshot.toString());
                        Toast.makeText(RequestUserActivity.this,dataSnapshot.toString(),Toast.LENGTH_SHORT).show();

                        UserData obj = dataSnapshot.getValue(UserData.class);


                        for (int i = 0; i < userIdArrayList.size(); i++)
                            if (userIdArrayList.get(i).equals(dataSnapshot.getKey())) {
                                userIdArrayList.set(i, obj);
                                break;
                            }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                    requestlistRecyclerAdapter = new RequestUserViewAdapter(userIdArrayList, RequestUserActivity.this);
//                    requestRecyclerView.setAdapter(requestlistRecyclerAdapter);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    for (int i = 0; i < userIdArrayList.size(); i++)
                        if (userIdArrayList.get(i).equals(dataSnapshot.getKey())) {
                            userIdArrayList.remove(i);
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
        if (mChildEventListCurEvent == null) {
            mChildEventListCurEvent = new ChildEventListener() {
                public UserData obj;


                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {

                        Log.d("ChildAdded", String.valueOf(userIdList.size()));
                        for (int i = 0; i < userIdList.size(); i++){

                            if (userIdList.get(i).equals(dataSnapshot.getKey())) {
                                UserData u = dataSnapshot.getValue(UserData.class);
                                userIdArrayList.add(u);
                            }
                    }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    requestlistRecyclerAdapter = new RequestUserViewAdapter(userIdArrayList, RequestUserActivity.this);
                    requestRecyclerView.setAdapter(requestlistRecyclerAdapter);

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    try {
                        for (int i = 0; i < userIdList.size(); i++)
                            if (userIdList.get(i).equals(dataSnapshot.getKey())) {
                                UserData u = dataSnapshot.getValue(UserData.class);
                                userIdArrayList.add(u);
                            }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    requestlistRecyclerAdapter = new RequestUserViewAdapter(userIdArrayList, RequestUserActivity.this);
                    requestRecyclerView.setAdapter(requestlistRecyclerAdapter);

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
            mDBRefCurEvent.addChildEventListener(mChildEventListCurEvent);
    }


}
}