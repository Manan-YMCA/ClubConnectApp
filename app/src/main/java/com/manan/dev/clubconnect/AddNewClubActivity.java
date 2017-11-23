package com.manan.dev.clubconnect;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AddNewClubActivity extends AppCompatActivity {

    EditText etclubName, etEmail, etPassword;
    Button btnAddClub;
    private FirebaseAuth mAuth;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_club);

        mAuth = FirebaseAuth.getInstance();

        etclubName = (EditText) findViewById(R.id.et_club_name);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnAddClub = (Button) findViewById(R.id.btn_add_club);


        etPassword.setText("12345678");


        pd = new ProgressDialog(AddNewClubActivity.this);
        pd.setMessage("Creating Account...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);

        btnAddClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                mAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                        .addOnCompleteListener(AddNewClubActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("TAG", "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(etclubName.getText().toString())
                                            .build();

                                    assert user != null;
                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("TAG", "User profile updated.");
                                                        pd.dismiss();
                                                        etclubName.setText("");
                                                        etEmail.setText("");
                                                        etPassword.setText("12345678");
                                                        Toast.makeText(AddNewClubActivity.this, "Ho gaya!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(AddNewClubActivity.this, "Club is not added", Toast.LENGTH_SHORT).show();
                                                        pd.dismiss();
                                                    }
                                                }
                                            });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(AddNewClubActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }

                            }
                        });


            }
        });


    }
}
