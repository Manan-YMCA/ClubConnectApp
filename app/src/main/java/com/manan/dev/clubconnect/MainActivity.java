package com.manan.dev.clubconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    LoginButton loginButton;
    CallbackManager callbackManager;
    FirebaseAuth mAuth;
    TextView toAdminZone;
    RelativeLayout containeer;
    AnimationDrawable anim;
    private ProgressDialog pd;
    private FirebaseAuth.AuthStateListener mAuthListener;
    static boolean offline = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (offline) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            offline = false;
        }

        setContentView(R.layout.activity_main);
        containeer = (RelativeLayout) findViewById(R.id.container);
        TransitionDrawable trans = (TransitionDrawable) containeer.getBackground();
        trans.startTransition(8000);

        //setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        mAuth = FirebaseAuth.getInstance();
        //LogInStub("de");
        loginButton = (LoginButton) findViewById(R.id.login_button);
        toAdminZone = (TextView) findViewById(R.id.to_admin_zone);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                LogInStub("AuthListener");
            }
        };

        toAdminZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AdminZoneActivity.class));
            }
        });
        loginButton.setReadPermissions("email", "public_profile");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);

                //String accessToken = loginResult.getAccessToken().getToken();
                // save accessToken to SharedPreference
                //saveAccessToken(accessToken);
                handleFacebookAccessToken(loginResult.getAccessToken());

                Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                Toast.makeText(MainActivity.this, "Login Cancelled!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(MainActivity.this, "Login Error! " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        pd.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Login UnSuccessful!", Toast.LENGTH_SHORT).show();
                        }
                        pd.hide();
                    }
                });
    }

    void LogInStub(String s) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getProviders().get(0).equals("facebook.com")) {
            startActivity(new Intent(MainActivity.this, DashboardUserActivity.class));
            //finish();
        } else if (currentUser != null) {
            Toast.makeText(MainActivity.this, "Switching to Dashboard!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, AdminDashboardActivity.class));
            //finish();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check if user is signed in
        mAuth.addAuthStateListener(mAuthListener);
    }
}
