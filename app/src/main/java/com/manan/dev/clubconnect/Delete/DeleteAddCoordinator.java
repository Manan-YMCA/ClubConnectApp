package com.manan.dev.clubconnect.Delete;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.manan.dev.clubconnect.Models.Coordinator;

/**
 * Created by yatindhingra on 23/11/17.
 */

public class DeleteAddCoordinator {


    public void addCoordinator(Coordinator coordinator, String clubName){
        FirebaseDatabase.getInstance().getReference().child("coordinators").child(clubName).push().setValue(coordinator).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("addCoord", "coordinator added success");
                }
                else{
                    Log.d("addCoor", "coordinator added failed");
                }
            }
        });
    }

}
