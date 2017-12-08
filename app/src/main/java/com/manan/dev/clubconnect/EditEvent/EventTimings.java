package com.manan.dev.clubconnect.EditEvent;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.manan.dev.clubconnect.AddNewEventActivity;
import com.manan.dev.clubconnect.EditEventActivity;
import com.manan.dev.clubconnect.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class EventTimings extends AppCompatActivity {

    long date, stime, etime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_timings);

        date = stime = etime = 0;

        long event_date = getIntent().getLongExtra(EditEventActivity.REQ_PARA_EVENT_DATE, 0);
        long event_stime = getIntent().getLongExtra(EditEventActivity.REQ_PARA_EVENT_STIME, 0);
        long event_etime = getIntent().getLongExtra(EditEventActivity.REQ_PARA_EVENT_ETIME, 0);

        final EditText eventDate = findViewById(R.id.etEventDate);
        final EditText eventStartTime = findViewById(R.id.etEventStartTime);
        final EditText eventEndTime = findViewById(R.id.etEventEndtime);
        Button bOK = findViewById(R.id.bOK);
        Button bCancel = findViewById(R.id.bCancel);

        if (event_date != 0) {
            java.util.Calendar myCalendar = java.util.Calendar.getInstance();
            myCalendar.setTimeInMillis(event_date);
            String myFormat = "dd/MM/yy"; //Change as you need
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

            eventDate.setText(sdf.format(myCalendar.getTime()));
        }
        if (event_stime != 0) {

            final java.util.Calendar mcurrentDate = java.util.Calendar.getInstance();
            mcurrentDate.setTimeInMillis(event_stime);
            String displayTime = String.format(Locale.ENGLISH, "%02d:%02d", mcurrentDate.get(Calendar.HOUR_OF_DAY), mcurrentDate.get(Calendar.MINUTE));
            eventStartTime.setText(displayTime);
        }
        if (event_etime != 0) {
            final java.util.Calendar mcurrentDate = java.util.Calendar.getInstance();
            mcurrentDate.setTimeInMillis(event_etime);
            String displayTime = String.format(Locale.ENGLISH, "%02d:%02d", mcurrentDate.get(Calendar.HOUR_OF_DAY), mcurrentDate.get(Calendar.MINUTE));
            eventEndTime.setText(displayTime);
        }

        if (bOK != null)
            bOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (eventDate.getText().toString().equals("")) {
                        Toast.makeText(EventTimings.this, "Event date cannot be empty", Toast.LENGTH_SHORT).show();
                    } else if (eventStartTime.getText().toString().equals("")) {
                        Toast.makeText(EventTimings.this, "Event start time cannot be empty", Toast.LENGTH_SHORT).show();
                    } else if (eventEndTime.getText().toString().equals("")) {
                        Toast.makeText(EventTimings.this, "Event end time cannot be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(EditEventActivity.REQ_PARA_EVENT_DATE, date);
                        intent.putExtra(EditEventActivity.REQ_PARA_EVENT_STIME, stime);
                        intent.putExtra(EditEventActivity.REQ_PARA_EVENT_ETIME, etime);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            });

        if (bCancel != null)
            bCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
            });

        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                java.util.Calendar mcurrentDate = java.util.Calendar.getInstance();
                final int mYear = mcurrentDate.get(java.util.Calendar.YEAR);
                final int mMonth = mcurrentDate.get(java.util.Calendar.MONTH);
                final int mDay = mcurrentDate.get(java.util.Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(EventTimings.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        java.util.Calendar myCalendar = java.util.Calendar.getInstance();
                        myCalendar.setTimeInMillis(0);
                        myCalendar.set(java.util.Calendar.YEAR, selectedyear);
                        myCalendar.set(java.util.Calendar.MONTH, selectedmonth);
                        myCalendar.set(java.util.Calendar.DAY_OF_MONTH, selectedday);
                        String myFormat = "dd/MM/yy"; //Change as you need
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

                        eventDate.setText(sdf.format(myCalendar.getTime()));

                        date = (myCalendar.getTimeInMillis());
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.getDatePicker().setCalendarViewShown(true);
                mDatePicker.show();
            }
        });

        eventEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final java.util.Calendar mcurrentDate = java.util.Calendar.getInstance();
                final int mHour = mcurrentDate.get(java.util.Calendar.HOUR_OF_DAY);
                final int mMinute = mcurrentDate.get(java.util.Calendar.MINUTE);

                TimePickerDialog mTimePicker = new TimePickerDialog(EventTimings.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String displayTime = String.format(Locale.ENGLISH, "%02d:%02d", hourOfDay, minute);

                        eventEndTime.setText(displayTime);
                        //Toast.makeText(AddNewEventActivity.this, "" + TimeZone.getDefault().getRawOffset(), Toast.LENGTH_SHORT).show();
                        etime = 1000L * (hourOfDay * 60 * 60 + minute * 60) - TimeZone.getDefault().getRawOffset();
                    }
                }, mHour, mMinute, true);

                mTimePicker.setTitle("Select date");
                mTimePicker.show();
            }
        });

        eventStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final java.util.Calendar mcurrentDate = java.util.Calendar.getInstance();
                final int mHour = mcurrentDate.get(java.util.Calendar.HOUR_OF_DAY);
                final int mMinute = mcurrentDate.get(java.util.Calendar.MINUTE);

                TimePickerDialog mTimePicker = new TimePickerDialog(EventTimings.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String displayTime = String.format(Locale.ENGLISH, "%02d:%02d", hourOfDay, minute);

                        eventStartTime.setText(displayTime);
                        //Toast.makeText(AddNewEventActivity.this, "" + TimeZone.getDefault().getRawOffset(), Toast.LENGTH_SHORT).show();
                        stime = 1000L * (hourOfDay * 60 * 60 + minute * 60) - TimeZone.getDefault().getRawOffset();
                    }
                }, mHour, mMinute, true);

                mTimePicker.setTitle("Select date");
                mTimePicker.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        super.onBackPressed();
    }
}
