package com.manan.dev.clubconnect.User;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.google.firebase.database.ValueEventListener;
import com.manan.dev.clubconnect.CircleTransform;
import com.manan.dev.clubconnect.Models.UserData;
import com.manan.dev.clubconnect.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView userImg;
    private EditText userName;
    private EditText userPhone;
    private EditText userRoll;
    private EditText userEmail;
    private LinearLayout llClubs;
    private FirebaseAuth mAuth;
    private FloatingActionButton submitFab;
    private ProgressDialog pd;
    private DatabaseReference mDatabaseReference;
    private Spinner dropdown;
    private Spinner course;
    private Spinner batch;
    private UserData user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userImg = (ImageView) findViewById(R.id.photo_crop_user);
        userName = (EditText) findViewById(R.id.et_name);
        userPhone = (EditText) findViewById(R.id.user_profile_phone);
        userRoll = (EditText) findViewById(R.id.et_RollNo);
        userEmail = (EditText) findViewById(R.id.et_email);
        submitFab = (FloatingActionButton) findViewById(R.id.submit_fab);
        llClubs = (LinearLayout) findViewById(R.id.club_radiogrp);
        user = new UserData();

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        //pd.show();

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        ValueEventListener listener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    if(data.getKey().equals(mAuth.getCurrentUser().getUid())){
                        user = data.getValue(UserData.class);
                        Log.d("user id", data.getValue().toString());
                        updateValues(user);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addListenerForSingleValueEvent(listener);

        //Picasso.with(UserProfileActivity.this).load(mAuth.getCurrentUser().getPhotoUrl()).transform(new CircleTransform()).into(userImg);
        //userName.setText(mAuth.getCurrentUser().getDisplayName());

        dropdown = (Spinner)findViewById(R.id.spinner1);
        course = (Spinner)findViewById(R.id.spinner2);
        batch = (Spinner) findViewById(R.id.spinner3);



        String[] itemsBatch = new String[]{"Select Graduation Year","2016","2017","2018", "2019", "2020","2021"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemsBatch);
        dropdown.setAdapter(adapter);

        final String[]  itemsCou = new String[]{"Select Course", "B.Tech","M.Tech","M.Sc"};
        ArrayAdapter<String> adapter_2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemsCou);
        course.setAdapter(adapter_2);



        course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[]  itemsBatch = new String[]{};
                ArrayAdapter<String> adapter_2 = new ArrayAdapter<String>(UserProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsBatch);;
                switch(i)
                {
                    case 0:
                        break;
                    case 1:
                        itemsBatch = new String[]{"Select Batch","CE","IT","ECE","EIC","Mech","EL"};
                        adapter_2 = new ArrayAdapter<String>(UserProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsBatch);
                        break;
                    case 2:
                        itemsBatch = new String[]{"Select Batch","CE","IT","ECE","EIC","Mech","EL"};
                        adapter_2 = new ArrayAdapter<String>(UserProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsBatch);
                        break;
                    case 3:
                        itemsBatch = new String[]{"Select Batch","Physics","Maths"};
                        adapter_2 = new ArrayAdapter<String>(UserProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsBatch);
                        break;
                }
                batch.setAdapter(adapter_2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                String[] itemsBatch1 = new String[]{"Select Batch"};
                ArrayAdapter<String> adapter_3 = new ArrayAdapter<String>(UserProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsBatch1);
                batch.setAdapter(adapter_3);
            }
        });

        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);

        submitFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            pd.setIndeterminate(false);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setMax(100);
            pd.show();


            String phoneNo = userPhone.getText().toString();
            String rollNo = userRoll.getText().toString().toUpperCase();
            String photoID = mAuth.getCurrentUser().getPhotoUrl().toString();
            String name = mAuth.getCurrentUser().getDisplayName();
            String branch = batch.getSelectedItem().toString();
            String coursedata = course.getSelectedItem().toString();
            String email = userEmail.getText().toString();
            long graduationYear = Long.parseLong(dropdown.getSelectedItem().toString());
            ArrayList<String> pendingClubs=new ArrayList<>();

            for(int i=0; i<llClubs.getChildCount(); i++)
            {
                CheckBox cb = (CheckBox) llClubs.getChildAt(i);
                if(cb.isChecked()){
                    Toast.makeText(UserProfileActivity.this, cb.getText().toString(), Toast.LENGTH_SHORT).show();
                    try {
                        pendingClubs.add(cb.getText().toString());
                    }catch (Exception e){
                        Toast.makeText(UserProfileActivity.this, cb.getText(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            //UserData userData = new UserData(phoneNo, branch, coursedata, rollNo, photoID, name, graduationYear);


            final UserData userData = new UserData(phoneNo, branch, coursedata, rollNo, photoID, name, email, null, null, pendingClubs, null, graduationYear);

            boolean checker = (!userData.getName().equals("") &&
                    !userData.getPhotoID().equals("") &&
                    !userData.getUserPhoneNo().equals("") &&
                    !userData.getUserRollNo().equals("") &&
                    batch.getSelectedItem().toString().equals("Select Batch") &&
                    course.getSelectedItem().toString().equals("Select Course") &&
                    dropdown.getSelectedItem().toString().equals("Select Graduation Year")
            );

            if (true) {
                for (String club : pendingClubs) {
                    FirebaseDatabase.getInstance().getReference().child("notification").child(club).push().setValue(mAuth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UserProfileActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UserProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                uploadProfile(userData);

            }
            else {
                if(userData.getName().equals(""))
                    userName.setError("Required");
                if(userData.getUserPhoneNo().equals(""))
                    userPhone.setError("Required");
                if(userData.getUserRollNo().equals(""))
                    userRoll.setError("Required");
                if(dropdown.getSelectedItem().toString().equals("Select Graduation Year"))
                    ((TextView)dropdown.getSelectedView()).setError("Required");
                if(batch.getSelectedItem().toString().equals("Select Batch"))
                    ((TextView)batch.getSelectedItem()).setError("Required");
                if(course.getSelectedItem().toString().equals("Select Course"))
                    ((TextView)course.getSelectedItem()).setError("Required");
                pd.hide();
            }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateValues(UserData user) {
        Picasso.with(UserProfileActivity.this).load(user.getPhotoID()).transform(new CircleTransform()).into(userImg);
        userName.setText(user.getName());
        if(user.getUserRollNo()!= null){
            userRoll.setText(user.getUserRollNo());
        }
        if(user.getEmailId()!=null){
            userEmail.setText(user.getEmailId());
        }
        if(user.getUserPhoneNo()!=null){
            userPhone.setText(user.getUserPhoneNo());
        }
        if(user.getUserGraduationYear() != null){
            String[] itemsBatch = new String[]{Long.toString(user.getUserGraduationYear()),"Select Graduation Year","2016","2017","2018", "2019", "2020","2021"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemsBatch);
            dropdown.setAdapter(adapter);
        }
        if(user.getUserCourse()!= null){
            final String[]  itemsCou = new String[]{user.getUserCourse(),"Select Course", "B.Tech","M.Tech","M.Sc"};
            ArrayAdapter<String> adapter_2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemsCou);
            course.setAdapter(adapter_2);
        }
        if(user.getUserBranch()!= null){
            String course = user.getUserCourse();
            ArrayAdapter<String> adapter_2 = new ArrayAdapter<String>(UserProfileActivity.this, android.R.layout.simple_spinner_dropdown_item);
            if(course.equals("B.Tech")){
                String[] itemsBatch = new String[]{user.getUserBranch(), "Select Batch","CE","IT","ECE","EIC","Mech","EL"};
                adapter_2 = new ArrayAdapter<String>(UserProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsBatch);
            }else if(course.equals("M.Tech")){
                String[] itemsBatch = new String[]{user.getUserBranch(), "Select Batch","CE","IT","ECE","EIC","Mech","EL"};
                adapter_2 = new ArrayAdapter<String>(UserProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsBatch);
            }else if(course.equals("M.Sc")){
                String[] itemsBatch = new String[]{user.getUserBranch(), "Select Batch","Physics","Maths"};
                adapter_2 = new ArrayAdapter<String>(UserProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsBatch);
            }
            batch.setAdapter(adapter_2);
        }
        if(user.getPendingClubs() != null){
            for(String item : user.getPendingClubs()){
                if(item.equals("Manan")){
                    CheckBox cb = (CheckBox) findViewById(R.id.Manan);
                    cb.setEnabled(false);
                }
                else if(item.equals("Srijan")){
                    CheckBox cb = (CheckBox) findViewById(R.id.Srijan);
                    cb.setEnabled(false);
                }
                else if(item.equals("Ananya")){
                    CheckBox cb = (CheckBox) findViewById(R.id.Ananya);
                    cb.setEnabled(false);
                }
                else if(item.equals("Vividha")){
                    CheckBox cb = (CheckBox) findViewById(R.id.Vividha);
                    cb.setEnabled(false);
                }
                else if(item.equals("Microbird")){
                    CheckBox cb = (CheckBox) findViewById(R.id.Microbird);
                    cb.setEnabled(false);
                }
                else if(item.equals("Jhalak")){
                    CheckBox cb = (CheckBox) findViewById(R.id.Jhalak);
                    cb.setEnabled(false);
                }
                else if(item.equals("Samarpan")){
                    CheckBox cb = (CheckBox) findViewById(R.id.Samarpan);
                    cb.setEnabled(false);
                }
                else if(item.equals("Natraja")){
                    CheckBox cb = (CheckBox) findViewById(R.id.Natraja);
                    cb.setEnabled(false);
                }
                else if(item.equals("Mechnext")){
                    CheckBox cb = (CheckBox) findViewById(R.id.Mechnext);
                    cb.setEnabled(false);
                }
                else if(item.equals("Tarannum")){
                    CheckBox cb = (CheckBox) findViewById(R.id.Tarannum);
                    cb.setEnabled(false);
                }
            }

        }
        if(user.getMyClubs() != null){
            for(String item : user.getMyClubs()){
                if(item.equals("Manan")){
                    CheckBox cb = (CheckBox) findViewById(R.id.Manan);
                    cb.setEnabled(false);
                    cb.setChecked(true);
                }
                else if(item.equals("Srijan")){
                    CheckBox cb = (CheckBox) findViewById(R.id.Srijan);
                    cb.setEnabled(false);
                    cb.setChecked(true);
                }
                else if(item.equals("Ananya")){
                    CheckBox cb = (CheckBox) findViewById(R.id.Ananya);
                    cb.setEnabled(false);
                    cb.setChecked(true);
                }
                else if(item.equals("Vividha")){
                    CheckBox cb = (CheckBox) findViewById(R.id.Vividha);
                    cb.setEnabled(false);
                    cb.setChecked(true);
                }
                else if(item.equals("Microbird")){
                    CheckBox cb = (CheckBox) findViewById(R.id.Microbird);
                    cb.setEnabled(false);
                    cb.setChecked(true);
                }
                else if(item.equals("Jhalak")){
                    CheckBox cb = (CheckBox) findViewById(R.id.Jhalak);
                    cb.setEnabled(false);
                    cb.setChecked(true);
                }
                else if(item.equals("Samarpan")){
                    CheckBox cb = (CheckBox) findViewById(R.id.Samarpan);
                    cb.setEnabled(false);
                    cb.setChecked(true);
                }
                else if(item.equals("Natraja")){
                    CheckBox cb = (CheckBox) findViewById(R.id.Natraja);
                    cb.setEnabled(false);
                    cb.setChecked(true);
                }
                else if(item.equals("Mechnext")){
                    CheckBox cb = (CheckBox) findViewById(R.id.Mechnext);
                    cb.setEnabled(false);
                    cb.setChecked(true);
                }
                else if(item.equals("Tarannum")){
                    CheckBox cb = (CheckBox) findViewById(R.id.Tarannum);
                    cb.setEnabled(false);
                    cb.setChecked(true);
                }
            }
        }
    }

    private void uploadProfile(UserData userData) {
        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UserProfileActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    finish();
                } else {
                    pd.hide();
                }
            }
        });
    }
}
