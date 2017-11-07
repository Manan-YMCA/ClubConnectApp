package com.manan.dev.clubconnect;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manan.dev.clubconnect.Models.Coordinator;

import java.util.ArrayList;

public class DelActivity extends AppCompatActivity {
    private DatabaseReference database;
    private FirebaseUser mUser;
    private String clubName;
    private ArrayList<Coordinator> coordinatorsList;
    private String nameList[];
    private AutoCompleteTextView coordinatorAutoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_del);
        coordinatorsList = new ArrayList<>();
        coordinatorAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.coordinator_text_view);
        new FetchDBDetails().execute("");


    }

    private class FetchDBDetails extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            try {
                clubName = mUser.getEmail().split("@")[0];
                database = FirebaseDatabase.getInstance().getReference("coordinators").child(clubName);
                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Coordinator coordinator = snapshot.getValue(Coordinator.class);
                            Toast.makeText(DelActivity.this, coordinator.getName(), Toast.LENGTH_SHORT).show();
                            try {
                                coordinatorsList.add(coordinator); //TODO error here else everything is working
                                Toast.makeText(DelActivity.this, coordinatorsList.size(), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(DelActivity.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "executed";
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("executed")) {

                int si = coordinatorsList.size();
                Log.d("bhasad", si + "");
                try {
                    nameList = new String[si];
                    for (int i = 0; i < si; i++) {
                        nameList[i] = coordinatorsList.get(i).getName();
                        Log.d("bhasad", nameList[i]);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(DelActivity.this, android.R.layout.select_dialog_item, nameList);
                    coordinatorAutoCompleteTextView.setThreshold(2);
                    coordinatorAutoCompleteTextView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
