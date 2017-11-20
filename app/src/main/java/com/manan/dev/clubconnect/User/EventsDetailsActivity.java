package com.manan.dev.clubconnect.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.manan.dev.clubconnect.Adapters.UserSingleEventListAdapter;
import com.manan.dev.clubconnect.CircleTransform;
import com.manan.dev.clubconnect.Models.Coordinator;
import com.manan.dev.clubconnect.Models.Event;
import com.manan.dev.clubconnect.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EventsDetailsActivity extends AppCompatActivity {

    //    private TextView eventDetailsToken;
    private String clubName;
    private String eventId;
    Event curEvent;
    private DatabaseReference mDBRefCurEvent;
    private ChildEventListener mChildEventListCurEvent;
    CollapsingToolbarLayout collapsingToolbarLayout;

    private ChildEventListener mChildEventListener;
    private DatabaseReference mDatabaseReference;

    private Map<String, Coordinator> coordinatorsAll;

    TextView tvTime, tvDate, tvVenue, tvDetails;
    LinearLayout llCoordinators;
    private AppBarLayout appBar;
    private FirebaseStorage storage;
    private ProgressDialog pd;

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

        llCoordinators = (LinearLayout) findViewById(R.id.ll_coordinators);


        Bundle bundle = getIntent().getExtras();
        clubName = bundle.getString(UserSingleEventListAdapter.CLUB_NAME);
        eventId = bundle.getString(UserSingleEventListAdapter.EVENT_ID);

        coordinatorsAll = new HashMap<>();

        //eventDetailsToken = (TextView) findViewById(R.id.event_details_);


   FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEventToCalender();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("coordinators").child(clubName);
        mDBRefCurEvent = FirebaseDatabase.getInstance().getReference().child("events").child(clubName);
        storage = FirebaseStorage.getInstance();

        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
//        Toast.makeText(EventsDetailsActivity.this, Integer.toString(curEvent.getPhotoID().getPosters().size()), Toast.LENGTH_SHORT).show();
    }

    private void addEventToCalender(){
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
                .putExtra(CalendarContract.Reminders.MINUTES,5);
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
    }

    private void attachDatabaseListener() {
        if (mChildEventListCurEvent == null) {
            mChildEventListCurEvent = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
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
    }

    private void updateUI() {
        if (curEvent == null)
            return;

        Log.d("updateUI", curEvent.getEventName());
        collapsingToolbarLayout.setTitle(curEvent.getEventName());

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
        }

        if (curEvent.getEventVenue() != null)
            tvVenue.setText(curEvent.getEventVenue());

        ((LinearLayout) findViewById(R.id.ll_posters)).removeAllViews();

        if (curEvent.photoID != null && curEvent.photoID.posters != null) {
            findViewById(R.id.cv_posters).setVisibility(View.VISIBLE);
            for (int i=0; i<curEvent.photoID.posters.size(); i++) {
                final String url = curEvent.photoID.posters.get(i);
                ImageView iv = new ImageView(this);
                iv.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
                iv.setPadding(0, 0, (int) EventsDetailsActivity.this.getResources().getDimension(R.dimen.dp30), 0);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                final int finalI = i+1;
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StorageReference httpsReference = storage.getReferenceFromUrl(url);
                        pd.show();
                        //File localFile = null;
                        try {
                            final File file = new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),curEvent.getEventName()+"_" +finalI +".jpg");
                            httpsReference.getFile(file).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        Uri path = Uri.fromFile(file);

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

    private void updateList() {
        llCoordinators.removeAllViews();

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

}
