package com.manan.dev.clubconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.manan.dev.clubconnect.Adapters.RecyclerViewDataAdapter;
import com.manan.dev.clubconnect.Models.Event;
import com.manan.dev.clubconnect.Models.Photos;
import com.manan.dev.clubconnect.Models.SectionDataModel;
import com.manan.dev.clubconnect.Models.TimeInterval;
import com.manan.dev.clubconnect.User.DevelopersActivity;
import com.manan.dev.clubconnect.User.UserClubEventListActivity;
import com.manan.dev.clubconnect.User.UserProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.manan.dev.clubconnect.Adapters.UserSingleEventListAdapter.CLUB_NAME;
import static java.util.Collections.sort;

public class DashboardUserActivity extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private FirebaseAuth mAuth;
    private ImageView fbImageView;
    private TextView tvfbName;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private Map<String, ArrayList<Pair<String, Event>>> eventsMap;
    private String clubName;
    private Event event;
    //private SectionDataModel allEvents;
    //private ArrayList<Event> allEventsItem;
    private SectionDataModel preEvents;
    private ArrayList<Event> preEventsItem;
    private SectionDataModel curEvents;
    private ArrayList<Event> curEventsItem;
    private RecyclerView my_recycler_view;

    ArrayList<SectionDataModel> eventListForRecyclerView;
    private RecyclerViewDataAdapter adapter;
    private ProgressBar pb;
    private NavigationView nav_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_user);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        eventListForRecyclerView = new ArrayList<SectionDataModel>();
        mAuth = FirebaseAuth.getInstance();
        //createDummyData();
        event = new Event();
        eventsMap = new HashMap<>();
        preEvents = new SectionDataModel();
        preEvents.setHeaderTitle("Past Events");
        preEventsItem = new ArrayList<>();

        curEvents = new SectionDataModel();
        curEvents.setHeaderTitle("Future Events");
        curEventsItem = new ArrayList<>();

        my_recycler_view = (RecyclerView) findViewById(R.id.my_recycler_view);

        my_recycler_view.setHasFixedSize(true);

        //RecyclerViewDataAdapter adapter = new RecyclerViewDataAdapter(this, eventListForRecyclerView);

        my_recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //my_recycler_view.setAdapter(adapter);
        nav_view = (NavigationView) findViewById(R.id.nav_view);

        //Fb Image  and  name fetching Code
        fbImageView = (ImageView) nav_view.getHeaderView(0).findViewById(R.id.fbImageView);
        tvfbName = (TextView) nav_view.getHeaderView(0).findViewById(R.id.tvfbName);
        pb = (ProgressBar) findViewById(R.id.pb);

        setClickListeners();

        try {
            Picasso.with(DashboardUserActivity.this)
                    .load(mAuth.getCurrentUser()
                            .getPhotoUrl()).resize((int) getResources()
                    .getDimension(R.dimen.t50), (int) getResources().getDimension(R.dimen.t50))
                    .centerCrop()
                    .transform(new CircleTransform())
                    .into(fbImageView);
            tvfbName.setText(mAuth.getCurrentUser().getDisplayName());
            //String urll=mAuth.getCurrentUser().getPhotoUrl().toString();
            //Toast.makeText(DashboardUserActivity.this,urll,Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.d("imageurl", e.toString());
            Toast.makeText(DashboardUserActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }


        //drawer Layout Code

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                DashboardUserActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        nav_view.setNavigationItemSelectedListener(this);
        nav_view.setCheckedItem(R.id.nav_camera);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("events");

        adapter = new RecyclerViewDataAdapter(this, eventListForRecyclerView);
        my_recycler_view.setAdapter(adapter);
    }

    private void setClickListeners() {
        findViewById(R.id.manan).setOnClickListener(this);
        findViewById(R.id.srijan).setOnClickListener(this);
        findViewById(R.id.samarpan).setOnClickListener(this);
        findViewById(R.id.vividha).setOnClickListener(this);
        findViewById(R.id.ananya).setOnClickListener(this);
        findViewById(R.id.nataraja).setOnClickListener(this);
        findViewById(R.id.jhalak).setOnClickListener(this);
        findViewById(R.id.microbird).setOnClickListener(this);
        findViewById(R.id.mechnext).setOnClickListener(this);
        findViewById(R.id.taranum).setOnClickListener(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseListener();
        if(nav_view!=null)
            nav_view.setCheckedItem(R.id.nav_camera);
    }

    @Override
    protected void onPause() {
        super.onPause();
        detatchDatabaseListener();
        eventsMap.clear();
        preEventsItem.clear();
        curEventsItem.clear();

        eventListForRecyclerView.clear();

        if(nav_view!=null)
            nav_view.setCheckedItem(R.id.nav_camera);
    }

    private void detatchDatabaseListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    private void attachDatabaseListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {
                        Log.d("onAdded", dataSnapshot.toString());
                        clubName = dataSnapshot.getKey();
                        if (!eventsMap.containsKey(clubName)) {
                            eventsMap.put(clubName, new ArrayList<Pair<String, Event>>());
                        }
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            eventsMap.get(clubName).add(new Pair<String, Event>(data.getKey(), data.getValue(Event.class)));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    updateList();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    try {
                        Log.d("onAdded", dataSnapshot.toString());
                        clubName = dataSnapshot.getKey();
                        eventsMap.put(clubName, new ArrayList<Pair<String, Event>>());
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            eventsMap.get(clubName).add(new Pair<String, Event>(data.getKey(), data.getValue(Event.class)));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    updateList();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    clubName = dataSnapshot.getKey();
                    eventsMap.remove(clubName);
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
        try {
            preEventsItem.clear();
            curEventsItem.clear();
            eventListForRecyclerView.clear();

            for (Map.Entry<String, ArrayList<Pair<String, Event>>> entry : eventsMap.entrySet()) {
                pb.setVisibility(View.GONE);
                ArrayList<Pair<String, Event>> clublist = eventsMap.get(entry.getKey());
                for (int i = 0; i < clublist.size(); i++) {
                    Event model = new Event();
                    Event eventItem = clublist.get(i).second;
                    model.setClubName(entry.getKey());
                    model.setEventId(clublist.get(i).first);
                    model.setEventName(eventItem.getEventName());
                    ArrayList<TimeInterval> dayTime = new ArrayList<>();
                    TimeInterval tInterval = new TimeInterval();
                    tInterval.setDate(eventItem.getDays().get(0).getDate());
                    tInterval.setEndTime(eventItem.getDays().get(0).getEndTime());
                    tInterval.setStartTime(eventItem.getDays().get(0).getStartTime());
                    dayTime.add(tInterval);
                    model.setDays(dayTime);

                    if (eventItem.getPhotoID() != null &&
                            eventItem.getPhotoID().getPosters() != null &&
                            eventItem.getPhotoID().getPosters().size() > 0) {
                        ArrayList<String> poster = new ArrayList<>();
                        poster.add(eventItem.getPhotoID().getPosters().get(0));
                        Photos ph = new Photos();
                        ph.setPosters(poster);
                        model.setPhotoID(ph);

                    } else
                        model.setPhotoID(null);
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    long curDate = cal.getTimeInMillis();
                    if (model.getDays().get(0).getDate() < curDate)
                        preEventsItem.add(model);
                    else
                        curEventsItem.add(model);
                }
            }
            sort(preEventsItem);
            sort(curEventsItem);
            //Toast.makeText(DashboardUserActivity.this, Integer.toString(allEventsItem.size()), Toast.LENGTH_SHORT).show();
            curEvents.setAllItemsInSection(curEventsItem);
            preEvents.setAllItemsInSection(preEventsItem);

            eventListForRecyclerView.add(curEvents);
            eventListForRecyclerView.add(preEvents);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.d("updateListEx", e.getMessage());
            Toast.makeText(DashboardUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(DashboardUserActivity.this, Integer.toString(allEventsItem.size()), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START, true);
        }
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        item.setChecked(false);

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(DashboardUserActivity.this, UserProfileActivity.class));
        }
        else if (id == R.id.nav_about) {
            String msg = "It is an integrated platform providing people with all the the necessary and most up to date features for Event management. \n" +
                    "The user panel would be giving all the informations about event in the form of cards, mentioning the gyst of event and the details like venue, day,timings and more. ";
            createDialogBox(msg);
            return true;
        } else if (id == R.id.nav_share) {

            String msg = "Try this awesome app which will make you aware of all the events in campus!";
            shareTextMessage(msg);
            return true;

        } else if (id == R.id.nav_send) {
            String to = "manantechnosurge@gmail.com";
            String subject = "Bug Reported - Club Connect ";
            String msg = "I found a bug!\n";

            sendEmailBug(to, subject, msg);
            return true;

        } else if (id == R.id.nav_logout) {
            try {
                mAuth.signOut();
                LoginManager.getInstance().logOut();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();
            return true;


        }
        else if (id == R.id.nav_dev)
        {
            startActivity(new Intent(DashboardUserActivity.this, DevelopersActivity.class));
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createDialogBox(String msg) {
        new MaterialStyledDialog.Builder(this)
                .setTitle("About Us")
                .setDescription(msg + "\n\n")
                .withDialogAnimation(true)
                .setStyle(Style.HEADER_WITH_TITLE)
                .setTitle("About US")
                .setScrollable(true, 20)
                .setCancelable(true)
                .show();

    }

    private void sendEmailBug(String to, String subject, String msg) {

        Uri uri = Uri.parse("mailto:")
                .buildUpon()
                .appendQueryParameter("subject", subject)
                .appendQueryParameter("body", msg)
                .build();

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"));

        /*Intent email = new Intent(Intent.ACTION_SEND);

        //   email.setData(Uri.parse("mailto:"));

        email.setType("text/plain");

        email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, msg);

        //need this to prompt email client only
        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, "Choose an Email client :"));
        */
    }

    private void shareTextMessage(String msg) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, msg);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        Bundle b = new Bundle();
        Intent i = new Intent(DashboardUserActivity.this, UserClubEventListActivity.class);
        switch (v.getId()) {
            case R.id.manan:
                b.putString(CLUB_NAME, "Manan");
                i.putExtras(b);
                startActivity(i);
                break;
            case R.id.vividha:
                b.putString(CLUB_NAME, "Vividha");
                i.putExtras(b);
                startActivity(i);
                break;
            case R.id.srijan:
                b.putString(CLUB_NAME, "Srijan");
                i.putExtras(b);
                startActivity(i);
                break;
            case R.id.samarpan:
                b.putString(CLUB_NAME, "Samarpan");
                i.putExtras(b);
                startActivity(i);
                break;
            case R.id.ananya:
                b.putString(CLUB_NAME, "Ananya");
                i.putExtras(b);
                startActivity(i);
                break;

            case R.id.nataraja:
                b.putString(CLUB_NAME, "Nataraja");
                i.putExtras(b);
                startActivity(i);
                break;

            case R.id.jhalak:
                b.putString(CLUB_NAME, "Jhalak");
                i.putExtras(b);
                startActivity(i);
                break;

            case R.id.microbird:
                b.putString(CLUB_NAME, "Microbird");
                i.putExtras(b);
                startActivity(i);
                break;

            case R.id.mechnext:
                b.putString(CLUB_NAME, "Mechnext");
                i.putExtras(b);
                startActivity(i);
                break;

            case R.id.taranum:
                b.putString(CLUB_NAME, "Taranum");
                i.putExtras(b);
                startActivity(i);
                break;
        }

    }
}
