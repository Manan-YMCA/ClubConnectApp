package com.manan.dev.clubconnect;

import android.annotation.SuppressLint;
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
import android.widget.RelativeLayout;
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

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class AddNewEventActivity extends AppCompatActivity {
    String clubName;
    private EditText input_eventname, input_event_venue, input_clubname, input_description, input_date, input_start_time, input_end_time;
    private ImageView Add_new_date, addPhotosBtn;
    private LinearLayout event_day_layout;
    private LinearLayout uploadedPhotoLL;
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

        count = 0;
        imgCount = 0;
        PICK_IMAGE_REQUEST = 111;

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

                count++;

                //adding items to arraylist for storing data
                dateData.add((long) 0);
                startTimeData.add((long) 0);
                endTimeData.add((long) 0);

                RelativeLayout rlayout = layoutreturner(count);
                Toast.makeText(AddNewEventActivity.this, "layout returned", Toast.LENGTH_SHORT).show();

                event_day_layout.addView(rlayout);

            }
        };

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ResourceType")
    RelativeLayout layoutreturner(int count){
        RelativeLayout rLayout = new RelativeLayout(AddNewEventActivity.this);
        rLayout.setBackground(getResources().getDrawable(R.drawable.border_textview));
        Resources r = getResources();
        int fifteen = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, r.getDisplayMetrics());
        int five = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5 , r.getDisplayMetrics());
        int sixty = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r.getDisplayMetrics());
        int zero = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, r.getDisplayMetrics());

        //params for relative layout
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(fifteen, fifteen, fifteen, fifteen);
        rLayout.setPadding(five, five, five, five);
        rLayout.setLayoutParams(layoutParams);


        //creating layout params for textView
        RelativeLayout.LayoutParams lparams1 = new RelativeLayout.LayoutParams(sixty, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lparams1.setMargins(0, 0, five, 0);

        //creating layout params for date editText
        RelativeLayout.LayoutParams lparams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        //creating layout params for time editText
        LinearLayout.LayoutParams lparams3 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);



        //adding items to arraylist for adding edit text
        date.add(new EditText(AddNewEventActivity.this));
        date.get(count).setId(3);
        startTime.add(new EditText(AddNewEventActivity.this));
        startTime.get(count).setId(4);
        endTime.add(new EditText(AddNewEventActivity.this));
        endTime.get(count).setId(5);

        //setting hints to the edit text boxes
        startTime.get(count).setHint(R.string.start_time);
        date.get(count).setHint(R.string.date);
        endTime.get(count).setHint(R.string.end_time);

        //creating text views
        TextView day = new TextView(AddNewEventActivity.this);
        day.setId(1);
        TextView textView = new TextView(AddNewEventActivity.this);
        textView.setId(2);
        textView.setText("TO");
        String dayText = "Day" + " " + (count + 1);
        day.setText(dayText);
        day.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        day.setPadding(five, five, five, five);
        day.setLayoutParams(lparams1);

        //creating a linear layout
        LinearLayout lLayout = new LinearLayout(AddNewEventActivity.this);
        lLayout.setOrientation(LinearLayout.HORIZONTAL);
        RelativeLayout.LayoutParams lLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        lLayoutParams.setMargins(0, five, 0, 0);
        lLayoutParams.addRule(RelativeLayout.BELOW, date.get(count).getId());
        lLayoutParams.addRule(RelativeLayout.ALIGN_START, date.get(count).getId());
        lLayout.setLayoutParams(lLayoutParams);



        //adding background to the edit text
        date.get(count).setBackground(drawableOriginal);
        startTime.get(count).setBackground(drawableOriginal);
        endTime.get(count).setBackground(drawableOriginal);


        //formatting the date editText
        date.get(count).setPadding(five, five, five, five);
        lparams2.addRule(RelativeLayout.RIGHT_OF, day.getId());
        date.get(count).setLayoutParams(lparams2);

        //formatting the time editText
        startTime.get(count).setLayoutParams(lparams3);
        endTime.get(count).setLayoutParams(lparams3);


        //setting text sizes of the edittext boxes
        startTime.get(count).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        endTime.get(count).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);


        //adding on click listeners to edittext boxes
        date.get(count).setOnClickListener(createOnClickListenerDate(count));
        startTime.get(count).setOnClickListener(createOnClickListenerTime(count, true));
        endTime.get(count).setOnClickListener(createOnClickListenerTime(count, false));

        //user cannot click or paste in date and time edit texts
        date.get(count).setLongClickable(false);
        startTime.get(count).setLongClickable(false);
        endTime.get(count).setLongClickable(false);
        date.get(count).setFocusable(false);
        startTime.get(count).setFocusable(false);
        endTime.get(count).setFocusable(false);

        //adding items to the lower linear layout
        lLayout.addView(startTime.get(count));
        lLayout.addView(textView);
        lLayout.addView(endTime.get(count));

        //adding items to relative layout
        rLayout.addView(day);
        rLayout.addView(date.get(count));
        rLayout.addView(lLayout);


        return  rLayout;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    View.OnClickListener createOnClickListenerDate(final int i) {

        return new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(final View v) {
                Toast.makeText(AddNewEventActivity.this, "clicked", Toast.LENGTH_SHORT).show();
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

}
