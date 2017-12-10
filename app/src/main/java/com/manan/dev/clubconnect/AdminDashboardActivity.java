package com.manan.dev.clubconnect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminDashboardActivity extends AppCompatActivity {
    private FloatingActionButton addNewEventFab;
    private FirebaseAuth mAuth;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        mAuth = FirebaseAuth.getInstance();
        addNewEventFab = (FloatingActionButton) findViewById(R.id.add_new_event_fab);
        Object ffffff;
        //addNewEventFab.setBackgroundTintList(ColorStateList.valueOf(R.color.darkBlack));
        //.withAlpha(R.color.cardview_shadow_start_color));
        addNewEventFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, EditEventActivity.class));

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.action_request_notfiy:
                FirebaseUser usser = mAuth.getCurrentUser();
                String cllubName = usser.getDisplayName();

                if(usser==null)
                    finish();
                startActivity(new Intent(AdminDashboardActivity.this, RequestUserActivity.class).putExtra("name",cllubName));
                return true;

            case R.id.action_add_coord:
                FirebaseUser user = mAuth.getCurrentUser();
                String clubName = user.getDisplayName();
                if(user==null)
                    finish();
                startActivity(new Intent(AdminDashboardActivity.this, AddNewCoordinatorActivity.class).putExtra("clubName", clubName));
                finish();


            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
