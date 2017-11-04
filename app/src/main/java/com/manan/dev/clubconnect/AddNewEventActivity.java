package com.manan.dev.clubconnect;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.manan.dev.clubconnect.Models.Event;

public class AddNewEventActivity extends AppCompatActivity {
    private EditText eventNameView, descriptionView, coordinatorsView, eventOrganiser;
    private RecyclerView coordinatorRecyclerView;
    private TextView dateView, startTimeView, endTimeView;
    private FloatingActionButton submitEventFab;
    long startTimestamp, endTimestamp;
    private DatabaseReference database;
    String clubName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);

        clubName = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];

        eventNameView = (EditText) findViewById(R.id.input_eventname);
        eventOrganiser = (EditText) findViewById(R.id.input_organiser);
        eventOrganiser.setText(clubName);
        descriptionView = (EditText) findViewById(R.id.input_description);
        coordinatorsView = (EditText) findViewById(R.id.input_coordinators);
        dateView = (TextView) findViewById(R.id.input_start_date_textview);
        startTimeView = (TextView) findViewById(R.id.input_start_time_textview);
        endTimeView = (TextView) findViewById(R.id.input_end_time_textview);
        submitEventFab = (FloatingActionButton) findViewById(R.id.submit_event_fab);

        startTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DialogFragment newFragment = new TimePickerFragment();
//                newFragment.show(getFragmentManager(),"startTimePicker");
            }
        });
        endTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DialogFragment newFragment = new TimePickerFragment();
//                newFragment.show(getFragmentManager(),"endTimePicker");
            }
        });
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DialogFragment newFragment = new DatePickerFragment();
//                newFragment.show(getFragmentManager(), "datePicker");
            }
        });
        database = FirebaseDatabase.getInstance().getReference();
        database.keepSynced(true);

        submitEventFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptEventAddition();
            }
        });
    }

    private void attemptEventAddition() {
        String eventname = eventNameView.getText().toString();
        String eventdesc = descriptionView.getText().toString();
        String coordinators = coordinatorsView.getText().toString();
        startTimestamp = 1111;
        endTimestamp = 9999;

        Event event = new Event(eventname, eventdesc, coordinators, startTimestamp, endTimestamp);
        String key = database.child("events").child(clubName).push().getKey();
        database.child("events").child(clubName).child(key).setValue(event);
    }

//    public static class TimePickerFragment extends DialogFragment
//            implements TimePickerDialog.OnTimeSetListener {
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the current time as the default values for the picker
//            final Calendar c = Calendar.getInstance();
//            int hour = c.get(Calendar.HOUR_OF_DAY);
//            int minute = c.get(Calendar.MINUTE);
//
//            // Create a new instance of TimePickerDialog and return it
//            return new TimePickerDialog(getActivity(), this, hour, minute,
//                    DateFormat.is24HourFormat(getActivity()));
//        }
//
//        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//            // Do something with the time chosen by the user
//        }
//    }
//    public static class DatePickerFragment extends DialogFragment
//            implements DatePickerDialog.OnDateSetListener {
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the current time as the default values for the picker
//            final Calendar c = Calendar.getInstance();
//            int year = c.get(Calendar.YEAR);
//            int month = c.get(Calendar.MONTH);
//            int date = c.get(Calendar.DATE);
//
//            // Create a new instance of TimePickerDialog and return it
//            return new DatePickerDialog(getActivity(), this, year, month, date);
//        }
//
//        public void onDateSet(DatePicker view, int year, int month,int date) {
//            // Do something with the time chosen by the user
//
//        }
//    }

}
