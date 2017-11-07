package com.manan.dev.clubconnect;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DeleteActivity extends AppCompatActivity {

    ArrayList<EditText> date;
    ArrayList<EditText> startTime, endTime;
    View plus;
    Button submitButton;
    int count = 0;
    ArrayList<Long> dateData, startTimeData, endTimeData;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_delete);
        date.add(new EditText(this));
        startTime.add(new EditText(this));
        endTime.add(new EditText(this));
        date.get(count).setOnClickListener(createOnClickListenerDate(count));
        dateData.add((long) 0);
        startTimeData.add((long) 0);
        endTimeData.add((long) 0);
        startTime.get(count).setOnClickListener(createOnClickListenerTime(count, true));
        endTime.get(count).setOnClickListener(createOnClickListenerTime(count, false));

        plus = new View(this);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                dateData.add((long) 0);
                startTimeData.add((long) 0);
                endTimeData.add((long) 0);
                date.add(new EditText(DeleteActivity.this));
                startTime.add(new EditText(DeleteActivity.this));
                endTime.add(new EditText(DeleteActivity.this));
                date.get(count).setOnClickListener(createOnClickListenerDate(count));
                startTime.get(count).setOnClickListener(createOnClickListenerTime(count, true));
                endTime.get(count).setOnClickListener(createOnClickListenerTime(count, false));
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    View.OnClickListener createOnClickListenerDate(final int i)
    {

        return new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(final View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                final int mYear = mcurrentDate.get(Calendar.YEAR);
                final int mMonth = mcurrentDate.get(Calendar.MONTH);
                final int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(DeleteActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar myCalendar = Calendar.getInstance();
                        myCalendar.setTimeInMillis(0);
                        myCalendar.set(Calendar.YEAR, selectedyear);
                        myCalendar.set(Calendar.MONTH, selectedmonth);
                        myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                        String myFormat = "dd/MM/yy"; //Change as you need
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                        date.get(i).setText(sdf.format(myCalendar.getTime()));

                        dateData.set(i,myCalendar.getTimeInMillis());
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.getDatePicker().setCalendarViewShown(true);
                mDatePicker.show();

            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    View.OnClickListener createOnClickListenerTime(final int i, final boolean isStart)
    {

        return new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(final View v) {
                final Calendar mcurrentDate = Calendar.getInstance();
                final int mHour = mcurrentDate.get(Calendar.HOUR_OF_DAY);
                final int mMinute = mcurrentDate.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker = new TimePickerDialog(DeleteActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String displayTime = hourOfDay + ":" + minute;
                        if(isStart)
                            startTime.get(i).setText(displayTime);
                        else
                            endTime.get(i).setText(displayTime);
                        int time = hourOfDay * 60 * 60 + minute * 60;
                        if(isStart)
                            startTimeData.set(i, (long) (time * 1000));
                        else
                            endTimeData.set(i, (long)(time*1000));
                    }
                }, mHour, mMinute, true);

                mTimePicker.setTitle("Select date");
                mTimePicker.show();

            }
        };
    }

}
