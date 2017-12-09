package com.manan.dev.clubconnect.EditEvent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.manan.dev.clubconnect.Adapters.CoordinatorAdapter;
import com.manan.dev.clubconnect.CircleTransform;
import com.manan.dev.clubconnect.EditEventActivity;
import com.manan.dev.clubconnect.Models.Coordinator;
import com.manan.dev.clubconnect.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static java.util.Collections.sort;

public class ModifyCoordinators extends AppCompatActivity {


    private static final String TAG = "ModifyCoordinators";
    private ArrayList<Coordinator> coordinatorsAll;

    private ChildEventListener mChildEventListener;
    private DatabaseReference mDatabaseReference;
    private CoordinatorAdapter coordinatorAdapter;
    private String clubNameData;
    AutoCompleteTextView eventCoord;
    private LinearLayout llCordinators;

    ArrayList<String> name,phone, photo, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_coordinators);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        name = getIntent().getStringArrayListExtra(EditEventActivity.REQ_PARA_EVENT_COORD_NAME);
        phone = getIntent().getStringArrayListExtra(EditEventActivity.REQ_PARA_EVENT_COORD_PHONE);
        photo = getIntent().getStringArrayListExtra(EditEventActivity.REQ_PARA_EVENT_COORD_PHOTO);
        email = getIntent().getStringArrayListExtra(EditEventActivity.REQ_PARA_EVENT_COORD_EMAIL);

        if(name==null)
            name = new ArrayList<>();
        if(phone==null)
            phone=new ArrayList<>();
        if(photo == null)
            photo = new ArrayList<>();
        if(email==null)
            email = new ArrayList<>();

        eventCoord = findViewById(R.id.etEventCoord);
        llCordinators = findViewById(R.id.ll_coordinators);
        Button bOK = findViewById(R.id.bOK);
        Button bCancel = findViewById(R.id.bCancel);

        for(int i=0; i<name.size(); i++)
        {
            Toast.makeText(this, name.get(i),Toast.LENGTH_SHORT).show();
            addCoordinatorToLayout(new Coordinator(name.get(i), email.get(i), phone.get(i), photo.get(i)));
        }

        if (bOK != null)
            bOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (name.size()==0) {
                        Toast.makeText(ModifyCoordinators.this, "There should be atleast one coordinator", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent();

                        intent.putExtra(EditEventActivity.REQ_PARA_EVENT_COORD_NAME, name);
                        intent.putExtra(EditEventActivity.REQ_PARA_EVENT_COORD_PHONE, phone);
                        intent.putExtra(EditEventActivity.REQ_PARA_EVENT_COORD_PHOTO, photo);
                        intent.putExtra(EditEventActivity.REQ_PARA_EVENT_COORD_EMAIL, email);

                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            });

        if (bCancel != null)
            bCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
            });

        init();
    }

    private void init() {
        coordinatorsAll = new ArrayList<>();

        coordinatorAdapter = new CoordinatorAdapter(
                this,
                R.layout.coordinator_item_view,
                R.id.coordinator_item_name,
                coordinatorsAll
        );
        eventCoord.setAdapter(coordinatorAdapter);
        eventCoord.setThreshold(1);
        eventCoord.setInputType(InputType.TYPE_CLASS_TEXT);

        eventCoord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventCoord.showDropDown();
            }
        });
        eventCoord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Coordinator coordinator = coordinatorsAll.get(i);
                eventCoord.setText("", false);

                for(int j=0;j<email.size();j++){
                    if(coordinator.getEmail().equals(email.get(j)))
                        return;
                }

                email.add(coordinator.getEmail());
                photo.add(coordinator.getPhoto());
                name.add(coordinator.getName());
                phone.add(coordinator.getPhone());

                addCoordinatorToLayout(coordinator);
            }
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            clubNameData = user.getDisplayName();
        } else {
            finish();
        }

        Log.d(TAG, clubNameData);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("coordinators").child(clubNameData);

    }

    private void addCoordinatorToLayout(final Coordinator coordinator) {
        @SuppressLint("InflateParams") final LinearLayout v = (LinearLayout) LayoutInflater.from(ModifyCoordinators.this).inflate(R.layout.add_coordinator_tv_item, null, false);
        ((TextView) v.findViewById(R.id.tvUserName)).setText(coordinator.getName());
        ((TextView) v.findViewById(R.id.tvUserId)).setText(coordinator.getPhone());
        v.findViewById(R.id.removeCoordinator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                llCordinators.removeView(v);
                for(int j=0;j<email.size();j++){
                    if(coordinator.getEmail().equals(email.get(j)))
                    {
                        email.remove(j);
                        photo.remove(j);
                        phone.remove(j);
                        name.remove(j);
                        break;
                    }
                }
            }
        });
        Picasso.with(ModifyCoordinators.this).load(coordinator.getPhoto()).resize(200, 200).centerCrop().transform(new CircleTransform()).into((ImageView) v.findViewById(R.id.ivUserIcon));
        llCordinators.addView(v);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        super.onBackPressed();
    }


    private void updateList() {
        sort(coordinatorsAll);
        coordinatorAdapter = new CoordinatorAdapter(
                this,
                R.layout.coordinator_item_view,
                R.id.coordinator_item_name,
                coordinatorsAll
        );
        eventCoord.setAdapter(coordinatorAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseReadListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
        coordinatorsAll.clear();
    }


    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }


    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {
                        coordinatorsAll.add(dataSnapshot.getValue(Coordinator.class));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    updateList();
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
                    updateList();
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
    }
}
