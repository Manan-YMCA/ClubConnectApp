package com.manan.dev.clubconnect.user;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class UserProfileActivity extends AppCompatActivity {

    private ImageView userImg;
    private EditText userName;
    private EditText userPhone;
    private EditText userRoll;
    private EditText userEmail;
    private LinearLayout llClubs;
    private FirebaseAuth mAuth;
    private ProgressDialog pd;
    private Spinner dropdown;
    private Spinner course;
    private Spinner batch;
    private UserData user;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userImg = findViewById(R.id.photo_crop_user);
        userName = findViewById(R.id.et_name);
        userPhone = findViewById(R.id.user_profile_phone);
        userRoll = findViewById(R.id.et_RollNo);
        userEmail = findViewById(R.id.et_email);
        FloatingActionButton submitFab = findViewById(R.id.submit_fab);
        llClubs = findViewById(R.id.club_radiogrp);
        user = new UserData();

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        //pd.show();

        mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        ValueEventListener listener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    if(data.getKey().equals(userId)){
                        user = data.getValue(UserData.class);
                        if(user!=null && user.getEmailId()!=null)
                            Log.d("user id", user.getEmailId());
                        else
                            Log.d("user id", "null");
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

        dropdown = findViewById(R.id.spinner1);
        course = findViewById(R.id.spinner2);
        batch = findViewById(R.id.spinner3);



        String[] itemsBatch = new String[]{"Select Graduation Year","2016","2017","2018", "2019", "2020","2021"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsBatch);
        dropdown.setAdapter(adapter);

        final String[]  itemsCou = new String[]{"Select Course", "B.Tech","M.Tech","M.Sc"};
        ArrayAdapter<String> adapter_2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsCou);
        course.setAdapter(adapter_2);



        course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[]  itemsBatch = new String[]{};
                switch(i)
                {
                    case 0:
                        break;
                    case 1:
                        itemsBatch = new String[]{"Select Batch","CE","IT","ECE","EIC","Mech","EL"};
                        break;
                    case 2:
                        itemsBatch = new String[]{"Select Batch","CE","IT","ECE","EIC","Mech","EL"};
                        break;
                    case 3:
                        itemsBatch = new String[]{"Select Batch","Physics","Maths"};
                        break;
                }
                ArrayAdapter<String> adapter_2 = new ArrayAdapter<>(UserProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsBatch);
                batch.setAdapter(adapter_2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                String[] itemsBatch1 = new String[]{"Select Batch"};
                ArrayAdapter<String> adapter_3 = new ArrayAdapter<>(UserProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsBatch1);
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
            onSubmitPressed();
            }
        });

        FirebaseUser curUser = mAuth.getCurrentUser();
        if(curUser==null) {
            finish();
            return;
        }
        userId = curUser.getUid();

    }

    private void onSubmitPressed() {
        pd.setIndeterminate(false);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMax(100);
        pd.show();


        FirebaseUser curUser = mAuth.getCurrentUser();
        if(curUser==null) {
            Toast.makeText(UserProfileActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            pd.dismiss();
            return;
        }
        String photoID = null;
        if(curUser.getPhotoUrl()!=null)
            photoID = curUser.getPhotoUrl().toString();
        String phoneNo = userPhone.getText().toString();
        String rollNo = userRoll.getText().toString().toUpperCase();
        String name = mAuth.getCurrentUser().getDisplayName();
        String branch=null;
        if(batch.getSelectedItem()!=null)
            branch = batch.getSelectedItem().toString();
        String coursedata=null;
        if(course.getSelectedItem()!=null)
            coursedata = course.getSelectedItem().toString();
        String email = userEmail.getText().toString();
        Long graduationYear = null;
        if(dropdown.getSelectedItem()!=null)
            graduationYear = Long.parseLong(dropdown.getSelectedItem().toString());
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

        /*
        boolean checker = (!userData.getName().equals("") &&
                !userData.getPhotoID().equals("") &&
                !userData.getUserPhoneNo().equals("") &&
                !userData.getUserRollNo().equals("") &&
                batch.getSelectedItem().toString().equals("Select Batch") &&
                course.getSelectedItem().toString().equals("Select Course") &&
                dropdown.getSelectedItem().toString().equals("Select Graduation Year")
        );
        */

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
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsBatch);
            dropdown.setAdapter(adapter);
        }
        String[] itemsBatch = new String[]{};

        if(user.getUserCourse()!= null){
            final String[]  itemsCou = new String[]{"Select Course", "B.Tech","M.Tech","M.Sc"};
            ArrayAdapter<String> adapter_2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsCou);
            ArrayAdapter<String> adapter_batch;
            course.setAdapter(adapter_2);
            int pos = 0;
            for(int i=0;i<itemsCou.length; i++)
                if(itemsCou[i].equals(user.getUserCourse())) {
                    pos = i;
                    break;
                }
            course.setSelection(pos);

            String course = user.getUserCourse();
            switch (course) {
                case "B.Tech":
                    itemsBatch = new String[]{"Select Batch", "CE", "IT", "ECE", "EIC", "Mech", "EL"};
                    break;
                case "M.Tech":
                    itemsBatch = new String[]{"Select Batch", "CE", "IT", "ECE", "EIC", "Mech", "EL"};
                    break;
                case "M.Sc":
                    itemsBatch = new String[]{"Select Batch", "Physics", "Maths"};
                    break;
            }
            adapter_batch = new ArrayAdapter<>(UserProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsBatch);
            batch.setAdapter(adapter_batch);
        }
        if(user.getUserBranch()!= null){
            int pos = 0;
            for(int i=0;i<itemsBatch.length; i++)
                if(itemsBatch[i].equals(user.getUserBranch())) {
                    pos = i;
                    break;
                }
            batch.setSelection(pos);
        }

        if(user.getPendingClubs() != null){
            for(String item : user.getPendingClubs()){
                CheckBox cb;
                switch (item) {
                    case "Manan":
                        cb = findViewById(R.id.Manan);
                        cb.setEnabled(false);
                        break;
                    case "Srijan":
                        cb = findViewById(R.id.Srijan);
                        cb.setEnabled(false);
                        break;
                    case "Ananya":
                        cb = findViewById(R.id.Ananya);
                        cb.setEnabled(false);
                        break;
                    case "Vividha":
                        cb = findViewById(R.id.Vividha);
                        cb.setEnabled(false);
                        break;
                    case "Microbird":
                        cb = findViewById(R.id.Microbird);
                        cb.setEnabled(false);
                        break;
                    case "Jhalak":
                        cb = findViewById(R.id.Jhalak);
                        cb.setEnabled(false);
                        break;
                    case "Samarpan":
                        cb = findViewById(R.id.Samarpan);
                        cb.setEnabled(false);
                        break;
                    case "Natraja":
                        cb = findViewById(R.id.Natraja);
                        cb.setEnabled(false);
                        break;
                    case "Mechnext":
                        cb = findViewById(R.id.Mechnext);
                        cb.setEnabled(false);
                        break;
                    case "Tarannum":
                        cb = findViewById(R.id.Tarannum);
                        cb.setEnabled(false);
                        break;
                }
            }

        }
        if(user.getMyClubs() != null){
            for(String item : user.getMyClubs()){
                CheckBox cb;
                switch (item) {
                    case "Manan":
                        cb = findViewById(R.id.Manan);
                        cb.setEnabled(false);
                        cb.setChecked(true);
                        break;
                    case "Srijan":
                        cb = findViewById(R.id.Srijan);
                        cb.setEnabled(false);
                        cb.setChecked(true);
                        break;
                    case "Ananya":
                        cb = findViewById(R.id.Ananya);
                        cb.setEnabled(false);
                        cb.setChecked(true);
                        break;
                    case "Vividha":
                        cb = findViewById(R.id.Vividha);
                        cb.setEnabled(false);
                        cb.setChecked(true);
                        break;
                    case "Microbird":
                        cb = findViewById(R.id.Microbird);
                        cb.setEnabled(false);
                        cb.setChecked(true);
                        break;
                    case "Jhalak":
                        cb = findViewById(R.id.Jhalak);
                        cb.setEnabled(false);
                        cb.setChecked(true);
                        break;
                    case "Samarpan":
                        cb = findViewById(R.id.Samarpan);
                        cb.setEnabled(false);
                        cb.setChecked(true);
                        break;
                    case "Natraja":
                        cb = findViewById(R.id.Natraja);
                        cb.setEnabled(false);
                        cb.setChecked(true);
                        break;
                    case "Mechnext":
                        cb = findViewById(R.id.Mechnext);
                        cb.setEnabled(false);
                        cb.setChecked(true);
                        break;
                    case "Tarannum":
                        cb = findViewById(R.id.Tarannum);
                        cb.setEnabled(false);
                        cb.setChecked(true);
                        break;
                }
            }
        }
    }

    private void uploadProfile(UserData userData) {
        FirebaseDatabase.getInstance().getReference().child("users").child(userId).setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
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
