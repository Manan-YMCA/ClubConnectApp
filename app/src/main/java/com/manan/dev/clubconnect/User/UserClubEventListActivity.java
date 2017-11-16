package com.manan.dev.clubconnect.User;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.manan.dev.clubconnect.Adapters.UserSingleEventListAdapter;
import com.manan.dev.clubconnect.Models.UserSingleEventList;
import com.manan.dev.clubconnect.R;

import java.util.ArrayList;

public class UserClubEventListActivity extends AppCompatActivity {

    RecyclerView userSingleEventListRV;
    ArrayList<UserSingleEventList> userSingleEventListArrayList = new ArrayList<>();
    UserSingleEventListAdapter userSingleEventListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_club_event_list);

        userSingleEventListRV = (RecyclerView) findViewById(R.id.rv_user_club_list);



        //Add all the database from Firebase to the ArrayList here!!



        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        userSingleEventListRV.setLayoutManager(layoutManager);

        userSingleEventListAdapter = new UserSingleEventListAdapter(userSingleEventListArrayList,this);
        userSingleEventListRV.setAdapter(userSingleEventListAdapter);

    }
}
