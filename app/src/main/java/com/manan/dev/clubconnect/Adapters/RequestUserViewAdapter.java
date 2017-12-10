package com.manan.dev.clubconnect.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Movie;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.manan.dev.clubconnect.CircleTransform;
import com.manan.dev.clubconnect.Models.Event;
import com.manan.dev.clubconnect.Models.UserData;
import com.manan.dev.clubconnect.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RequestUserViewAdapter extends RecyclerView.Adapter<RequestUserViewAdapter.MyViewHolder> {

    private final Context context;
    private final ProgressDialog pd;
    private final String clubName;
    private ArrayList<UserData> userID;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone;
        ImageView profilePic;
        Button acceptBtn, rejectBtn;

        MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.request_user_name);
            phone = (TextView) view.findViewById(R.id.request_user_mnumber);
            profilePic = (ImageView) view.findViewById(R.id.request_user_image);
            acceptBtn = (Button) view.findViewById(R.id.request_user_accept_btn);
            rejectBtn = (Button) view.findViewById(R.id.request_user_reject_btn);
        }
    }


    public RequestUserViewAdapter(ArrayList<UserData> userID, Context context) {
        this.userID = userID;
        this.context = context;
        this.pd = new ProgressDialog(context);


        FirebaseUser curUser = FirebaseAuth.getInstance().getCurrentUser();
        assert curUser != null;
        this.clubName = curUser.getDisplayName();
        pd.setMessage("Please Wait...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_user_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final UserData user = userID.get(position);
        Picasso.with(context).load(user.getPhotoID()).transform(new CircleTransform()).into(holder.profilePic);
        holder.name.setText(user.getName());
        holder.phone.setText(user.getUserPhoneNo());
        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                FirebaseDatabase.getInstance().getReference().child("notification").child(clubName).child(user.tempData).setValue(null)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (user.getPendingClubs() != null)
                                        user.getPendingClubs().remove(clubName);

                                    if (user.getMyClubs() == null)
                                        user.setMyClubs(new ArrayList<String>());
                                    user.getMyClubs().add(clubName);

                                    FirebaseDatabase.getInstance().getReference().child("users").child(user.UID).setValue(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    pd.dismiss();
                                                    if (task.isSuccessful())
                                                        Toast.makeText(context, "Request Accepted!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    pd.dismiss();
                                }
                            }
                        });

            }
        });
        holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                FirebaseDatabase.getInstance().getReference().child("notification").child(clubName).child(user.tempData).setValue(null)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (user.getPendingClubs() != null)
                                        user.getPendingClubs().remove(clubName);

                                    FirebaseDatabase.getInstance().getReference().child("users").child(user.UID).setValue(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    pd.dismiss();
                                                    if (task.isSuccessful())
                                                        Toast.makeText(context, "Request Rejected!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    pd.dismiss();
                                }
                            }
                        });

            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != userID ? userID.size() : 0);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView phone;
        ImageView profilePic;
        Button acceptBtn, rejectBtn;

        public ViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.request_user_name);
            phone = (TextView) view.findViewById(R.id.request_user_mnumber);
            profilePic = (ImageView) view.findViewById(R.id.request_user_image);
            acceptBtn = (Button) view.findViewById(R.id.request_user_accept_btn);
            rejectBtn = (Button) view.findViewById(R.id.request_user_reject_btn);
        }
    }
}