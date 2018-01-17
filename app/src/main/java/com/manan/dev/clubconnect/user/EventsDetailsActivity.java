package com.manan.dev.clubconnect.user;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.manan.dev.clubconnect.Adapters.UserSingleEventListAdapter;
import com.manan.dev.clubconnect.CircleTransform;
import com.manan.dev.clubconnect.EditEventActivity;
import com.manan.dev.clubconnect.Models.Coordinator;
import com.manan.dev.clubconnect.Models.Event;
import com.manan.dev.clubconnect.Models.TimeInterval;
import com.manan.dev.clubconnect.Models.UserData;
import com.manan.dev.clubconnect.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.security.cert.Extension;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.manan.dev.clubconnect.R.drawable.vector_star;

public class EventsDetailsActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST = 1024;
    //    private TextView eventDetailsToken;
    private String clubName;
    private String eventId;
    Event curEvent;
    private DatabaseReference mDBRefCurEvent;
    private ChildEventListener mChildEventListCurEvent;
    CollapsingToolbarLayout collapsingToolbarLayout;

    private ChildEventListener mChildEventListener;
    private DatabaseReference mDatabaseReference;

    private ChildEventListener getmChildUser;
    private DatabaseReference mDatabaseReferenceUsers;
    private Map<String, Coordinator> coordinatorsAll;

    TextView tvTime, tvDate, tvVenue, tvDetails, tvAttendees;
    LinearLayout llCoordinators;
    private AppBarLayout appBar;
    private FirebaseStorage storage;
    private ProgressDialog pd;
    private FirebaseAuth mAuth;
    private UserData user;
    private FloatingActionButton bookm;
    private FloatingActionButton going;
    private int togglebookm = 0;
    private int toggleGoing = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        appBar = (AppBarLayout) findViewById(R.id.app_bar);
        //collapsingToolbarLayout.setTitleEnabled(false);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        tvTime = (TextView) findViewById(R.id.tv_time);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvVenue = (TextView) findViewById(R.id.tv_venue);
        tvDetails = (TextView) findViewById(R.id.tv_details);
        tvAttendees = (TextView) findViewById(R.id.label_attendees_count_user);

        llCoordinators = (LinearLayout) findViewById(R.id.ll_coordinators);


        Bundle bundle = getIntent().getExtras();
        clubName = bundle.getString(UserSingleEventListAdapter.CLUB_NAME);
        eventId = bundle.getString(UserSingleEventListAdapter.EVENT_ID);

        mAuth = FirebaseAuth.getInstance();
        coordinatorsAll = new HashMap<>();

        mDatabaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child("users");
                   //Toast.makeText(EventsDetailsActivity.this,mAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();



        //eventDetailsToken = (TextView) findViewById(R.id.event_details_);


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!permissionGranted(android.Manifest.permission.READ_CALENDAR, android.Manifest.permission.WRITE_CALENDAR)) {
                    Toast.makeText(EventsDetailsActivity.this, "Read/Write Calendar Access Permission Denied!", Toast.LENGTH_SHORT);
                    askForPermission(android.Manifest.permission.READ_CALENDAR, android.Manifest.permission.WRITE_CALENDAR);
                    return;
                }

                addEventToCalender();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        bookm = (FloatingActionButton) findViewById(R.id.bookmark);

        bookm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            bookMarkTodb();
            }
        });

        going = (FloatingActionButton) findViewById(R.id.going);
        going.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goingTodb();
            }
        });

        mAuth = FirebaseAuth.getInstance();



  // Toast.makeText(EventsDetailsActivity.this,mAuth.getCurrentUser().getUid(),Toast.LENGTH_SHORT).show();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("coordinators").child(clubName);
        mDBRefCurEvent = FirebaseDatabase.getInstance().getReference().child("events").child(clubName);
        storage = FirebaseStorage.getInstance();

        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
//        Toast.makeText(EventsDetailsActivity.this, Integer.toString(curEvent.getPhotoID().getPosters().size()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                EventsDetailsActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addEventToCalender() {
        long date = curEvent.getDays().get(0).getDate();
        long startTime = curEvent.getDays().get(0).getStartTime();
        long endTime = curEvent.getDays().get(0).getEndTime();


        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.EventsEntity.CONTENT_URI)
                .setType("vnd.android.cursor.item/event")
                //.putExtra(CalendarContract.EventDays.STARTDAY, date)
                //.putExtra(CalendarContract.EventDays.ENDDAY, date)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime + date)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime + date)
                .putExtra(CalendarContract.EventsEntity.TITLE, curEvent.eventName)
                .putExtra(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
                .putExtra(CalendarContract.Reminders.MINUTES, 5);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseListener();
        curEvent = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        detatchDatabaseListener();
        curEvent = null;
        coordinatorsAll.clear();
    }

    private void detatchDatabaseListener() {
        if (mChildEventListCurEvent != null) {
            mDBRefCurEvent.removeEventListener(mChildEventListCurEvent);
            mChildEventListCurEvent = null;
        }
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }

        if (getmChildUser != null) {
            mDatabaseReferenceUsers.removeEventListener(getmChildUser);
            getmChildUser = null;
        }
    }

    private void attachDatabaseListener() {
        if (mChildEventListCurEvent == null) {
            mChildEventListCurEvent = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               //     Toast.makeText(EventsDetailsActivity.this, "this is 1", Toast.LENGTH_SHORT).show();
                    try {
                        Log.d("OnAdded", dataSnapshot.toString());
                        if (dataSnapshot.getKey().equals(eventId)) {
                            curEvent = dataSnapshot.getValue(Event.class);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    updateUI();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    try {
                        Log.d("OnChanged", dataSnapshot.toString());
                        if (dataSnapshot.getKey().equals(eventId)) {
                            curEvent = dataSnapshot.getValue(Event.class);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    updateUI();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getKey().equals(eventId)) {
                        curEvent = null;
                    }
                    updateUI();
                    Toast.makeText(EventsDetailsActivity.this, "Sorry! This event no longer exists!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mDBRefCurEvent.addChildEventListener(mChildEventListCurEvent);
        }

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {
                        Coordinator coordinator = dataSnapshot.getValue(Coordinator.class);
                        coordinatorsAll.put(coordinator.getEmail(), coordinator);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    updateList();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    try {
                        Coordinator coodinator = dataSnapshot.getValue(Coordinator.class);
                        coordinatorsAll.put(coodinator.getEmail(), coodinator);
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

        if (getmChildUser == null) {
            getmChildUser = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {

                        if(dataSnapshot.getKey().equals(mAuth.getCurrentUser().getUid())) {
                            user = dataSnapshot.getValue(UserData.class);
                            if(user.getBookmarked() == null) {
                                togglebookm = 0;
    //                            Toast.makeText(EventsDetailsActivity.this, "Black", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                if(user.getBookmarked().containsKey(eventId)) {
                                    togglebookm=1;
                                    bookm.setImageResource(R.drawable.vector_yellow_star);
                                 Toast.makeText(EventsDetailsActivity.this, "This Event is Bookmarked by you", Toast.LENGTH_SHORT).show();
                                }
                            }
                            if(user.getGoing() == null){
                                toggleGoing = 0;
                            }else {
                                if(user.getGoing().containsKey(eventId)){
                                    toggleGoing = 1;
                                    going.setImageResource(R.drawable.vector_going_yellow);
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    try {

                        if(dataSnapshot.getKey().equals(mAuth.getCurrentUser().getUid())) {
                            user = dataSnapshot.getValue(UserData.class);
                            if(user.getBookmarked().isEmpty()) {
                                togglebookm = 0;
 //                               Toast.makeText(EventsDetailsActivity.this, "Black", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                if(user.getBookmarked().containsKey(eventId)) {
                                    togglebookm=1;
                                    bookm.setImageResource(R.drawable.vector_yellow_star);
 //                                  Toast.makeText(EventsDetailsActivity.this, "Yellow", Toast.LENGTH_SHORT).show();
                                }
                            }
                            if(user.getGoing().isEmpty()){
                                toggleGoing = 0;
                            }else {
                                if(user.getGoing().containsKey(eventId)){
                                    toggleGoing = 1;
                                    going.setImageResource(R.drawable.vector_going_yellow);
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

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
            mDatabaseReferenceUsers.addChildEventListener(getmChildUser);
        }






    }

    private void updateUI() {
        if (curEvent == null)
            return;

        Log.d("updateUI", curEvent.getEventName());
        collapsingToolbarLayout.setTitle(curEvent.getEventName());

        if(curEvent.getAttendees() == null){
            curEvent.setAttendees(new ArrayList<String>());
        }
        String counter = Integer.toString(curEvent.getAttendees().size());
        tvAttendees.setText(counter);

        if (curEvent.getEventDesc() != null)
            tvDetails.setText(curEvent.getEventDesc());

        if (curEvent.getDays().get(0) != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(curEvent.getDays().get(0).getDate());
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            String formattedDate = sdf.format(cal.getTime());
            tvDate.setText(formattedDate);

            cal.setTimeInMillis(curEvent.getDays().get(0).getStartTime());
            sdf = new SimpleDateFormat("HH:mm", Locale.US);
            formattedDate = sdf.format(cal.getTime());
            tvTime.setText(formattedDate);

            LinearLayout llContainer = (LinearLayout) findViewById(R.id.ll_timmings_all);
            llContainer.removeAllViews();

            for (TimeInterval ti : curEvent.getDays()) {
                LinearLayout ll = (LinearLayout) LayoutInflater.from(EventsDetailsActivity.this).inflate(R.layout.user_single_event_item_timmings, null, false);

                TextView tvDateTimmings = (TextView) ll.findViewById(R.id.tv_date);
                TextView tvTimeTimmings = (TextView) ll.findViewById(R.id.tv_duration);

                Calendar cal1 = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();
                SimpleDateFormat sdf1,sdf2;
                String formattedDate1;

                cal1.setTimeInMillis(ti.getDate());
                sdf1 = new SimpleDateFormat("EEEE, dd MMMM", Locale.US);
               // sdf2 = new SimpleDateFormat("EEEE");
                //formattedDate1 = sdf2.format(cal1.getTime())+ ", "+sdf1.format(cal1.getTime());
                formattedDate1 = sdf1.format(cal1.getTime());
                tvDateTimmings.setText(formattedDate1);

                cal1.setTimeInMillis(ti.getStartTime());
                cal2.setTimeInMillis(ti.getEndTime());
                sdf1 = new SimpleDateFormat("HH:mm", Locale.US);
                sdf2 = new SimpleDateFormat("HH:mm", Locale.US);
                formattedDate1 = sdf1.format(cal1.getTime())+ " - " + sdf2.format(cal2.getTime());
                tvTimeTimmings.setText(formattedDate1);

                llContainer.addView(ll);
            }
        }

        if (curEvent.getEventVenue() != null)
            tvVenue.setText(curEvent.getEventVenue());

        ((LinearLayout) findViewById(R.id.ll_posters)).removeAllViews();

        if (curEvent.photoID != null && curEvent.photoID.posters != null) {
            findViewById(R.id.cv_posters).setVisibility(View.VISIBLE);
            for (int i = 0; i < curEvent.photoID.posters.size(); i++) {
                final String url = curEvent.photoID.posters.get(i);
                ImageView iv = new ImageView(this);
                iv.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.dp80),(int)getResources().getDimension(R.dimen.dp80) ));
                iv.setPadding(0, 0, (int) EventsDetailsActivity.this.getResources().getDimension(R.dimen.dp10), 0);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                final int finalI = i + 1;
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!permissionGranted(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            Toast.makeText(EventsDetailsActivity.this, "Write External Storage Permission Denied!", Toast.LENGTH_SHORT).show();
                            askForPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE);
                            return;
                        }

                        StorageReference httpsReference = storage.getReferenceFromUrl(url);
                        pd.show();
                        //File localFile = null;
                        try {
                            final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), curEvent.getEventName() + "_" + finalI + ".jpg");
                            httpsReference.getFile(file).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {


                                        Uri path = Uri.fromFile(file);

                                        MediaScannerConnection.scanFile(EventsDetailsActivity.this,
                                                new String[]{file.getAbsolutePath()},
                                                null,
                                                null);

                                        //Get File MIME type
                                        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpg");

                                        //Open the file
                                        Intent fileOpenIntent = new Intent(Intent.ACTION_VIEW);
                                        fileOpenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        fileOpenIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        fileOpenIntent.setDataAndType(path, type);
                                        startActivity(fileOpenIntent);
                                    }
                                    pd.dismiss();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                ((LinearLayout) findViewById(R.id.ll_posters)).addView(iv);
                Picasso.with(EventsDetailsActivity.this).load(url).into(iv);
            }
        }

        Picasso.with(this).load(curEvent.getPhotoID().getPosters().get(0)).into(new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                float tWidth = appBar.getWidth();
                float tHei = appBar.getHeight();
                float finalRatio = tWidth / tHei;
                Bitmap resizedbitmap;
                if (bitmap.getHeight() <= bitmap.getWidth()) {
                    float finalWidth = bitmap.getHeight() * finalRatio;
                    float x0 = Math.max(0, (bitmap.getWidth() - finalWidth) / 2);
                    float w = finalWidth;
                    if (x0 + w > bitmap.getWidth())
                        w = bitmap.getWidth();
                    resizedbitmap = Bitmap.createBitmap(bitmap, (int) x0, 0, (int) w, bitmap.getHeight());
                } else {
                    float finalHei = bitmap.getWidth() / finalRatio;
                    float y0 = Math.max(0, (bitmap.getHeight() - finalHei) / 2);
                    float h = finalHei;
                    if (y0 + h > bitmap.getHeight())
                        h = bitmap.getHeight();
                    resizedbitmap = Bitmap.createBitmap(bitmap, 0, (int) y0, bitmap.getWidth(), (int) h);
                }

                appBar.setBackground(new BitmapDrawable(EventsDetailsActivity.this.getResources(), resizedbitmap));
            }

            @Override
            public void onBitmapFailed(final Drawable errorDrawable) {
                Log.d("PicassoPoster", "FAILED");
            }

            @Override
            public void onPrepareLoad(final Drawable placeHolderDrawable) {
                Log.d("PicassoPoster", "Prepare Load");
            }
        });

        updateList();
    }

    private boolean permissionGranted(String permission, String permission2) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                ||
                (
                        ContextCompat.checkSelfPermission(EventsDetailsActivity.this, permission)
                            == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(EventsDetailsActivity.this, permission2)
                            == PackageManager.PERMISSION_GRANTED
                )
                ;
    }


    private void askForPermission(String permission, String permission2) {
        ActivityCompat.requestPermissions(EventsDetailsActivity.this,
                new String[]{permission, permission2},
                MY_PERMISSIONS_REQUEST);
    }

    private void goingTodb() {
        if(toggleGoing == 1){
            user.getGoing().remove(eventId);
            if(curEvent.getAttendees().size() > 0 ){
                Toast.makeText(EventsDetailsActivity.this, curEvent.getAttendees().get(0), Toast.LENGTH_SHORT).show();
            }
            curEvent.getAttendees().remove(mAuth.getCurrentUser().getUid());
            FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                going.setImageResource(R.drawable.vector_going);
                                toggleGoing = 0;
                                FirebaseDatabase.getInstance().getReference().child("events").child(clubName).child(eventId).setValue(curEvent)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(EventsDetailsActivity.this, "REMOVED", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                Toast.makeText(EventsDetailsActivity.this, "Going removed Successfully", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(EventsDetailsActivity.this, "CHECK YOUR NET BRO!!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        if(toggleGoing == 0){
            if(user.getGoing() == null){
                user.setGoing(new HashMap<String, String>());
            }
            if(curEvent.getAttendees() == null){
                Toast.makeText(EventsDetailsActivity.this, "nhi hai abhi tak", Toast.LENGTH_SHORT).show();
                curEvent.setAttendees(new ArrayList<String>());
            }
            user.getGoing().put(eventId, clubName);
            curEvent.getAttendees().add(mAuth.getCurrentUser().getUid());
            Toast.makeText(EventsDetailsActivity.this, curEvent.getAttendees().get(0), Toast.LENGTH_SHORT).show();
            FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                toggleGoing = 1;
                                going.setImageResource(R.drawable.vector_going_yellow);
                                FirebaseDatabase.getInstance().getReference().child("events").child(clubName).child(eventId).setValue(curEvent)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(EventsDetailsActivity.this, "GOING", Toast.LENGTH_SHORT).show();
                                                }
                                                else {
                                                    Toast.makeText(EventsDetailsActivity.this, "Panga ho gya", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                Toast.makeText(EventsDetailsActivity.this, "Going Succesfully", Toast.LENGTH_SHORT).show();
                            }else
                                Toast.makeText(EventsDetailsActivity.this, "CHECK YOUR NETWORK BRO!!", Toast.LENGTH_SHORT).show();

                        }
                    });

        }

    }

    private void bookMarkTodb()
    {
        if(togglebookm == 1 )
        {
            user.getBookmarked().remove(eventId);
            FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                bookm.setImageResource(R.drawable.vector_star);
                                togglebookm = 0;
                                Toast.makeText(EventsDetailsActivity.this, "Bookmark removed Successfully", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(EventsDetailsActivity.this, "CHECK YOUR NET BRO!!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        if(togglebookm == 0)
        {

            if(user.getBookmarked()!= null) {
                user.getBookmarked().put(eventId, clubName);
            }
            if(user.getBookmarked()== null) {
                user.setBookmarked(new HashMap<String, String>());
                user.getBookmarked().put(eventId, clubName);
                //       Toast.makeText(EventsDetailsActivity.this, user.getBookmarked().get(1), Toast.LENGTH_SHORT).show();

            }
            FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                togglebookm = 1;
                                Toast.makeText(EventsDetailsActivity.this, "Bookmarked Succesfully", Toast.LENGTH_SHORT).show();
                            }else
                                Toast.makeText(EventsDetailsActivity.this, "CHECK YOUR NETWORK BRO!!", Toast.LENGTH_SHORT).show();

                        }
                    });

        }


    }





    private void updateList() {
        llCoordinators.removeAllViews();

        if(curEvent==null) return;

        if (curEvent.getCoordinatorID() == null)
            return;

        for (String coordEmailId : curEvent.getCoordinatorID()) {
            Coordinator coordinator = coordinatorsAll.get(coordEmailId);
            if (coordinator == null) continue;

            LinearLayout ll = (LinearLayout) LayoutInflater.from(EventsDetailsActivity.this).inflate(R.layout.coordinators_event_details_display, null, false);
            TextView tvName = (TextView) ll.findViewById(R.id.tv_org_name);
            TextView tvPhone = (TextView) ll.findViewById(R.id.tv_phone_no);

            tvName.setText(coordinator.getName());
            tvPhone.setText(coordinator.getPhone());

            Picasso.with(EventsDetailsActivity.this).load(coordinator.getPhoto()).resize(200, 200).centerCrop().transform(new CircleTransform()).into((ImageView) ll.findViewById(R.id.iv_photo));

            llCoordinators.addView(ll);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        ) {
                    Toast.makeText(EventsDetailsActivity.this, permissions[0] + " and " + permissions[1] + " granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EventsDetailsActivity.this, permissions[0] + " or " + permissions[1] + " denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

}
