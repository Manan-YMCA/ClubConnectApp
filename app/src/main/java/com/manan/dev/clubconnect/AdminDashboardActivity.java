package com.manan.dev.clubconnect;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AdminDashboardActivity extends AppCompatActivity {
    private FloatingActionButton addNewEventFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        addNewEventFab = (FloatingActionButton) findViewById(R.id.add_new_event_fab);
        addNewEventFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this,AddNewEventActivity.class));
            }
        });
    }
}
