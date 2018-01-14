package com.manan.dev.clubconnect;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.manan.dev.clubconnect.Adapters.AdminClubMemberAdapter;
import com.manan.dev.clubconnect.Models.ClubMember;
import com.manan.dev.clubconnect.Models.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yatindhingra on 14/01/18.
 */

public class FragmentMemberClub extends Fragment {

    private AdminClubMemberAdapter adapter;

    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private DatabaseReference mDBRefCurEvent;
    private ChildEventListener mChildEventListCurEvent;

    private ArrayList<UserData> userIdArrayList;
    private Map<String, String> userIdList;
    private Map<String, UserData> allUsers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_members_club, container, false);

        userIdArrayList = new ArrayList<>();
        userIdList = new HashMap<>();
        allUsers = new HashMap<>();
        String clubName = this.getActivity().getIntent().getStringExtra("name");

        RecyclerView adminMemberRecyclerView = (RecyclerView) view.findViewById(R.id.member_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        adminMemberRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new AdminClubMemberAdapter(getActivity(), userIdArrayList);
        adminMemberRecyclerView.setAdapter(adapter);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("members").child(clubName);
        mDBRefCurEvent = FirebaseDatabase.getInstance().getReference().child("users");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        attachDatabaseListener();
    }

    @Override
    public void onPause() {
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
                        Log.d("onAdded",dataSnapshot.getValue().toString());
                        ClubMember refValue = dataSnapshot.getValue(ClubMember.class);
                        for (String key : userIdList.keySet())
                            if (userIdList.get(key).equals(refValue)) {
                                dataSnapshot.getRef().setValue(null);
                                return;
                            }
                        userIdList.put(dataSnapshot.getKey(), refValue.getUserId());
                        modifyUserIdArrayList();
                        adapter.notifyDataSetChanged();
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
                        userIdList.remove(dataSnapshot.getKey());
                        modifyUserIdArrayList();
                        adapter.notifyDataSetChanged();
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
                        if (u != null) {
                            u.UID = dataSnapshot.getKey();
                            allUsers.put(dataSnapshot.getKey(), u);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    modifyUserIdArrayList();
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    try {
                        UserData u = dataSnapshot.getValue(UserData.class);
                        if (u != null) {
                            u.UID = dataSnapshot.getKey();
                            allUsers.put(dataSnapshot.getKey(), u);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    modifyUserIdArrayList();
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    try {
                        allUsers.remove(dataSnapshot.getKey());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    modifyUserIdArrayList();
                    adapter.notifyDataSetChanged();
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
        String uid;
        for (String pushId : userIdList.keySet()) {
            uid = userIdList.get(pushId);
            if (allUsers.containsKey(uid)) {
                allUsers.get(uid).tempData = pushId;
                userIdArrayList.add(allUsers.get(uid));
            }
        }
        //Toast.makeText(getActivity(), Integer.toString(userIdArrayList.size()), Toast.LENGTH_SHORT).show();
    }
}
