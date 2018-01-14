package com.manan.dev.clubconnect;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.manan.dev.clubconnect.Models.ClubMember;
import com.manan.dev.clubconnect.Models.Coordinator;
import com.manan.dev.clubconnect.Models.UserData;
import com.squareup.picasso.Picasso;


public class ClubMembersAdmin extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private DatabaseReference mMemberDatabaseReference;
    private ChildEventListener mChildEventListener;
    private ChildEventListener mMemberChildEventListener;
    private String memberKey;
    private UserData user;
    private ImageView userProfile;
    private TextView userName;
    private TextView userPhone;
    private TextView userEmail;
    private TextView userYear;
    private TextView userCourse;
    private TextView userBranch;
    private Button addCoord;
    private ProgressDialog pd;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_members_admin);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        final String clubName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        mMemberDatabaseReference = FirebaseDatabase.getInstance().getReference().child("members").child(clubName);

        userId = getIntent().getStringExtra("userId");
        userName = (TextView) findViewById(R.id.member_name);
        userProfile = (ImageView) findViewById(R.id.member_photo);
        userPhone = (TextView) findViewById(R.id.member_phone);
        userEmail = (TextView) findViewById(R.id.member_email);
        userYear = (TextView) findViewById(R.id.member_graduation_year);
        userCourse = (TextView) findViewById(R.id.member_course);
        userBranch = (TextView) findViewById(R.id.member_branch);
        addCoord = (Button) findViewById(R.id.member_add_coordinator);

        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait...");
        pd.setCanceledOnTouchOutside(false);
        addCoord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setIndeterminate(false);
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.setMax(100);
                pd.setCancelable(false);
                pd.show();
                Coordinator coordinator = new Coordinator(user.getName(), user.getEmailId(), user.getUserPhoneNo(), user.getPhotoID());
                FirebaseDatabase.getInstance().getReference().child("coordinators").child(clubName).child(userId).setValue(coordinator)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //pd.dismiss();
                                    Toast.makeText(ClubMembersAdmin.this, "Added as coordinator", Toast.LENGTH_SHORT).show();
                                    ClubMember member = new ClubMember(userId, true);
                                    FirebaseDatabase.getInstance().getReference().child("members").child(clubName).child(memberKey).setValue(member)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                pd.dismiss();
                                                Toast.makeText(ClubMembersAdmin.this, "ADDED", Toast.LENGTH_SHORT).show();
                                                addCoord.setEnabled(false);
                                                addCoord.setText("ALREADY A COODINATOR");
                                            }
                                        }
                                    });
                                }
                                else {
                                    pd.dismiss();
                                }
                            }
                        });
            }
        });

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
    }

    private void detatchDatabaseListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
        if(mMemberChildEventListener != null){
            mMemberDatabaseReference.removeEventListener(mMemberChildEventListener);
            mMemberChildEventListener = null;
        }
    }

    private void attachDatabaseListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getKey().equals(userId)) {
                        user = dataSnapshot.getValue(UserData.class);
                        if (user != null && user.getEmailId() != null) {
                            Log.d("user id", user.getEmailId());
                        } else
                            Log.d("user id", "null");
                        updateValues(user);
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
                        updateValues(user);
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
            mDatabaseReference.addChildEventListener(mChildEventListener);
        }
        if(mMemberChildEventListener == null){
            mMemberChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {
                        ClubMember clubMember = dataSnapshot.getValue(ClubMember.class);
                        if (clubMember.getUserId().equals(userId)) {
                            Boolean isCoordinator = clubMember.getCoordinator();
                            memberKey = dataSnapshot.getKey();
                            Log.d("key ", memberKey);
                            if(isCoordinator){
                                addCoord.setVisibility(View.GONE);
                            }
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
            mMemberDatabaseReference.addChildEventListener(mMemberChildEventListener);
        }
    }

    private void updateValues(UserData user) {
        userName.setText(userName.getText() + user.getName());
        userPhone.setText(userPhone.getText() + user.getUserPhoneNo());
        userEmail.setText(userEmail.getText() + user.getEmailId());
        userBranch.setText(userBranch.getText() + user.getUserBranch());
        userCourse.setText(userCourse.getText() + user.getUserCourse());
        userYear.setText(userYear.getText() + Long.toString(user.getUserGraduationYear()));
        Picasso.with(ClubMembersAdmin.this).load(user.getPhotoID()).transform(new CircleTransform()).into(userProfile);
    }
}
