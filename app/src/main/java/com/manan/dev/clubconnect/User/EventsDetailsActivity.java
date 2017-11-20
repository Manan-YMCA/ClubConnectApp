package com.manan.dev.clubconnect.User;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.manan.dev.clubconnect.Models.Event;
import com.manan.dev.clubconnect.Models.TimeInterval;
import com.manan.dev.clubconnect.R;

import java.util.ArrayList;

public class EventsDetailsActivity extends AppCompatActivity {

    private TextView eventDetailsToken;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent i = getIntent();
        eventDetailsToken  = (TextView) findViewById(R.id.event_details_token_la);
        eventDetailsToken.setText(i.getStringExtra("eventToken"));

        event = new Event();

   FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Hello Yatin Sir,Kushank sir,kachroo", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void addEventToCalender(){
        long date = event.getDays().get(0).getDate();
        long startTime = event.getDays().get(0).getStartTime();
        long endTime = event.getDays().get(0).getEndTime();

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.EventsEntity.CONTENT_URI)
                .setType("vnd.android.cursor.item/event")
                .putExtra(CalendarContract.EventDays.STARTDAY, date)
                .putExtra(CalendarContract.EventDays.ENDDAY, date)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
                .putExtra(CalendarContract.EventsEntity.TITLE, event.eventName)
                .putExtra(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
                .putExtra(CalendarContract.Reminders.MINUTES,5);


        startActivity(intent);
    }
}
