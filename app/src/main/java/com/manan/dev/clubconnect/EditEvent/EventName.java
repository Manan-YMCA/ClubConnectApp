package com.manan.dev.clubconnect.EditEvent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.manan.dev.clubconnect.EditEventActivity;
import com.manan.dev.clubconnect.R;

public class EventName extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_name);

        String event_name = getIntent().getStringExtra(EditEventActivity.REQ_PARA_EVENT_NAME);

        final EditText eventName = findViewById(R.id.etEventName);
        Button bOK = findViewById(R.id.bOK);
        Button bCancel = findViewById(R.id.bCancel);

        if(event_name!=null) {
            eventName.setText(event_name);
            eventName.setSelection(0, event_name.length());
        }

        if (bOK != null)
            bOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (eventName.getText().toString().trim().equals("")) {
                        Toast.makeText(EventName.this, "Event name cannot be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(EditEventActivity.REQ_PARA_EVENT_NAME, eventName.getText().toString().trim());
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
