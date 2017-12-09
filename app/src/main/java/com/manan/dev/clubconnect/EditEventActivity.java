package com.manan.dev.clubconnect;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.manan.dev.clubconnect.EditEvent.EventDetails;
import com.manan.dev.clubconnect.EditEvent.EventName;
import com.manan.dev.clubconnect.EditEvent.EventTimings;
import com.manan.dev.clubconnect.EditEvent.EventVenue;
import com.manan.dev.clubconnect.EditEvent.ModifyCoordinators;
import com.manan.dev.clubconnect.Models.Coordinator;
import com.manan.dev.clubconnect.Models.Event;
import com.manan.dev.clubconnect.Models.TimeInterval;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.lang.Math.min;
import static java.util.Collections.sort;

public class EditEventActivity extends AppCompatActivity {

    private static final int REQ_ID_NAME = 1;
    private static final int REQ_ID_POSTER = 2;
    private static final int REQ_ID_VENUE = 3;
    private static final int REQ_ID_DETAILS = 4;
    private static final int REQ_ID_COORDS = 5;
    private static final int REQ_ID_TIMINGS = 6;
    private static final int REQ_ID_TIMINGS_EDIT = 7;
    private static final int REQ_ID_ATTACH_IMAGE = 8;
    public static final String REQ_PARA_EVENT_NAME = "event_name";
    public static final String REQ_PARA_EVENT_VENUE = "event_venue";
    public static final String REQ_PARA_EVENT_DETAILS = "event_details";
    public static final String REQ_PARA_EVENT_COORD_NAME = "coord_name";
    public static final String REQ_PARA_EVENT_COORD_PHONE = "coord_phone";
    public static final String REQ_PARA_EVENT_COORD_PHOTO = "coord_photo";
    public static final String REQ_PARA_EVENT_COORD_EMAIL = "coord_email";
    public static final String REQ_PARA_EVENT_DATE = "event_date";
    public static final String REQ_PARA_EVENT_STIME = "event_stime";
    public static final String REQ_PARA_EVENT_ETIME = "event_etime";

    private static final String TAG = "EditEventActivity";

    Event event;

    private Map<String, Coordinator> coordinatorsAll;
    private ArrayList<Uri> posters;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBar;
    private TextView tvVenue;
    private TextView tvDetails;
    private LinearLayout llCoordinators;
    private LinearLayout llTimings;
    private TimeInterval editTimingReqSentFor;
    private LinearLayout llPosters;
    private ImageView ivMainPoster;
    private TextView tvStartTime;
    private TextView tvStartDate;

    private ProgressDialog pd;

    private ValueEventListener mChildOnlineOfflineEventListener;
    private boolean isOnline;
    private DatabaseReference connectedRef;
    private StorageReference firebaseStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initDataVariables();

        initViews();

        setListenersForButtons();

        initForEventUpload();
    }

    private void initDataVariables() {
        event = new Event();
        event.coordinatorID = new ArrayList<>();
        event.days = new ArrayList<>();
        event.photoID.posters = new ArrayList<>();
        event.photoID.afterEvent = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            return;
        }
        event.setClubName(user.getDisplayName());

        coordinatorsAll = new HashMap<>();
        posters = new ArrayList<>();
        posters.add(null);

        editTimingReqSentFor = null;

    }

    private void initViews() {
        tvVenue = findViewById(R.id.tv_venue);
        tvDetails = findViewById(R.id.tv_details);
        llCoordinators = findViewById(R.id.ll_coordinators);
        llTimings = findViewById(R.id.ll_timmings_all);
        llPosters = findViewById(R.id.ll_posters);
        ivMainPoster = findViewById(R.id.iv_main_poster);
        tvStartTime = findViewById(R.id.tv_time);
        tvStartDate = findViewById(R.id.tv_date);

        appBar = findViewById(R.id.app_bar);
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);


        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
    }

    private void setListenersForButtons() {
        View selectPoster = collapsingToolbarLayout.findViewById(R.id.ll_select_poster);
        if (selectPoster != null) {
            selectPoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(intent, "Select Poster"), REQ_ID_POSTER);
                }
            });
        }

        View editName = collapsingToolbarLayout.findViewById(R.id.iv_edit_name);
        if (editName != null) {
            editName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String eventName = event.getEventName();
                    Intent intent = new Intent(EditEventActivity.this, EventName.class);
                    if (eventName != null)
                        intent.putExtra(REQ_PARA_EVENT_NAME, eventName);
                    startActivityForResult(intent, REQ_ID_NAME);
                }
            });
        }

        View editVenue = findViewById(R.id.iv_edit_venue);
        if (editVenue != null) {
            editVenue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String eventVenue = event.getEventVenue();
                    Intent intent = new Intent(EditEventActivity.this, EventVenue.class);
                    if (eventVenue != null)
                        intent.putExtra(REQ_PARA_EVENT_VENUE, eventVenue);
                    startActivityForResult(intent, REQ_ID_VENUE);
                }
            });
        }

        View editDetails = findViewById(R.id.tv_edit_details);
        if (editDetails != null) {
            editDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String eventDetails = event.getEventDesc();
                    Intent intent = new Intent(EditEventActivity.this, EventDetails.class);
                    if (eventDetails != null)
                        intent.putExtra(REQ_PARA_EVENT_DETAILS, eventDetails);
                    startActivityForResult(intent, REQ_ID_DETAILS);
                }
            });
        }

        View modifyCoordinators = findViewById(R.id.tvAddCoords);
        if (modifyCoordinators != null) {
            modifyCoordinators.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> name, phone, email, photo;
                    name = new ArrayList<>();
                    phone = new ArrayList<>();
                    email = new ArrayList<>();
                    photo = new ArrayList<>();
                    fromCoordinatorsToArrayList(name, phone, email, photo);

                    Intent intent = new Intent(EditEventActivity.this, ModifyCoordinators.class);
                    //Toast.makeText(EditEventActivity.this, event.getCoordinatorID().size(),Toast.LENGTH_SHORT).show();
                    if (name.size() > 0) {
                        intent.putExtra(REQ_PARA_EVENT_COORD_NAME, name);
                        intent.putExtra(REQ_PARA_EVENT_COORD_PHONE, phone);
                        intent.putExtra(REQ_PARA_EVENT_COORD_PHOTO, photo);
                        intent.putExtra(REQ_PARA_EVENT_COORD_EMAIL, email);
                    }
                    startActivityForResult(intent, REQ_ID_COORDS);
                }
            });
        }
        View addTimings = findViewById(R.id.tvAddTimings);
        if (addTimings != null) {
            addTimings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(EditEventActivity.this, EventTimings.class);
                    startActivityForResult(intent, REQ_ID_TIMINGS);
                }
            });
        }

        View addPosters = findViewById(R.id.tvAddPosters);
        if (addPosters != null) {
            addPosters.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), REQ_ID_ATTACH_IMAGE);
                }
            });
        }

    }

    private void initForEventUpload() {
        isOnline = false;
        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        firebaseStorage = FirebaseStorage.getInstance().getReference();
    }

    void fromCoordinatorsToArrayList(ArrayList<String> name, ArrayList<String> phone, ArrayList<String> email, ArrayList<String> photo) {
        name.clear();
        phone.clear();
        email.clear();
        photo.clear();
        for (String coordId : event.getCoordinatorID()) {
            Coordinator coord = coordinatorsAll.get(coordId);
            if (coord == null)
                continue;
            name.add(coord.getName());
            phone.add(coord.getPhone());
            email.add(coord.getEmail());
            photo.add(coord.getPhoto());
        }
    }

    void upDateCoordinators(ArrayList<String> name, ArrayList<String> phone, ArrayList<String> email, ArrayList<String> photo) {
        llCoordinators.removeAllViews();

        if (name.size() == 0) {
            LinearLayout ll = (LinearLayout) LayoutInflater.from(EditEventActivity.this).inflate(R.layout.coordinators_event_details_display, null, false);
            llCoordinators.addView(ll);
        }

        for (int i = 0; i < name.size(); i++) {
            LinearLayout ll = (LinearLayout) LayoutInflater.from(EditEventActivity.this).inflate(R.layout.coordinators_event_details_display, null, false);
            TextView tvName = ll.findViewById(R.id.tv_org_name);
            TextView tvPhone = ll.findViewById(R.id.tv_phone_no);

            tvName.setText(name.get(i));
            tvPhone.setText(phone.get(i));

            Picasso.with(EditEventActivity.this).load(photo.get(i)).resize(200, 200).centerCrop().transform(new CircleTransform()).into((ImageView) ll.findViewById(R.id.iv_photo));

            llCoordinators.addView(ll);

            coordinatorsAll.put(email.get(i), new Coordinator(name.get(i), email.get(i), phone.get(i), photo.get(i)));

        }
    }

    void updatePoster() {
        if (event.getPhotoID().getPosters().size() > 0) {
        }
    }

    void updateUIText() {
        if (event.getEventName() != null)
            collapsingToolbarLayout.setTitle(event.getEventName());
        if (event.getEventVenue() != null)
            tvVenue.setText(event.getEventVenue());
        if (event.getEventDesc() != null)
            tvDetails.setText(event.getEventDesc());

        llTimings.removeAllViews();

        if (event.getDays().size() == 0) {
            LinearLayout ll = (LinearLayout) LayoutInflater.from(EditEventActivity.this).inflate(R.layout.user_single_event_item_timmings, null, false);
            TextView tvDateTimmings = ll.findViewById(R.id.tv_date);
            TextView tvTimeTimmings = ll.findViewById(R.id.tv_duration);
            tvDateTimmings.setText(getResources().getString(R.string.default_date));
            tvTimeTimmings.setText(getResources().getString(R.string.default_duration));
            llTimings.addView(ll);

            tvStartDate.setText(getResources().getString(R.string.default_start_date));
            tvStartTime.setText(getResources().getString(R.string.default_start_time));
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(event.getDays().get(0).getDate());
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            String formattedDate = sdf.format(cal.getTime());
            tvStartDate.setText(formattedDate);

            cal.setTimeInMillis(event.getDays().get(0).getStartTime());
            sdf = new SimpleDateFormat("HH:mm", Locale.US);
            formattedDate = sdf.format(cal.getTime());
            tvStartTime.setText(formattedDate);
        }
        for (final TimeInterval ti : event.getDays()) {
            LinearLayout ll = (LinearLayout) LayoutInflater.from(EditEventActivity.this).inflate(R.layout.user_single_event_item_timmings, null, false);
            TextView tvDateTimmings = ll.findViewById(R.id.tv_date);
            TextView tvTimeTimmings = ll.findViewById(R.id.tv_duration);
            View editTimings = ll.findViewById(R.id.iv_edit_timings);
            editTimings.setVisibility(View.VISIBLE);
            editTimings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(EditEventActivity.this, EventTimings.class);
                    intent.putExtra(EditEventActivity.REQ_PARA_EVENT_DATE, ti.getDate());
                    intent.putExtra(EditEventActivity.REQ_PARA_EVENT_STIME, ti.getStartTime());
                    intent.putExtra(EditEventActivity.REQ_PARA_EVENT_ETIME, ti.getEndTime());
                    editTimingReqSentFor = ti;
                    startActivityForResult(intent, REQ_ID_TIMINGS_EDIT);
                }
            });

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            SimpleDateFormat sdf1, sdf2;
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
            formattedDate1 = sdf1.format(cal1.getTime()) + " - " + sdf2.format(cal2.getTime());
            tvTimeTimmings.setText(formattedDate1);

            llTimings.addView(ll);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case REQ_ID_NAME:
                if (data != null) {
                    event.setEventName(data.getStringExtra(REQ_PARA_EVENT_NAME));
                    updateUIText();
                }
                break;
            case REQ_ID_VENUE:
                if (data != null) {
                    event.setEventVenue(data.getStringExtra(REQ_PARA_EVENT_VENUE));
                    updateUIText();
                }
                break;
            case REQ_ID_DETAILS:
                if (data != null) {
                    event.setEventDesc(data.getStringExtra(REQ_PARA_EVENT_DETAILS));
                    updateUIText();
                }
                break;
            case REQ_ID_COORDS:
                if (data != null) {
                    ArrayList<String> name, phone, email, photo;
                    name = data.getStringArrayListExtra(REQ_PARA_EVENT_COORD_NAME);
                    phone = data.getStringArrayListExtra(REQ_PARA_EVENT_COORD_PHONE);
                    photo = data.getStringArrayListExtra(REQ_PARA_EVENT_COORD_PHOTO);
                    email = data.getStringArrayListExtra(REQ_PARA_EVENT_COORD_EMAIL);

                    event.setCoordinatorID(email);
                    upDateCoordinators(name, phone, email, photo);
                }
                break;
            case REQ_ID_TIMINGS:
                if (data != null) {
                    long date = data.getLongExtra(REQ_PARA_EVENT_DATE, 0);
                    long stime = data.getLongExtra(REQ_PARA_EVENT_STIME, 0);
                    long etime = data.getLongExtra(REQ_PARA_EVENT_ETIME, 0);

                    event.getDays().add(new TimeInterval(date, stime, etime));
                    sort(event.getDays());
                    updateUIText();
                }
                break;
            case REQ_ID_TIMINGS_EDIT:
                if (data == null) {
                    event.getDays().remove(editTimingReqSentFor);
                    updateUIText();
                } else {
                    long date = data.getLongExtra(REQ_PARA_EVENT_DATE, 0);
                    long stime = data.getLongExtra(REQ_PARA_EVENT_STIME, 0);
                    long etime = data.getLongExtra(REQ_PARA_EVENT_ETIME, 0);

                    if (date == 0) {
                        event.getDays().remove(editTimingReqSentFor);
                        updateUIText();
                        break;
                    }

                    editTimingReqSentFor.setDate(date);
                    editTimingReqSentFor.setStartTime(stime);
                    editTimingReqSentFor.setEndTime(etime);

                    sort(event.getDays());
                    updateUIText();
                }
                break;
            case REQ_ID_POSTER:
                if (data != null && data.getData() != null) {
                    Uri localData = data.getData();
                    posters.set(0, localData);
                    try {
                        Bitmap bitmap;
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), localData);
                        bitmap = getAppBarFriendlyBitmap(bitmap);
                        appBar.setBackground(new BitmapDrawable(EditEventActivity.this.getResources(), bitmap));
                        float finalWidth = min(100, bitmap.getWidth());
                        bitmap = Bitmap.createScaledBitmap(bitmap, (int) finalWidth, (int) (finalWidth / bitmap.getWidth() * bitmap.getHeight()),
                                true);
                        ivMainPoster.setImageBitmap(bitmap);
                        ivMainPoster.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQ_ID_ATTACH_IMAGE:
                if (data != null && data.getData() != null) {
                    Uri localData = data.getData();
                    posters.add(localData);
                    final ImageView iv = addNewHolderForImage(llPosters, localData);

                    try {
                        Bitmap bitmap;
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), localData);
                        float finalWidth = min(100, bitmap.getWidth());
                        bitmap = Bitmap.createScaledBitmap(bitmap, (int) finalWidth, (int) (finalWidth / bitmap.getWidth() * bitmap.getHeight()),
                                true);
                        iv.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

    }

    private ImageView addNewHolderForImage(final LinearLayout container, final Uri localData) {
        final RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams rlLayoutParams = new RelativeLayout.LayoutParams((int) getResources().getDimension(R.dimen.dp80), (int) getResources().getDimension(R.dimen.dp80));
        relativeLayout.setLayoutParams(rlLayoutParams);
        relativeLayout.setPadding(0, 0, (int) getResources().getDimension(R.dimen.ten), 0);

        ImageView imageViewData = new ImageView(this);
        imageViewData.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageViewData.setCropToPadding(true);
        imageViewData.setScaleType(ImageView.ScaleType.CENTER_CROP);

        ImageView imageView = new ImageView(this);
        RelativeLayout.LayoutParams ivLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ivLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        ivLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        imageView.setLayoutParams(ivLayoutParams);
        imageView.setBackgroundColor(Color.argb(0xaa, 140, 140, 140));
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.vector_clear));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!posters.remove(localData))
                    Toast.makeText(EditEventActivity.this, "Sorry, the image was not removed compeltely!", Toast.LENGTH_SHORT).show();
                container.removeView(relativeLayout);
            }
        });

        relativeLayout.addView(imageViewData);
        relativeLayout.addView(imageView);

        container.addView(relativeLayout);

        return imageViewData;
    }

    private Bitmap getAppBarFriendlyBitmap(Bitmap bitmap) {
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

        return resizedbitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_event:
                if (isOnline)
                    uploadEvent();
                else
                    Toast.makeText(this, "Sorry! You are offline!", Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_discard:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void uploadEvent() {
        if (event.getEventName()==null || event.getEventName().equals("")) {
            Toast.makeText(EditEventActivity.this, "Event Name cannot be empty!", Toast.LENGTH_SHORT).show();
        } else if (event.getEventVenue()==null || event.getEventVenue().equals("")) {
            Toast.makeText(EditEventActivity.this, "Event Venue cannot be empty!", Toast.LENGTH_SHORT).show();
        } else if (event.getEventDesc()==null || event.getEventDesc().equals("")) {
            Toast.makeText(EditEventActivity.this, "Event Description cannot be empty!", Toast.LENGTH_SHORT).show();
        } else if (event.coordinatorID.size() == 0) {
            Toast.makeText(EditEventActivity.this, "There should be atleast 1 coordinator", Toast.LENGTH_SHORT).show();
        } else if (event.getDays().size() == 0) {
            Toast.makeText(EditEventActivity.this, "Event Timings cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (posters.get(0) == null) {
            Toast.makeText(EditEventActivity.this, "Event cannot be uploaded without a poster", Toast.LENGTH_SHORT).show();
        } else {
            pd.setIndeterminate(false);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setMax(posters.size());
            pd.show();
            new ImageUpload(uploadImagesToFirebase()).execute();
        }
    }

    private void uploadEventData() {
        Log.d(TAG, "Uploading Event Data");

        pd.setIndeterminate(true);
        //pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //pd.setProgress(100);
        pd.show();

        FirebaseDatabase.getInstance().getReference().child("events").child(event.getClubName()).push().setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Uploading Event Data Completed");

                if (task.isSuccessful()) {
                    Toast.makeText(EditEventActivity.this, "Event Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    EditEventActivity.this.finish();
                } else {
                    Toast.makeText(EditEventActivity.this, "Event Uploading Failed!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failure detected");
            }
        });
    }

    ArrayList<StorageTask<UploadTask.TaskSnapshot>> uploadImagesToFirebase() {

        ArrayList<StorageTask<UploadTask.TaskSnapshot>> promises = new ArrayList<>();

        event.getPhotoID().getPosters().clear();
        for(int i=0; i<posters.size(); i++)
            event.getPhotoID().getPosters().add(null);

        for (int i = 0; i < posters.size(); i++) {

            Bitmap bmp;
            try {
                bmp = MediaStore.Images.Media.getBitmap(EditEventActivity.this.getContentResolver(), posters.get(i));
                bmp = Bitmap.createScaledBitmap(bmp, 500, (int) ((float) bmp.getHeight() / bmp.getWidth() * 500), true);

                ByteArrayOutputStream boas = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, boas);

                StorageReference childRef = firebaseStorage.child(event.getEventName() + "_"+event.getDays().get(0).getDate() + "_" + i);

                UploadTask uploadTask = childRef.putBytes(boas.toByteArray());
                final int finalI = i;
                StorageTask<UploadTask.TaskSnapshot> promise = uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "Image Upload Successful!");
                        //Toast.makeText(AddNewEventActivity.this, "Image Upload successful", Toast.LENGTH_SHORT).show();
                        Uri uri = taskSnapshot.getDownloadUrl();
                        assert uri != null;
                        event.photoID.posters.set(finalI, uri.toString());

                        int cur = pd.getSecondaryProgress();
                        pd.setSecondaryProgress(cur + 1);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditEventActivity.this, "Image Upload failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.setProgress((int) (((float) taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount()) * 100);
                    }
                });
                promises.add(promise);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return promises;
    }


    boolean isUntouched() {
        return (event.getEventVenue()==null &&
                (event.getEventName()==null || event.getEventName().trim().equals(""))&&
                (event.getEventDesc()==null || event.getEventDesc().trim().equals(""))&&
                event.coordinatorID.size() == 0 &&
                posters.size() == 1 &&
                posters.get(0) == null &&
                event.days.size() == 0
        );
    }

    @Override
    public void onBackPressed() {
        if (isUntouched()) {
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
                        EditEventActivity.this.finish();
                    }
                });
        builder.create();
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        attackOnlineOfflineReadListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachOnlineOfflineReadListener();
    }

    private void detachOnlineOfflineReadListener() {
        if (mChildOnlineOfflineEventListener != null) {
            connectedRef.removeEventListener(mChildOnlineOfflineEventListener);
            mChildOnlineOfflineEventListener = null;
        }
    }

    private void attackOnlineOfflineReadListener() {
        if (mChildOnlineOfflineEventListener == null) {
            mChildOnlineOfflineEventListener = new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    try {
                        isOnline = snapshot.getValue(Boolean.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (isOnline) {
                        Log.d(TAG, "Online");
                    } else {
                        Log.d(TAG, "Offline");
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e(TAG,"Listener was cancelled");
                }
            };
        }
        connectedRef.addValueEventListener(mChildOnlineOfflineEventListener);
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

                if (!promises.get(i).isSuccessful()) {
                    alluploaded = false;
                    Log.d(TAG, "Image Upload Unsuccessful for task " + i);
                }
            }
            for(int i=0;i<event.getPhotoID().getPosters().size();i++)
                while(event.getPhotoID().getPosters().get(i)==null);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pd.hide();
            if (alluploaded) {
                Toast.makeText(EditEventActivity.this, "Image Upload Complete!", Toast.LENGTH_SHORT).show();
                uploadEventData();
            }
        }

        @Override
        protected void onPreExecute() {
            alluploaded = true;
        }
    }
}
