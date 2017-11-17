package com.manan.dev.clubconnect;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
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
import com.manan.dev.clubconnect.R;
import com.squareup.picasso.Picasso;

public class DashboardUserActivity extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private ImageView fbImageView;
    private TextView tvfbName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fbImageView = (ImageView) ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.fbImageView);
        tvfbName = (TextView) ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.tvfbName);
        try {
            Picasso.with(DashboardUserActivity.this)
                    .load(mAuth.getCurrentUser()
                            .getPhotoUrl()).resize((int)getResources()
                    .getDimension(R.dimen.t50),(int)getResources().getDimension(R.dimen.t50))
                    .centerCrop()
                    .transform(new CircleTransform())
                    .into(fbImageView);
            tvfbName.setText(mAuth.getCurrentUser().getDisplayName());
        }
        catch (Exception e){
            Log.d("imageurl", e.toString());
            Toast.makeText(DashboardUserActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                                finish();
                                return true;


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
