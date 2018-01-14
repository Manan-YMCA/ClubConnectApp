package com.manan.dev.clubconnect;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.widget.Toast;

import com.manan.dev.clubconnect.Adapters.AdminClubFragmentAdapter;

public class ClubMembersRequest extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AdminClubFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_members_request);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout_admin);
        //TabLayout.Tab tab1 = tabLayout.getTabAt(0);
        viewPager = (ViewPager) findViewById(R.id.view_pager_admin);
        viewPager.setOffscreenPageLimit(3);
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setUpViewPager(ViewPager viewPager) {

        adapter = new AdminClubFragmentAdapter(getSupportFragmentManager());
        RequestUserActivity requestFragment = new RequestUserActivity();
        FragmentMemberClub memberClub = new FragmentMemberClub();
        FragmentCoordinatorClub coordinatorClub = new FragmentCoordinatorClub();
        adapter.addFragment(memberClub, "MEMBERS");
        adapter.addFragment(coordinatorClub, "COORDINATORS");
        adapter.addFragment(requestFragment, "NEW REQUEST");
        viewPager.setAdapter(adapter);
    }
}
