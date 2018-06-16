package com.manan.dev.clubconnect.Utilitites;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class ApplicationClass extends Application {

    private static ApplicationClass mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        if (!FirebaseApp.getApps(this).isEmpty())
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public static synchronized ApplicationClass getInstance() {
        return mInstance;
    }
}
