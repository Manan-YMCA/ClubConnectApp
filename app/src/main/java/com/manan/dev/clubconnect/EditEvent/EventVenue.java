package com.manan.dev.clubconnect.EditEvent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.manan.dev.clubconnect.EditEventActivity;
import com.manan.dev.clubconnect.R;

public class EventVenue extends AppCompatActivity {

    private static final String[] EVENT_VENUE = new String[] {
            "IT-01", "IT-02", "IT-03", "IT-04", "IT-05", "IT-06", "IT-07", "IT-08", "LTs", "LT-01", "LT-02", "LT-03", "LT-04", "LC", "Shakuntalam", "Auditorium", "Main Stage", "MMC", "Yet to be decided"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_venue);
        String venue = getIntent().getStringExtra(EditEventActivity.REQ_PARA_EVENT_VENUE);

        final AutoCompleteTextView eventVenue = findViewById(R.id.etEventVenue);
        Button bOK = findViewById(R.id.bOK);
        Button bCancel = findViewById(R.id.bCancel);

        if(venue!=null){
            eventVenue.setText(venue, false);
            eventVenue.setSelection(0, venue.length());
        }

        if (bOK != null)
            bOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (eventVenue.getText().toString().trim().equals("")) {
                        Toast.makeText(EventVenue.this, "Event venue cannot be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        String venue = eventVenue.getText().toString().trim();
                        boolean found =false;
                        for(String each: EVENT_VENUE){
                            if(each.equals(venue)){
                                found=true;
                                break;
                            }
                        }
                        if(!found){
                            Toast.makeText(EventVenue.this, "Event venue should be from the list provided", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent();
                        intent.putExtra(EditEventActivity.REQ_PARA_EVENT_VENUE, venue);
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

        eventVenue.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, EVENT_VENUE));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        super.onBackPressed();
    }
}
