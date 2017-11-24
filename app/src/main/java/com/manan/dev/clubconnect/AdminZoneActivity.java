package com.manan.dev.clubconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AdminZoneActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    Button signUpAsAdminBtn, loginAsAdminBtn;
    EditText usernameAdminEditText;
    EditText passwordAdminEditText;
    TextView backToLoginScreen;
    private ImageView ivShowPassword;
    private ProgressDialog pd;
    RelativeLayout rlAdminzone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_zone);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.rgb(126, 14, 14));
//        }

        rlAdminzone = (RelativeLayout) findViewById(R.id.admin_container);
        TransitionDrawable trans = (TransitionDrawable) rlAdminzone.getBackground();
        trans.startTransition(6000);

        //  Window window = activity.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        //  window.setStatusBarColor(ContextCompat.getColor(activity,R.color.my_statusbar_color))
        //setTitle("Admin Zone");

        mAuth = FirebaseAuth.getInstance();
        usernameAdminEditText = (EditText) findViewById(R.id.usernameAdmin);
        passwordAdminEditText = (EditText) findViewById(R.id.passwordAdmin);

        loginAsAdminBtn = (Button) findViewById(R.id.loginAsAdminbtn);
        // signUpAsAdminBtn = (Button) findViewById(R.id.signUpAsAdminbtn);
        backToLoginScreen = (TextView) findViewById(R.id.back_to_login);

        usernameAdminEditText.setText("manantechnosurge@gmail.com");
        passwordAdminEditText.setText("12345678");

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);

//        pd.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                pd.dismiss();
//            }
//        });

        backToLoginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loginAsAdminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAdminSignIn();
            }
        });

        //        signUpAsAdminBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                attemptAdminSignUp();
//            }
//        });
    }

    private void attemptAdminSignUp() {
        String username = usernameAdminEditText.getText().toString();
        String password = passwordAdminEditText.getText().toString();
        if (username.isEmpty()) {
            Toast.makeText(this, "Enter Username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            mAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminZoneActivity.this, "Admin Created", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminZoneActivity.this, AdminDashboardActivity.class));
                        finish();
                    } else {
                        Toast.makeText(AdminZoneActivity.this, "Error making a new admin.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void attemptAdminSignIn() {
        String username = usernameAdminEditText.getText().toString();
        String password = passwordAdminEditText.getText().toString();
        if (username.isEmpty()) {
            Toast.makeText(this, "Enter Username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            pd.show();
            mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //startActivity(new Intent(AdminZoneActivity.this, MainActivity.class));
                        pd.dismiss();
                        finish();
                    } else {
                        Toast.makeText(AdminZoneActivity.this, "Error signing you in.", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            });

        } catch (Exception e) {
            pd.hide();
            e.printStackTrace();
        }
    }
}
