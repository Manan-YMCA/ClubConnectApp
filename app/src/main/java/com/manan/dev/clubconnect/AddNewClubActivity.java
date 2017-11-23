package com.manan.dev.clubconnect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddNewClubActivity extends AppCompatActivity {

    EditText etclubName, etEmail, etPassword;
    Button btnAddClub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_club);

        etclubName = (EditText) findViewById(R.id.et_club_name);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnAddClub = (Button) findViewById(R.id.btn_add_club);

        btnAddClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
