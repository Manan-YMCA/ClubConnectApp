package com.manan.dev.clubconnect.User;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.manan.dev.clubconnect.CircleTransform;
import com.manan.dev.clubconnect.Models.UserData;
import com.manan.dev.clubconnect.R;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView userImg;
    private EditText userName;
    private EditText userPhone;
    private EditText userRoll;
    private FirebaseAuth mAuth;
    private FloatingActionButton submitFab;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userImg = (ImageView) findViewById(R.id.photo_crop_user);
        userName = (EditText) findViewById(R.id.et_name);
        userPhone = (EditText) findViewById(R.id.user_profile_phone);
        userRoll = (EditText) findViewById(R.id.et_RollNo);
        submitFab = (FloatingActionButton) findViewById(R.id.submit_fab);

        mAuth = FirebaseAuth.getInstance();

        Picasso.with(UserProfileActivity.this).load(mAuth.getCurrentUser().getPhotoUrl()).transform(new CircleTransform()).into(userImg);
        userName.setText(mAuth.getCurrentUser().getDisplayName());

        final Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
        final Spinner course = (Spinner)findViewById(R.id.spinner2);
        final Spinner batch = (Spinner) findViewById(R.id.spinner3);





        String[] itemsBatch = new String[]{"Select Graduation Year","2016","2017","2018", "2019", "2020"};
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
                //Log.d("value", userPhone.getText().toString());
                String phoneNo = userPhone.getText().toString();
                String rollNo = userRoll.getText().toString().toUpperCase();
                String photoID = mAuth.getCurrentUser().getPhotoUrl().toString();
                String name = mAuth.getCurrentUser().getDisplayName();
                String branch = batch.getSelectedItem().toString();
                String coursedata = course.getSelectedItem().toString();
                long graduationYear = Long.parseLong(dropdown.getSelectedItem().toString());
                UserData userData = new UserData(phoneNo, branch, coursedata, rollNo, photoID, name, graduationYear);

                FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(UserProfileActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                        else{
                            pd.hide();
                        }
                    }
                });

            }
        });

    }
}
