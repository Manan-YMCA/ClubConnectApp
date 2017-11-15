package com.manan.dev.clubconnect;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AddNewEventActivity extends AppCompatActivity {
    String clubName;
    private EditText input_eventname, input_event_venue, input_clubname, input_description, input_date, input_start_time, input_end_time;
    private ImageView Add_new_date;
    private LinearLayout event_day_layout;
    private LinearLayout uploadedPhotoLL;
    int count = 0, PICK_IMAGE_REQUEST = 111;
    ArrayList<EditText> date;
    ArrayList<EditText> startTime, endTime;
    ArrayList<Long> dateData, startTimeData, endTimeData;
    private ArrayList<Uri> imgLocationsData;
    private ArrayList<String> coordinatorsData;
    private String eventNameData, eventVenueData, clubNameData, descriptionData;
    private Drawable drawableOriginal;
    private StorageReference firebaseStorage;
    private ProgressDialog pd;

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
        event_day_layout = (LinearLayout) findViewById(R.id.event_day_layout);
        uploadedPhotoLL = (LinearLayout) findViewById(R.id.img_uploaded_ll);
        date = new ArrayList<EditText>();
        startTime = new ArrayList<EditText>();
        endTime = new ArrayList<EditText>();
        dateData = new ArrayList<Long>();
        startTimeData = new ArrayList<Long>();
        endTimeData = new ArrayList<Long>();
        imgLocationsData = new ArrayList<>();
        coordinatorsData = new ArrayList<>();

        count = 0;

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

        clubNameData = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
        input_clubname.setText(clubNameData);

        Add_new_date.setOnClickListener(newEventAdditionListener());
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.setIndeterminate(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            LinearLayout container = (LinearLayout) findViewById(R.id.img_uploaded_ll);
            Uri localData = data.getData();
            imgLocationsData.add(localData);
            final ImageView iv = addNewHolderForImage(container, localData);

            float finalWidth = 400;
            try {
                Bitmap bitmap = null;
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgLocationsData.get(imgLocationsData.size() - 1));
                bitmap = Bitmap.createScaledBitmap(bitmap, (int) finalWidth, (int) (finalWidth / bitmap.getWidth() * bitmap.getHeight()),
                        true);
                iv.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(AddNewEventActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private ImageView addNewHolderForImage(final LinearLayout container, final Uri localData) {
        if (container.getChildCount() == 0)
            container.setVisibility(View.VISIBLE);
        final RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams rlLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //TODO: get from dimens.xml
        rlLayoutParams.setMargins(0, 10, 0, 0);
        relativeLayout.setLayoutParams(rlLayoutParams);

        ImageView imageViewData = new ImageView(this);
        imageViewData.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 450));
        imageViewData.setCropToPadding(true);
        imageViewData.setScaleType(ImageView.ScaleType.CENTER_CROP);


        ImageView imageView = new ImageView(this);
        RelativeLayout.LayoutParams ivLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ivLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        ivLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        imageView.setLayoutParams(ivLayoutParams);
        imageView.setBackgroundColor(Color.argb(80, 140, 140, 140));
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.vector_clear));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgLocationsData.remove(localData);
                container.removeView(relativeLayout);
            }
        });

        relativeLayout.addView(imageViewData);
        relativeLayout.addView(imageView);

        container.addView(relativeLayout);

        return imageViewData;
    }


    void uploadImagesToFirebase() {
        pd.setMax(100);
        pd.show();

        for (int i = 0; i < imgLocationsData.size(); i++) {
            String imgName = imgLocationsData.get(i).getLastPathSegment();
            imgName = imgName.replace('.', '@');
            int lastIndex = imgName.lastIndexOf('@');
            String imgExtension = imgName.substring(lastIndex + 1);
            StorageReference childRef = firebaseStorage.child(input_eventname.getText().toString() + "_" + i + "." + imgExtension);

            // To be put on final add event button to avoid useless uploads
            UploadTask uploadTask = childRef.putFile(imgLocationsData.get(i));
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(AddNewEventActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                    // TODO Add photoLink in database
                    taskSnapshot.getDownloadUrl();
                    pd.hide();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddNewEventActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                    pd.hide();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    float transferred = taskSnapshot.getBytesTransferred();
                    float total = taskSnapshot.getTotalByteCount();
                    pd.setProgress((int) (transferred / total * 100.0 / imgLocationsData.size()));
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_photos:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
                return true;

            default:
                return super.onOptionsItemSelected(item);
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

                //setting layout parameters
                final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                count++;
                final LinearLayout.LayoutParams lparams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                int top = (int) convertDpToPixel(5, getApplicationContext());
                int left = (int) convertDpToPixel(50, getApplicationContext());
                lparams1.setMargins(left, top, 0, 0);

                final LinearLayout.LayoutParams lparams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int width = (int) convertDpToPixel(10, getApplicationContext());
                lparams2.setMargins(width, 0, 0, 0);

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
                day.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.vector_date, 0, 0, 0);
                textView.setText("TO");
                String dayText = "DAY" + " " + (count + 1);
                day.setText(dayText);
                day.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

                date.get(count).setLayoutParams(lparams2);
                date.get(count).setBackground(drawableOriginal);

                date.get(count).setLongClickable(false);
                startTime.get(count).setLongClickable(false);
                endTime.get(count).setLongClickable(false);

                startTime.get(count).setBackground(drawableOriginal);
                endTime.get(count).setBackground(drawableOriginal);
                startTime.get(count).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                endTime.get(count).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

                layout1.addView(day);
                layout1.addView(date.get(count));
                layout1.setLayoutParams(lparams);
                layout2.setLayoutParams(lparams1);
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

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    @Override
    public void onBackPressed() {
        fillData();
        boolean isUntouched = (eventVenueData.equals("") &&
                eventNameData.equals("") &&
                descriptionData.equals("") &&
                coordinatorsData.size() == 0 &&
                imgLocationsData.size() == 0 &&
                dateData.size() == 1 &&
                startTimeData.size() == 1 &&
                endTimeData.size() == 1 &&
                dateData.get(0) == 0 &&
                startTimeData.get(0) == 0 &&
                endTimeData.get(0) == 0
        );
        if (isUntouched) {
            finish();
        } else {
            showDialogBoxToExit();
        }
    }

    private void showDialogBoxToExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to exit?")
                .setPositiveButton("No", null)
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // delete the task on abnormal exit.
                        AddNewEventActivity.this.finish();
                    }
                });
        builder.create();
        builder.show();
    }


    private void fillData() {
        eventNameData = input_eventname.getText().toString().trim();
        eventVenueData = input_event_venue.getText().toString().trim();
        clubNameData = input_clubname.getText().toString().trim();
        descriptionData = input_description.getText().toString().trim();
    }


}
