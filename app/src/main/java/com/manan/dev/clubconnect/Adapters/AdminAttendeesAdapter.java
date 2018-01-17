package com.manan.dev.clubconnect.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.manan.dev.clubconnect.CircleTransform;
import com.manan.dev.clubconnect.Models.UserData;
import com.manan.dev.clubconnect.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by yatindhingra on 17/01/18.
 */

public class AdminAttendeesAdapter extends RecyclerView.Adapter<AdminAttendeesAdapter.ItemViewHolder> {

    private Context context;
    private ArrayList<UserData> dataList;

    public AdminAttendeesAdapter(Context context, ArrayList<UserData> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_view_attendees, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        final UserData user = dataList.get(position);
        holder.tvPhone.setText(user.getUserPhoneNo());
        holder.tvName.setText(user.getName());
        holder.tvEmail.setText(user.getEmailId());
        Picasso.with(context).load(user.getPhotoID()).transform(new CircleTransform()).into(holder.ivPhoto);
        //Toast.makeText(context, "11221", Toast.LENGTH_SHORT).show();

    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvName;
        protected TextView tvEmail;
        protected TextView tvPhone;
        protected ImageView ivPhoto;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_attendees_name);
            tvEmail = itemView.findViewById(R.id.tv_attendees_email);
            tvPhone = itemView.findViewById(R.id.tv_attendees_phone);
            ivPhoto = itemView.findViewById(R.id.iv_attendees_photo);

        }
    }
}
