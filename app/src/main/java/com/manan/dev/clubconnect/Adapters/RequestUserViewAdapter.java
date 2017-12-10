package com.manan.dev.clubconnect.Adapters;

import android.content.Context;
import android.graphics.Movie;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.manan.dev.clubconnect.CircleTransform;
import com.manan.dev.clubconnect.Models.Event;
import com.manan.dev.clubconnect.Models.UserData;
import com.manan.dev.clubconnect.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
 
public class RequestUserViewAdapter extends RecyclerView.Adapter<RequestUserViewAdapter.MyViewHolder> {

    private final Context context;
    private ArrayList<UserData> userID;
 
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, phone;
        public ImageView profilePic;
        public Button acceptBtn ,rejectBtn;
 
        public MyViewHolder(View view) {
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
    }
 
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_user_item, parent, false);
 
        return new MyViewHolder(itemView);
    }
 
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final UserData u = userID.get(position);
        Picasso.with(context).load(u.getPhotoID()).transform(new CircleTransform()).into(holder.profilePic);
        String name,phone;
       holder.name.setText(u.getName());
       holder.phone.setText(u.getUserPhoneNo());
       holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

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