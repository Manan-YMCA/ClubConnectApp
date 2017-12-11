package com.manan.dev.clubconnect.editEvent;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.manan.dev.clubconnect.EditEventActivity;
import com.manan.dev.clubconnect.R;

public class EventDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        String details = getIntent().getStringExtra(EditEventActivity.REQ_PARA_EVENT_DETAILS);

        final EditText eventDetails = findViewById(R.id.etEventDetails);
        Button bOK = findViewById(R.id.bOK);
        Button bCancel = findViewById(R.id.bCancel);

        if(details!=null)
        {
            eventDetails.setText(details);
            eventDetails.setSelection(0, details.length());
        }

        if (bOK != null)
            bOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (eventDetails.getText().toString().trim().equals("")) {
                        Toast.makeText(EventDetails.this, "Event details cannot be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(EditEventActivity.REQ_PARA_EVENT_DETAILS, eventDetails.getText().toString().trim());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            });

        if (bCancel != null)
            bCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
            });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        super.onBackPressed();
    }
}
