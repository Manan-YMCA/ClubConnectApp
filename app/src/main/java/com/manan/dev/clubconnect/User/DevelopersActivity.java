package com.manan.dev.clubconnect.User;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.manan.dev.clubconnect.CircleTransform;
import com.manan.dev.clubconnect.DashboardUserActivity;
import com.manan.dev.clubconnect.R;
import com.squareup.picasso.Picasso;

public class DevelopersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developers);
       ImageView kushank = (ImageView) findViewById(R.id.img_kushank);
        ImageView yatin = (ImageView) findViewById(R.id.yatin);
        ImageView shubham = (ImageView) findViewById(R.id.sharma);
        ImageView kachroo = (ImageView) findViewById(R.id.kachroo);
        ImageView naman = (ImageView) findViewById(R.id.naman);

        Picasso.with(DevelopersActivity.this).load("https://media.licdn.com/mpr/mpr/shrinknp_400_400/AAEAAQAAAAAAAAwUAAAAJDIzY2I4Nzk2LWQyZjUtNDI2MC05NjlhLWMyMmNlMGYwZTg2Nw.jpg").transform(new CircleTransform()).into(kushank);
        Picasso.with(DevelopersActivity.this).load("https://media.licdn.com/mpr/mpr/shrinknp_400_400/AAEAAQAAAAAAAAl1AAAAJDMyMGEyNDU3LTVkZjYtNDVlZi1hOWFhLWQ2MjliNTlmNDk0Ng.jpg").transform(new CircleTransform()).into(yatin);
        Picasso.with(DevelopersActivity.this).load("https://media.licdn.com/mpr/mpr/shrinknp_400_400/AAIA_wDGAAAAAQAAAAAAAAuBAAAAJGFlOTMyMTVjLTBlZTQtNGNlZi05YjE1LTdlYzc1NTZkNzgzYw.jpg").transform(new CircleTransform()).into(shubham);
        Picasso.with(DevelopersActivity.this).load("https://media.licdn.com/mpr/mpr/shrinknp_400_400/AAIA_wDGAAAAAQAAAAAAAArdAAAAJGYxNjJmNjlkLWEwYTAtNDI5Zi1iYzE4LWMyNjQyYWVhM2ZjOQ.jpg").transform(new CircleTransform()).into(kachroo);
        Picasso.with(DevelopersActivity.this).load("https://media.licdn.com/mpr/mpr/shrinknp_400_400/AAEAAQAAAAAAAAsZAAAAJDczNWQ0Zjc1LTRkZDMtNDQxNi1hNGVkLWI1Y2ZlYzRhOGNhZA.jpg").transform(new CircleTransform()).into(naman);



    }
}
