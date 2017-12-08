package com.manan.dev.clubconnect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.manan.dev.clubconnect.EditEvent.EventDetails;
import com.manan.dev.clubconnect.EditEvent.EventName;
import com.manan.dev.clubconnect.EditEvent.EventTimings;
import com.manan.dev.clubconnect.EditEvent.EventVenue;
import com.manan.dev.clubconnect.EditEvent.ModifyCoordinators;
import com.manan.dev.clubconnect.Models.Coordinator;
import com.manan.dev.clubconnect.Models.Event;
import com.manan.dev.clubconnect.Models.TimeInterval;
import com.manan.dev.clubconnect.User.EventsDetailsActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.util.Collections.sort;

public class EditEventActivity extends AppCompatActivity {

    private static final int REQ_ID_NAME = 1;
    private static final int REQ_ID_POSTER = 2;
    private static final int REQ_ID_VENUE = 3;
    public static final String REQ_PARA_EVENT_NAME = "event_name";
    public static final String REQ_PARA_EVENT_VENUE = "event_venue";
    private static final int REQ_ID_DETAILS = 4;
    public static final String REQ_PARA_EVENT_DETAILS = "event_details";
    public static final String REQ_PARA_EVENT_COORD_NAME = "coord_name";
    public static final String REQ_PARA_EVENT_COORD_PHONE = "coord_phone";
    public static final String REQ_PARA_EVENT_COORD_PHOTO = "coord_photo";
    public static final String REQ_PARA_EVENT_COORD_EMAIL = "coord_email";
    private static final int REQ_ID_COORDS = 5;
    public static final String REQ_PARA_EVENT_DATE = "event_date";
    public static final String REQ_PARA_EVENT_STIME = "event_stime";
    public static final String REQ_PARA_EVENT_ETIME = "event_etime";
    private static final int REQ_ID_TIMINGS = 6;

    Event event;

    private Map<String,Coordinator> coordinatorsAll;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBar;
    private TextView tvVenue;
    private TextView tvDetails;
    private LinearLayout llCoordinators;
    private LinearLayout llTimings;

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

        event = new Event();
        event.coordinatorID = new ArrayList<>();
        event.days = new ArrayList<>();
        event.photoID.posters = new ArrayList<>();
        event.photoID.afterEvent = new ArrayList<>();

        coordinatorsAll = new HashMap<>();

        tvVenue = findViewById(R.id.tv_venue);
        tvDetails = findViewById(R.id.tv_details);
        llCoordinators = findViewById(R.id.ll_coordinators);
        llTimings = findViewById(R.id.ll_timmings_all);

        appBar = findViewById(R.id.app_bar);
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        View selectPoster = collapsingToolbarLayout.findViewById(R.id.ll_select_poster);
        if(selectPoster!=null)
        {
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
        if(editName!=null) {
            editName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String eventName = event.getEventName();
                    Intent intent = new Intent(EditEventActivity.this, EventName.class);
                    if(eventName!=null)
                        intent.putExtra(REQ_PARA_EVENT_NAME, eventName);
                    startActivityForResult(intent, REQ_ID_NAME);
                }
            });
        }

        View editVenue = findViewById(R.id.iv_edit_venue);
        if(editVenue!=null){
            editVenue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String eventVenue = event.getEventVenue();
                    Intent intent = new Intent(EditEventActivity.this, EventVenue.class);
                    if(eventVenue!=null)
                        intent.putExtra(REQ_PARA_EVENT_VENUE, eventVenue);
                    startActivityForResult(intent, REQ_ID_VENUE);
                }
            });
        }

        View editDetails = findViewById(R.id.tv_edit_details);
        if(editDetails!=null)
        {
            editDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String eventDetails = event.getEventDesc();
                    Intent intent = new Intent(EditEventActivity.this, EventDetails.class);
                    if(eventDetails!=null)
                        intent.putExtra(REQ_PARA_EVENT_DETAILS, eventDetails);
                    startActivityForResult(intent, REQ_ID_DETAILS);
                }
            });
        }

        View modifyCoordinators = findViewById(R.id.tvAddCoords);
        if(modifyCoordinators!=null)
        {
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
                    if(name.size()>0) {
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
        if(addTimings!=null)
        {
            addTimings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(EditEventActivity.this, EventTimings.class);
                    startActivityForResult(intent, REQ_ID_TIMINGS);
                }
            });
        }
    }
    void fromCoordinatorsToArrayList(ArrayList<String> name, ArrayList<String> phone, ArrayList<String> email, ArrayList<String> photo)
    {
        name.clear();
        phone.clear();
        email.clear();
        photo.clear();
        for(String coordId : event.getCoordinatorID()){
            Coordinator coord = coordinatorsAll.get(coordId);
            if(coord==null)
                continue;
            name.add(coord.getName());
            phone.add(coord.getPhone());
            email.add(coord.getEmail());
            photo.add(coord.getPhoto());
        }
    }

    void upDateCoordinators(ArrayList<String> name, ArrayList<String> phone, ArrayList<String> email, ArrayList<String> photo)
    {
        llCoordinators.removeAllViews();

        if(name.size() == 0)
        {
            LinearLayout ll = (LinearLayout) LayoutInflater.from(EditEventActivity.this).inflate(R.layout.coordinators_event_details_display, null, false);
            llCoordinators.addView(ll);
        }

        for(int i=0; i<name.size(); i++)
        {
            LinearLayout ll = (LinearLayout) LayoutInflater.from(EditEventActivity.this).inflate(R.layout.coordinators_event_details_display, null, false);
            TextView tvName = ll.findViewById(R.id.tv_org_name);
            TextView tvPhone = ll.findViewById(R.id.tv_phone_no);

            tvName.setText(name.get(i));
            tvPhone.setText(phone.get(i));

            Picasso.with(EditEventActivity.this).load(photo.get(i)).resize(200, 200).centerCrop().transform(new CircleTransform()).into((ImageView) ll.findViewById(R.id.iv_photo));

            llCoordinators.addView(ll);

            coordinatorsAll.put(email.get(i),new Coordinator(name.get(i), email.get(i), phone.get(i), photo.get(i)));

        }
    }

    void updatePoster(){
        if(event.getPhotoID().getPosters().size()>0){}
    }
    void updateUIText(){
        if(event.getEventName()!=null)
            collapsingToolbarLayout.setTitle(event.getEventName());
        if(event.getEventVenue()!=null)
            tvVenue.setText(event.getEventVenue());
        if(event.getEventDesc()!=null)
            tvDetails.setText(event.getEventDesc());

        if(event.getDays().size()!=0)
        {
            llTimings.removeAllViews();
        }
        for(TimeInterval ti : event.getDays()){
            LinearLayout ll = (LinearLayout) LayoutInflater.from(EditEventActivity.this).inflate(R.layout.user_single_event_item_timmings, null, false);
            ll.setClickable(true);
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

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

            llTimings.addView(ll);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode)
        {
            case REQ_ID_NAME:
                if(data!=null) {
                    event.setEventName(data.getStringExtra(REQ_PARA_EVENT_NAME));
                    updateUIText();
                }
                break;
            case REQ_ID_VENUE:
                if(data!=null) {
                    event.setEventVenue(data.getStringExtra(REQ_PARA_EVENT_VENUE));
                    updateUIText();
                }
                break;
            case REQ_ID_DETAILS:
                if(data!=null) {
                    event.setEventDesc(data.getStringExtra(REQ_PARA_EVENT_DETAILS));
                    updateUIText();
                }
                break;
            case REQ_ID_COORDS:
                if(data!=null){
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
                if(data!=null)
                {
                    long date = data.getLongExtra(REQ_PARA_EVENT_DATE,0);
                    long stime = data.getLongExtra(REQ_PARA_EVENT_STIME,0);
                    long etime = data.getLongExtra(REQ_PARA_EVENT_ETIME, 0);

                    event.getDays().add(new TimeInterval(date,stime, etime));
                    sort(event.getDays());
                    updateUIText();
                }
                break;
            case REQ_ID_POSTER:
                if (data != null && data.getData() != null) {
                    Uri localData = data.getData();
                    try {
                        Bitmap bitmap;
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), localData);
                        bitmap = getAppBarFriendlyBitmap(bitmap);
                        appBar.setBackground(new BitmapDrawable(EditEventActivity.this.getResources(), bitmap));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

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
}
