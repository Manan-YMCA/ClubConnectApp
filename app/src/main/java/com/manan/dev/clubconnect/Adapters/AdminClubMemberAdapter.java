package com.manan.dev.clubconnect.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manan.dev.clubconnect.CircleTransform;
import com.manan.dev.clubconnect.ClubMembersAdmin;
import com.manan.dev.clubconnect.Models.UserData;
import com.manan.dev.clubconnect.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by yatindhingra on 14/01/18.
 */

public class AdminClubMemberAdapter extends RecyclerView.Adapter<AdminClubMemberAdapter.ViewHolder> {

    private final Context context;
    //private final ProgressDialog pd;
    private final String clubName;
    private ArrayList<UserData> userID;

    public AdminClubMemberAdapter(Context context, ArrayList<UserData> userID) {
        this.context = context;
        this.userID = userID;
        //this.pd = new ProgressDialog(context);
        FirebaseUser curUser = FirebaseAuth.getInstance().getCurrentUser();
        assert curUser != null;
        this.clubName = curUser.getDisplayName();
        Log.d("sizeUserList", Integer.toString(userID.size()));
    }

    @Override
    public AdminClubMemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_members_club_admin, parent, false);
        return new AdminClubMemberAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AdminClubMemberAdapter.ViewHolder holder, int position) {
        final UserData user = userID.get(position);
        Picasso.with(context).load(user.getPhotoID()).transform(new CircleTransform()).into(holder.memberPhoto);
        holder.memberEmail.setText(user.getEmailId());
        holder.memberPhone.setText(user.getUserPhoneNo());
        holder.memberName.setText(user.getName());
        holder.memberView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ClubMembersAdmin.class);
                i.putExtra("userId", user.UID);
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != userID ? userID.size() : 0);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView memberName, memberPhone, memberEmail;
        ImageView memberPhoto;
        CardView memberView;
        ViewHolder(View view) {
            super(view);
            memberView = (CardView) view.findViewById(R.id.member_card_view);
            memberName = (TextView) view.findViewById(R.id.member_user_name);
            memberPhone = (TextView) view.findViewById(R.id.member_user_number);
            memberEmail = (TextView) view.findViewById(R.id.member_user_emailid);
            memberPhoto = (ImageView) view.findViewById(R.id.member_user_image);
        }
    }
}
