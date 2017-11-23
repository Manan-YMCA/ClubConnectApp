package com.manan.dev.clubconnect.User;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.manan.dev.clubconnect.CircleTransform;
import com.manan.dev.clubconnect.R;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        ImageView profilePhoto = (ImageView) findViewById(R.id.photo_crop_user);
     Picasso.with(UserProfileActivity.this).load(R.drawable.login_back).transform(new CircleTransform()).into(profilePhoto);

        Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
        Spinner course = (Spinner)findViewById(R.id.spinner2);



        String[] itemsBatch = new String[]{"Select Year","2K14","2K15","2K16", "2K17", "2K18","2K14","2K15","2K16", "2K17", "2K18"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemsBatch);
        dropdown.setAdapter(adapter);

        String[]  itemsCou = new String[]{"CE","IT","MCA","MTech"};
        ArrayAdapter<String> adapter_2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemsCou);
        dropdown.setAdapter(adapter);


    }
}
