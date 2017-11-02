package com.manan.dev.clubconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AdminLoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    Button userModeBtn, loginAsAdminBtn;
    EditText usernameAdminEditText, passwordAdminEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        setTitle("Admin Login");
        mAuth = FirebaseAuth.getInstance();

        userModeBtn = (Button) findViewById(R.id.usermodebtn);
        usernameAdminEditText = (EditText) findViewById(R.id.usernameAdmin);
        passwordAdminEditText = (EditText) findViewById(R.id.passwordAdmin);
        loginAsAdminBtn = (Button) findViewById(R.id.loginAsAdminbtn);

        loginAsAdminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAdminSignIn();
            }
        });

    }

    private void attemptAdminSignIn() {
        String username = usernameAdminEditText.getText().toString();
        String password = passwordAdminEditText.getText().toString();
        mAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(AdminLoginActivity.this, AdminDashboardActivity.class));
                    finish();
                } else{
                    Toast.makeText(AdminLoginActivity.this, "Error logging you in.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
