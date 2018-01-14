package com.manan.dev.clubconnect.user;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.manan.dev.clubconnect.Adapters.AdminClubFragmentAdapter;
import com.manan.dev.clubconnect.R;

public class UserBookmarkActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AdminClubFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bookmark);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout_events);
        viewPager = (ViewPager) findViewById(R.id.view_pager_events);
        viewPager.setOffscreenPageLimit(2);
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setUpViewPager(ViewPager viewPager) {
        adapter = new AdminClubFragmentAdapter(getSupportFragmentManager());
        FragmentBookmarkedEvents interested = new FragmentBookmarkedEvents();
        FragmentGoingEvents going = new FragmentGoingEvents();
        adapter.addFragment(interested, "INTERESTED");
        adapter.addFragment(going, "GOING");
        viewPager.setAdapter(adapter);
    }
}
