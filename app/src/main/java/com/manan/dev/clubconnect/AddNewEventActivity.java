package com.manan.dev.clubconnect;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.manan.dev.clubconnect.Adapters.CoordinatorAdapter;
import com.manan.dev.clubconnect.Models.Coordinator;
import com.manan.dev.clubconnect.Models.Event;
import com.manan.dev.clubconnect.Models.TimeInterval;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

import static java.util.Collections.sort;

public class AddNewEventActivity extends AppCompatActivity {
    private EditText input_eventname;
    private EditText input_event_venue;
    private EditText input_clubname;
    private EditText input_description;
    private AutoCompleteTextView input_event_cooordinator;
    private LinearLayout event_day_layout;
    private LinearLayout containerCoordinators;
    int count = 0, PICK_IMAGE_REQUEST = 111;
    ArrayList<EditText> date;
    ArrayList<EditText> startTime, endTime;
    //ArrayList<Long> dateData, startTimeData, endTimeData;
    private ArrayList<Uri> imgLocationsData;
    private ArrayList<Coordinator> coordinatorsAll;
    //private ArrayList<Coordinator> coordinatorsData;
    //private String eventNameData, eventVenueData, clubNameData, descriptionData;
    private Event event;
    private StorageReference firebaseStorage;
    private ProgressDialog pd;

    private ChildEventListener mChildEventListener;
    private DatabaseReference mDatabaseReference;
    private CoordinatorAdapter coordinatorAdapter;
    private String clubNameData;
    private EditText input_date;
    private EditText input_start_time;
    private EditText input_end_time;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);

        firebaseStorage = FirebaseStorage.getInstance().getReference();

        ImageView add_new_date = (ImageView) findViewById(R.id.Add_new_date);
        input_date = (EditText) findViewById(R.id.input_date);
        input_start_time = (EditText) findViewById(R.id.input_start_time);
        input_end_time = (EditText) findViewById(R.id.input_end_time);


        input_eventname = (EditText) findViewById(R.id.input_eventname);
        input_event_venue = (EditText) findViewById(R.id.input_event_venu);
        input_clubname = (EditText) findViewById(R.id.input_clubname);
        input_description = (EditText) findViewById(R.id.input_description);
        event_day_layout = (LinearLayout) findViewById(R.id.event_day_layout);
        containerCoordinators = (LinearLayout) findViewById(R.id.ll_add_coordinators);
        input_event_cooordinator = (AutoCompleteTextView) findViewById(R.id.coordinator_name);
        event = new Event();
        event.coordinatorID = new ArrayList<>();
        event.days = new ArrayList<>();
        event.photoID.posters = new ArrayList<>();
        event.photoID.afterEvent = new ArrayList<>();

        coordinatorsAll = new ArrayList<>();

        //coordinatorsAll.add(new Coordinator("Kushank", "k@g.com", null, null));
        //coordinatorsAll.add(new Coordinator("Saini", "s@g.com", null, null));
        //coordinatorsAll.add(new Coordinator("Naman", "n@g.com", null, null));
        coordinatorAdapter = new CoordinatorAdapter(
                this,
                R.layout.coordinator_item_view,
                R.id.coordinator_item_name,
                coordinatorsAll
        );

        input_event_cooordinator.setAdapter(coordinatorAdapter);
        input_event_cooordinator.setThreshold(1);
        input_event_cooordinator.setInputType(InputType.TYPE_CLASS_TEXT);
        input_event_cooordinator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_event_cooordinator.showDropDown();
            }
        });
        input_event_cooordinator.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                input_event_cooordinator.setError(null);
                final Coordinator coordinator = coordinatorsAll.get(i);
                event.coordinatorID.add(coordinator.getEmail());
                @SuppressLint("InflateParams") final LinearLayout v = (LinearLayout) LayoutInflater.from(AddNewEventActivity.this).inflate(R.layout.add_coordinator_tv_item, null, false);
                ((TextView) v.findViewById(R.id.tvUserName)).setText(coordinator.getName());
                ((TextView) v.findViewById(R.id.tvUserId)).setText(coordinator.getPhone());
                v.findViewById(R.id.removeCoordinator).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v1) {
                        containerCoordinators.removeView(v);
                        event.coordinatorID.remove(coordinator.getEmail());
                    }
                });
                Picasso.with(AddNewEventActivity.this).load(coordinator.getPhoto()).resize(200, 200).centerCrop().transform(new CircleTransform()).into((ImageView) v.findViewById(R.id.ivUserIcon));
                containerCoordinators.addView(v);
                input_event_cooordinator.setText(" ", true);
                //hideKeyboard();
            }
        });

        date = new ArrayList<>();
        startTime = new ArrayList<>();
        endTime = new ArrayList<>();
        imgLocationsData = new ArrayList<>();
        coordinatorsAll = new ArrayList<>();

        count = 0;

        event.days.add(new TimeInterval(0, 0, 0));
        date.add(input_date);
        startTime.add(input_start_time);
        endTime.add(input_end_time);
        date.get(count).setOnClickListener(createOnClickListenerDate(count));
        startTime.get(count).setOnClickListener(createOnClickListenerTime(count, true));
        endTime.get(count).setOnClickListener(createOnClickListenerTime(count, false));
        startTime.get(count).setFocusable(false);
        date.get(count).setFocusable(false);
        endTime.get(count).setFocusable(false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userEmail = user.getEmail();
        assert userEmail != null;
        clubNameData = userEmail.split("@")[0];

        input_clubname.setText(clubNameData);

        add_new_date.setOnClickListener(newEventAdditionListener());
        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("coordinators").child(clubNameData);

        Toast.makeText(AddNewEventActivity.this, "" + TimeZone.getDefault().getRawOffset(), Toast.LENGTH_SHORT).show();

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
                Bitmap bitmap;
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
        relativeLayout.setLayoutParams(rlLayoutParams);
        relativeLayout.setPadding(0, (int) getResources().getDimension(R.dimen.ten), 0, 0);

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

    void uploadEvent() {

        fillData();

        Boolean checker = (!event.getEventVenue().equals("") &&
                !event.getEventName().equals("") &&
                !event.getEventDesc().equals("") &&
                event.coordinatorID.size() > 0 &&
                imgLocationsData.size() > 0 &&
                event.days.size() >= 1 &&
                event.days.get(0).getDate() > 0 &&
                event.days.get(0).getStartTime() > 0 &&
                event.days.get(0).getEndTime() > 0
        );

        if(checker){
            pd.setIndeterminate(false);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setMax(100);
            pd.show();
            event.setEventName(input_eventname.getText().toString());
            event.setEventDesc(input_description.getText().toString());
            event.setEventVenue(input_event_venue.getText().toString());
            new ImageUpload(uploadImagesToFirebase()).execute();
        }
        else {
            if(event.getEventName().equals("")){
                input_eventname.setError("Required");
            }
            if(event.getEventVenue().equals("")){
                input_event_venue.setError("Required");
            }
            if(event.getEventDesc().equals("")){
                input_description.setError("Required");
            }
            if(event.coordinatorID.size() == 0){
                input_event_cooordinator.setError("Required");
            }
            if(imgLocationsData.size() == 0){
                Toast.makeText(AddNewEventActivity.this, "One Poster is Compulsory!", Toast.LENGTH_SHORT).show();
            }
            if(event.days.get(0).getDate() == 0){
                input_date.setError("Required");
            }
            if(event.days.get(0).getStartTime() == 0){
                input_start_time.setError("Required");
            }
            if(event.days.get(0).getEndTime() == 0){
                input_end_time.setError("Required");
            }
        }

    }

    private void uploadEventData() {
        pd.setIndeterminate(true);
        //pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setProgress(0);
        pd.show();

        FirebaseDatabase.getInstance().getReference().child("events").child(clubNameData).push().setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddNewEventActivity.this, "Project complete", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    AddNewEventActivity.this.finish();
                } else {
                    Toast.makeText(AddNewEventActivity.this, "Project Failed", Toast.LENGTH_SHORT).show();
                    pd.hide();
                }
            }
        });
    }

    ArrayList<StorageTask<UploadTask.TaskSnapshot>> uploadImagesToFirebase() {

        ArrayList<StorageTask<UploadTask.TaskSnapshot>> promises = new ArrayList<>();

        for (int i = 0; i < imgLocationsData.size(); i++) {
            String imgName = imgLocationsData.get(i).getLastPathSegment();

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(AddNewEventActivity.this.getContentResolver(), imgLocationsData.get(i));
                bmp = Bitmap.createScaledBitmap(bmp, 500, (int) ((float) bmp.getHeight() / bmp.getWidth() * 500), true);

                ByteArrayOutputStream boas = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, boas);

                imgName = imgName.replace('.', '@');
                int lastIndex = imgName.lastIndexOf('@');
                String imgExtension = imgName.substring(lastIndex + 1);
                StorageReference childRef = firebaseStorage.child(input_eventname.getText().toString() + "_" + i + "." + imgExtension);

                // To be put on final add event button to avoid useless uploads
                UploadTask uploadTask = childRef.putBytes(boas.toByteArray());
                StorageTask<UploadTask.TaskSnapshot> promise = uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(AddNewEventActivity.this, "Image Upload successful", Toast.LENGTH_SHORT).show();
                        Uri uri = taskSnapshot.getDownloadUrl();
                        assert uri != null;
                        event.photoID.posters.add(uri.toString());

                        int cur = pd.getProgress();

                        pd.setProgress((int) (cur + 100.0 / imgLocationsData.size()));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddNewEventActivity.this, "Image Upload failed", Toast.LENGTH_SHORT).show();
                    }
                });
                promises.add(promise);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return promises;
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
            case R.id.create_new_event:
                uploadEvent();
                return true;
            case android.R.id.home:
                onBackPressed();
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
                count++;
                //adding items to arraylist for storing data
                event.days.add(new TimeInterval(0, 0, 0));
                //Toast.makeText(AddNewEventActivity.this, "layout returned", Toast.LENGTH_SHORT).show();
                event_day_layout.addView(layoutreturner(count));

            }
        };

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"ResourceType", "SetTextI18n"})
    LinearLayout layoutreturner(int count) {
        LinearLayout rLayout = new LinearLayout(AddNewEventActivity.this);
        rLayout.setBackground(getResources().getDrawable(R.drawable.border_textview));
        Resources r = getResources();
        rLayout.setOrientation(LinearLayout.HORIZONTAL);
        int fifteen = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, r.getDisplayMetrics());
        int five = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
        //int sixty = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r.getDisplayMetrics());
        //int zero = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, r.getDisplayMetrics());

        //params for main layout
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(fifteen, fifteen, fifteen, fifteen);
        rLayout.setPadding(five, five, five, five);

        //creating a linear layout2
        LinearLayout lLayout = new LinearLayout(AddNewEventActivity.this);
        lLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lLayoutParams.setMargins(0, five, 0, 0);

        //creating layout params for textView
        LinearLayout.LayoutParams lparams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams1.gravity = Gravity.CENTER_VERTICAL;
        lparams1.setMargins(0, 0, five, 0);

        //creating layout params for date editText
        LinearLayout.LayoutParams lparams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //creating layout params for time editText
        LinearLayout.LayoutParams lparams3 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);


        //adding items to arraylist for adding edit text
        date.add(new EditText(AddNewEventActivity.this));
        startTime.add(new EditText(AddNewEventActivity.this));
        endTime.add(new EditText(AddNewEventActivity.this));

        //formatting the time editText
        date.get(count).setLayoutParams(lparams2);
        startTime.get(count).setLayoutParams(lparams3);
        endTime.get(count).setLayoutParams(lparams3);
        rLayout.setLayoutParams(layoutParams);
        lLayout.setLayoutParams(lLayoutParams);


        //setting hints to the edit text boxes
        startTime.get(count).setHint(R.string.start_time);
        date.get(count).setHint(R.string.date);
        endTime.get(count).setHint(R.string.end_time);

        //adding background to the edit text
        date.get(count).setBackground(getResources().getDrawable(android.R.drawable.editbox_background));
        startTime.get(count).setBackground(getResources().getDrawable(android.R.drawable.editbox_background));
        endTime.get(count).setBackground(getResources().getDrawable(android.R.drawable.editbox_background));


        //formatting the date editText
        date.get(count).setPadding(five, five, five, five);

        //setting text sizes of the edittext boxes
        startTime.get(count).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        endTime.get(count).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

        //user cannot click or paste in date and time edit texts
        date.get(count).setLongClickable(false);
        startTime.get(count).setLongClickable(false);
        endTime.get(count).setLongClickable(false);
        date.get(count).setFocusable(false);
        startTime.get(count).setFocusable(false);
        endTime.get(count).setFocusable(false);


        //adding on click listeners to edittext boxes
        date.get(count).setOnClickListener(createOnClickListenerDate(count));
        startTime.get(count).setOnClickListener(createOnClickListenerTime(count, true));
        endTime.get(count).setOnClickListener(createOnClickListenerTime(count, false));

        //creating text views
        TextView tvDay = new TextView(AddNewEventActivity.this);
        tvDay.setLayoutParams(lparams1);
        String dayText = "Day" + " " + (count + 1);
        tvDay.setText(dayText);
        tvDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tvDay.setPadding(five * 3, five * 3, five * 3, five * 3);


        TextView tvTo = new TextView(AddNewEventActivity.this);
        tvTo.setText("TO");
        tvTo.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvTo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);


        //adding items to the lower linear layout
        lLayout.addView(startTime.get(count));
        lLayout.addView(tvTo);
        lLayout.addView(endTime.get(count));

        LinearLayout llayout3 = new LinearLayout(this);
        llayout3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        llayout3.setOrientation(LinearLayout.VERTICAL);

        llayout3.addView(date.get(count));
        llayout3.addView(lLayout);

        //adding items to relative layout
        rLayout.addView(tvDay);
        rLayout.addView(llayout3);

        return rLayout;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    View.OnClickListener createOnClickListenerDate(final int i) {

        return new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(final View v) {
                input_date.setError(null);
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

                        event.days.get(i).setDate(myCalendar.getTimeInMillis());
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
                if(isStart)
                    input_start_time.setError(null);
                else
                    input_end_time.setError(null);
                final java.util.Calendar mcurrentDate = java.util.Calendar.getInstance();
                final int mHour = mcurrentDate.get(java.util.Calendar.HOUR_OF_DAY);
                final int mMinute = mcurrentDate.get(java.util.Calendar.MINUTE);

                TimePickerDialog mTimePicker = new TimePickerDialog(AddNewEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String displayTime = String.format(Locale.ENGLISH, "%02d:%02d", hourOfDay, minute);
                        if (isStart)
                            startTime.get(i).setText(displayTime);
                        else
                            endTime.get(i).setText(displayTime);

                        //Calendar cal = Calendar.getInstance();
                        //cal.setTimeInMillis(0);
                        //cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        //cal.set(Calendar.MINUTE, minute);

                        Toast.makeText(AddNewEventActivity.this, "" + TimeZone.getDefault().getRawOffset(), Toast.LENGTH_SHORT).show();
                        long time = 1000L * (hourOfDay * 60 * 60 + minute * 60) - TimeZone.getDefault().getRawOffset();
                        if (isStart)
                            event.days.get(i).setStartTime(time);
                        else
                            event.days.get(i).setEndTime(time);
                    }
                }, mHour, mMinute, true);

                mTimePicker.setTitle("Select date");
                mTimePicker.show();
            }
        };
    }


    @Override
    public void onBackPressed() {
        fillData();
        boolean isUntouched = (event.getEventVenue().equals("") &&
                event.getEventName().equals("") &&
                event.getEventDesc().equals("") &&
                event.coordinatorID.size() == 0 &&
                imgLocationsData.size() == 0 &&
                event.days.size() == 1 &&
                event.days.get(0).getDate() == 0 &&
                event.days.get(0).getStartTime() == 0 &&
                event.days.get(0).getEndTime() == 0
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
        event.setEventName(input_eventname.getText().toString().trim());
        event.setEventVenue(input_event_venue.getText().toString().trim());
        clubNameData = input_clubname.getText().toString().trim();
        event.setEventDesc(input_description.getText().toString().trim());
    }

    /*
    void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    */

    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
        coordinatorsAll.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseReadListener();
        //updateList();
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {
                        coordinatorsAll.add(dataSnapshot.getValue(Coordinator.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    updateList();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    try {
                        Coordinator coodinator = dataSnapshot.getValue(Coordinator.class);
                        for (Coordinator c : coordinatorsAll) {
                            assert coodinator != null;
                            if (c.getEmail().equals(coodinator.getEmail())) {
                                coordinatorsAll.remove(c);
                                break;
                            }
                        }
                        coordinatorsAll.add(coodinator);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    updateList();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void updateList() {
        sort(coordinatorsAll);
        coordinatorAdapter = new CoordinatorAdapter(
                this,
                R.layout.coordinator_item_view,
                R.id.coordinator_item_name,
                coordinatorsAll
        );
        input_event_cooordinator.setAdapter(coordinatorAdapter);
    }


    @SuppressLint("StaticFieldLeak")
    private class ImageUpload extends AsyncTask<Void, Void, Void> {
        final ArrayList<StorageTask<UploadTask.TaskSnapshot>> promises;
        boolean alluploaded;

        ImageUpload(ArrayList<StorageTask<UploadTask.TaskSnapshot>> storageTasks) {
            promises = storageTasks;
        }

        @Override
        protected Void doInBackground(Void... params) {

            for (int i = 0; i < promises.size(); i++) {
                while (!promises.get(i).isComplete()) ;
                if (!promises.get(i).isSuccessful())
                    alluploaded = false;
            }
            while (event.getPhotoID().getPosters().size() != promises.size()) ;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pd.hide();
            Toast.makeText(AddNewEventActivity.this, "Image Upload Complete!", Toast.LENGTH_SHORT).show();
            if (alluploaded) {
                uploadEventData();
            }
        }

        @Override
        protected void onPreExecute() {
            alluploaded = true;
        }
    }
}
