package com.manan.dev.clubconnect;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.manan.dev.clubconnect.Adapters.RecyclerViewDataAdapter;
import com.manan.dev.clubconnect.Models.Event;
import com.manan.dev.clubconnect.Models.SectionDataModel;
import com.manan.dev.clubconnect.Models.SingleItemModel;
import com.manan.dev.clubconnect.R;
import com.squareup.picasso.Picasso;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DashboardUserActivity extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener {
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
    private SectionDataModel allEvents;
    private ArrayList<SingleItemModel> allEventsItem;
    private RecyclerView my_recycler_view;

    ArrayList<SectionDataModel> eventListForRecyclerView;


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
        allEvents = new SectionDataModel();
        allEvents.setHeaderTitle("All Events");
        allEventsItem = new ArrayList<>();

        my_recycler_view = (RecyclerView) findViewById(R.id.my_recycler_view);

        my_recycler_view.setHasFixedSize(true);

        //RecyclerViewDataAdapter adapter = new RecyclerViewDataAdapter(this, eventListForRecyclerView);

        my_recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //my_recycler_view.setAdapter(adapter);



        //Fb Image  and  name fetching Code
        fbImageView = (ImageView) ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.fbImageView);
        tvfbName = (TextView) ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.tvfbName);
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("events");
    }


    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseListener();
        try {

            for(Map.Entry<String, ArrayList<Pair<String, Event>>> entry : eventsMap.entrySet()){
                Toast.makeText(DashboardUserActivity.this, entry.getKey(), Toast.LENGTH_SHORT).show();
                Toast.makeText(DashboardUserActivity.this, Integer.toString(entry.getValue().size()), Toast.LENGTH_SHORT).show();
                ArrayList<Pair<String, Event>> clublist = eventsMap.get(entry.getKey());
                for(int i = 0; i < clublist.size(); i++){
                    Toast.makeText(DashboardUserActivity.this, clublist.get(i).first, Toast.LENGTH_SHORT).show();
                    Toast.makeText(DashboardUserActivity.this, clublist.get(i).second.eventName, Toast.LENGTH_LONG).show();
                }
            }
        }
        catch (Exception e){
            Toast.makeText(DashboardUserActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        detatchDatabaseListener();
        eventsMap.clear();
        allEventsItem.clear();
        eventListForRecyclerView.clear();
    }

    private void detatchDatabaseListener() {
        if(mChildEventListener != null){
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    private void attachDatabaseListener() {
        if(mChildEventListener == null){
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try{
                        clubName = dataSnapshot.getKey();
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            if(!eventsMap.containsKey(clubName)){
                                eventsMap.put(clubName, new ArrayList<Pair<String, Event>>());
                            }
                            eventsMap.get(clubName).add(new Pair<String, Event>(data.getKey(), data.getValue(Event.class)));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    updateList();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        try{
            for(Map.Entry<String, ArrayList<Pair<String, Event>>> entry : eventsMap.entrySet()) {
                ArrayList<Pair<String, Event>> clublist = eventsMap.get(entry.getKey());
                for(int i = 0; i < clublist.size(); i++){
                    SingleItemModel model = new SingleItemModel();
                    Event eventItem = clublist.get(i).second;
                    model.setClubName(entry.getKey());
                    model.setEventId(clublist.get(i).first);
                    model.setEventName(eventItem.getEventName());
                    model.setEventDate(eventItem.getDays().get(0).getDate());
                    model.setEventTime(eventItem.getDays().get(0).getStartTime());
                    if(eventItem.getPhotoID().getPosters().size() > 0)
                        model.setImageUrl(eventItem.getPhotoID().getPosters().get(0));
                    else
                        model.setImageUrl(null);
                    allEventsItem.add(model);
                }
            }
            Toast.makeText(DashboardUserActivity.this, Integer.toString(allEventsItem.size()), Toast.LENGTH_SHORT).show();
            allEvents.setAllItemsInSection(allEventsItem);
            eventListForRecyclerView.add(allEvents);
            RecyclerViewDataAdapter adapter = new RecyclerViewDataAdapter(this, eventListForRecyclerView);
            my_recycler_view.setAdapter(adapter);
        }catch (Exception e){
            Toast.makeText(DashboardUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Toast.makeText(DashboardUserActivity.this, Integer.toString(allEventsItem.size()), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START, true);
        }
        if(!drawer.isDrawerOpen(GravityCompat.START)) {
            super.onBackPressed();
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

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


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*public void createDummyData() {


        SectionDataModel dm  = new SectionDataModel();

        dm.setHeaderTitle("Bookmarked Events");

        ArrayList<SingleItemModel> bookmarkedEvents = new ArrayList<SingleItemModel>();
        for (int j = 0; j <= 5; j++) {
            String urll=mAuth.getCurrentUser().getPhotoUrl().toString();
            bookmarkedEvents.add(new SingleItemModel("id"+j, "eventName"+j, urll, "clubname"+j, 123+j, 123+j));
        }

        dm.setAllItemsInSection(bookmarkedEvents);
        eventListForRecyclerView.add(dm);

        ArrayList<SingleItemModel> upcomingEvents = new ArrayList<>();
        SectionDataModel fm  = new SectionDataModel();
        fm.setHeaderTitle("Upcoming Events");
        for (int j = 0; j <= 5; j++) {
            upcomingEvents.add(new SingleItemModel("id"+j, "eventName"+j, "url+'j", "clubname"+j, 123+j, 123+j));
        }

        fm.setAllItemsInSection(upcomingEvents);
        eventListForRecyclerView.add(fm);
    }*/
}
