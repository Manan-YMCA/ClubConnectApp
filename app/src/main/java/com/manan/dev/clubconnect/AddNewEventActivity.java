package com.manan.dev.clubconnect;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AddNewEventActivity extends AppCompatActivity {
    String clubName;
    private EditText input_eventname, input_event_venue, input_clubname, input_description, input_date, input_start_time, input_end_time;
    private ImageView Add_new_date, addPhotosBtn;
    private LinearLayout event_day_layout, uploadedPhotoLL;
    int count = 0, PICK_IMAGE_REQUEST = 111, imgCount = 0;
    ArrayList<EditText> date;
    ArrayList<EditText> startTime, endTime;
    ArrayList<Long> dateData, startTimeData, endTimeData;
    private Drawable drawableOriginal;
    private StorageReference firebaseStorage;
    Uri filePath;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);

        firebaseStorage = FirebaseStorage.getInstance().getReference();

        input_eventname = (EditText) findViewById(R.id.input_eventname);
        input_event_venue = (EditText) findViewById(R.id.input_event_venu);
        input_clubname = (EditText) findViewById(R.id.input_clubname);
        input_description = (EditText) findViewById(R.id.input_description);
        input_date = (EditText) findViewById(R.id.input_date);
        input_start_time = (EditText) findViewById(R.id.input_start_time);
        input_end_time = (EditText) findViewById(R.id.input_end_time);
        Add_new_date = (ImageView) findViewById(R.id.Add_new_date);
        addPhotosBtn = (ImageView) findViewById(R.id.upload_photos_btn);
        event_day_layout = (LinearLayout) findViewById(R.id.event_day_layout);
        uploadedPhotoLL = (LinearLayout) findViewById(R.id.img_uploaded_ll);
        date = new ArrayList<EditText>();
        startTime = new ArrayList<EditText>();
        endTime = new ArrayList<EditText>();
        dateData = new ArrayList<Long>();
        startTimeData = new ArrayList<Long>();
        endTimeData = new ArrayList<Long>();


        dateData.add((long) 0);
        startTimeData.add((long) 0);
        endTimeData.add((long) 0);
        date.add(input_date);
        startTime.add(input_start_time);
        endTime.add(input_end_time);
        date.get(count).setOnClickListener(createOnClickListenerDate(count));
        startTime.get(count).setOnClickListener(createOnClickListenerTime(count, true));
        endTime.get(count).setOnClickListener(createOnClickListenerTime(count, false));
        startTime.get(count).setFocusable(false);
        date.get(count).setFocusable(false);
        endTime.get(count).setFocusable(false);
        drawableOriginal = input_date.getBackground();

        clubName = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
        input_clubname.setText(clubName);

        Add_new_date.setOnClickListener(newEventAdditionListener());
        addPhotosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                StorageReference childRef = firebaseStorage.child(input_eventname.getText().toString() + imgCount + "image.jpg");
                // To be put on final add event button to avoid useless uploads
                UploadTask uploadTask = childRef.putFile(filePath);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(AddNewEventActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        //Setting image to ImageView
                        ImageView imgView = new ImageView(AddNewEventActivity.this);
                        imgView.setImageBitmap(bitmap);
                        uploadedPhotoLL.addView(imgView);
                        imgCount++;
                        // TODO Add photoLink in database
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddNewEventActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(AddNewEventActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_event, menu);
        return true;
    }

    View.OnClickListener newEventAdditionListener() {

        return new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {


                final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
                count++;
                final LinearLayout.LayoutParams lparams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lparams1.setMarginStart((int) convertDpToPixel(50, getApplicationContext()));
                final LinearLayout.LayoutParams lparams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int width = (int) convertDpToPixel(5, getApplicationContext());
                //lparams2.setMargins(width, width, width, width);k

                //Log.d("countChecker", Integer.toString(count));
                dateData.add((long) 0);
                startTimeData.add((long) 0);
                endTimeData.add((long) 0);
                date.add(new EditText(AddNewEventActivity.this));
                startTime.add(new EditText(AddNewEventActivity.this));
                endTime.add(new EditText(AddNewEventActivity.this));
                date.get(count).setWidth((int) convertDpToPixel(200, getApplicationContext()));
                startTime.get(count).setWidth((int) convertDpToPixel(140, getApplicationContext()));
                endTime.get(count).setWidth((int) convertDpToPixel(140, getApplicationContext()));

                startTime.get(count).setHint(R.string.start_time);
                date.get(count).setHint(R.string.date);
                endTime.get(count).setHint(R.string.end_time);

                LinearLayout layout1 = new LinearLayout(AddNewEventActivity.this);
                layout1.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout layout2 = new LinearLayout(AddNewEventActivity.this);
                layout2.setOrientation(LinearLayout.HORIZONTAL);

                TextView day = new TextView(AddNewEventActivity.this);
                TextView textView = new TextView(AddNewEventActivity.this);
                textView.setText("TO");
                String dayText = "DAY" + " " + (count + 1);
                day.setText(dayText);
                day.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                date.get(count).setLayoutParams(lparams2);
                date.get(count).setBackground(drawableOriginal);
                startTime.get(count).setBackground(drawableOriginal);
                endTime.get(count).setBackground(drawableOriginal);

                layout1.addView(day);
                layout1.addView(date.get(count));
                layout1.setLayoutParams(lparams);

                layout2.setLayoutParams(lparams2);
                startTime.get(count).setLayoutParams(lparams1);
                layout2.addView(startTime.get(count));
                layout2.addView(textView);
                layout2.addView(endTime.get(count));

                event_day_layout.addView(layout1);
                event_day_layout.addView(layout2);

                date.get(count).setOnClickListener(createOnClickListenerDate(count));
                startTime.get(count).setOnClickListener(createOnClickListenerTime(count, true));
                endTime.get(count).setOnClickListener(createOnClickListenerTime(count, false));
                date.get(count).setFocusable(false);
                startTime.get(count).setFocusable(false);
                endTime.get(count).setFocusable(false);
            }
        };

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    View.OnClickListener createOnClickListenerDate(final int i) {

        return new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(final View v) {
                java.util.Calendar mcurrentDate = java.util.Calendar.getInstance();
                final int mYear = mcurrentDate.get(java.util.Calendar.YEAR);
                final int mMonth = mcurrentDate.get(java.util.Calendar.MONTH);
                final int mDay = mcurrentDate.get(java.util.Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(AddNewEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        java.util.Calendar myCalendar = java.util.Calendar.getInstance();
                        myCalendar.setTimeInMillis(0);
                        myCalendar.set(java.util.Calendar.YEAR, selectedyear);
                        myCalendar.set(java.util.Calendar.MONTH, selectedmonth);
                        myCalendar.set(java.util.Calendar.DAY_OF_MONTH, selectedday);
                        String myFormat = "dd/MM/yy"; //Change as you need
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                        date.get(i).setText(sdf.format(myCalendar.getTime()));

                        dateData.set(i, myCalendar.getTimeInMillis());
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.getDatePicker().setCalendarViewShown(true);
                mDatePicker.show();

            }
        };
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    View.OnClickListener createOnClickListenerTime(final int i, final boolean isStart) {

        return new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(final View v) {
                final java.util.Calendar mcurrentDate = java.util.Calendar.getInstance();
                final int mHour = mcurrentDate.get(java.util.Calendar.HOUR_OF_DAY);
                final int mMinute = mcurrentDate.get(java.util.Calendar.MINUTE);

                TimePickerDialog mTimePicker = new TimePickerDialog(AddNewEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String displayTime = String.format("%02d:%02d", hourOfDay, minute);
                        if (isStart)
                            startTime.get(i).setText(displayTime);
                        else
                            endTime.get(i).setText(displayTime);
                        int time = hourOfDay * 60 * 60 + minute * 60;
                        if (isStart)
                            startTimeData.set(i, (long) (time * 1000));
                        else
                            endTimeData.set(i, (long) (time * 1000));
                    }
                }, mHour, mMinute, true);

                mTimePicker.setTitle("Select date");
                mTimePicker.show();
            }
        };
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

}
