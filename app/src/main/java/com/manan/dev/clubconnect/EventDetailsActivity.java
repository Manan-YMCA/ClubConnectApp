package com.manan.dev.clubconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class EventDetailsActivity extends AppCompatActivity {
    private TextView eventDetailsToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        eventDetailsToken  = (TextView) findViewById(R.id.event_details_token);
        Intent i = getIntent();
        eventDetailsToken.setText(i.getStringExtra("eventToken"));
        //Toast.makeText(EventDetailsActivity.this, i.getStringExtra("eventToken"), Toast.LENGTH_LONG).show();
    }
}
