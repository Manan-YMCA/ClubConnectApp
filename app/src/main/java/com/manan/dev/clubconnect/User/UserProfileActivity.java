package com.manan.dev.clubconnect.User;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.manan.dev.clubconnect.CircleTransform;
import com.manan.dev.clubconnect.R;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView userImg;
    private EditText userName;
    private EditText userPhone;
    private EditText userRoll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userImg = (ImageView) findViewById(R.id.photo_crop_user);
        userName = (EditText) findViewById(R.id.et_name);
        userPhone = (EditText) findViewById(R.id.et_phone);
        userRoll = (EditText) findViewById(R.id.et_RollNo);

        Picasso.with(UserProfileActivity.this).load(R.drawable.login_back).transform(new CircleTransform()).into(userImg);

        Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
        final Spinner course = (Spinner)findViewById(R.id.spinner2);
        final Spinner batch = (Spinner) findViewById(R.id.spinner3);





        String[] itemsBatch = new String[]{"Select Graduation Year","2014","2015","2016", "2017", "2018"};
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

    }
}
