package com.manan.dev.clubconnect;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.manan.dev.clubconnect.Models.Event;

import java.security.Timestamp;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class AddNewEventActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText eventNameView, descriptionView, coordinatorsView, eventOrganiser;
    private RecyclerView coordinatorRecyclerView;
    private TextView dateView, startTimeView, endTimeView;
    private FloatingActionButton submitEventFab;
    long startTimestamp, endTimestamp;
    private DatabaseReference database;
    String clubName;
    Button btnDatePicker, btnTimePicker,btnEndTimePicker;
    EditText txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;

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
        // dateView = (TextView) findViewById(R.id.input_start_date_textview);
        //startTimeView = (TextView) findViewById(R.id.input_start_time_textview);
        //endTimeView = (TextView) findViewById(R.id.input_end_time_textview);
        submitEventFab = (FloatingActionButton) findViewById(R.id.submit_event_fab);
//
//        startTimeView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                DialogFragment newFragment = new TimePickerFragment();
////                newFragment.show(getFragmentManager(),"startTimePicker");
//            }
//        });
//        endTimeView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                DialogFragment newFragment = new TimePickerFragment();
////                newFragment.show(getFragmentManager(),"endTimePicker");
//            }
//        });
//        dateView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                DialogFragment newFragment = new DatePickerFragment();
////                newFragment.show(getFragmentManager(), "datePicker");
//            }
//        });


        btnDatePicker = (Button) findViewById(R.id.btn_date);
        btnTimePicker = (Button) findViewById(R.id.btn_time);
        //btnEndTimePicker = (Button) findViewById(R.id.btn_endTime);
        txtDate = (EditText) findViewById(R.id.in_date);
        txtTime = (EditText) findViewById(R.id.in_time);
        //txtEndTime = (EditText) findViewById(R.id.end_time);

        btnDatePicker.setOnClickListener((View.OnClickListener) this);
        btnTimePicker.setOnClickListener((View.OnClickListener) this);

        database = FirebaseDatabase.getInstance().getReference();
        database.keepSynced(true);
        submitEventFab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                attemptEventAddition();
            }
        });


    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void attemptEventAddition() {
        String eventname = eventNameView.getText().toString();
        String eventdesc = descriptionView.getText().toString();
        String coordinators = coordinatorsView.getText().toString();

        startTimestamp = componentTimeToTimestamp(mYear,mMonth,mDay,mHour,mMinute);
        Log.e("Time","TIMESENT" + startTimestamp);
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
    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {

        if (v == btnDatePicker) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == btnTimePicker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            txtTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }
    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    int componentTimeToTimestamp(int year, int month, int day, int hour, int minute) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return (int) (c.getTimeInMillis() / 1000L);
    }


}
