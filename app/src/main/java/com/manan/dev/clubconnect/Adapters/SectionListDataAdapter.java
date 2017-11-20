package com.manan.dev.clubconnect.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.manan.dev.clubconnect.Models.Event;
import com.manan.dev.clubconnect.R;
import com.manan.dev.clubconnect.User.EventsDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by shubhamsharma on 17/11/17.
 */

public class SectionListDataAdapter extends RecyclerView.Adapter<SectionListDataAdapter.SingleItemRowHolder> {

    private ArrayList<Event> itemsList;
    private Context mContext;
    Event singleItem;

    public SectionListDataAdapter(Context context, ArrayList<Event> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_single_card, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        singleItem = itemsList.get(i);

        holder.tvTitle.setText(singleItem.getEventName());
        holder.dateTimeTextView.setText(Long.toString(singleItem.getDays().get(0).getDate()) + "," + Long.toString(singleItem.getDays().get(0).getStartTime()));
        holder.tvClubName.setText(singleItem.getClubName());



Picasso.with(mContext).load(singleItem.getPhotoID().getPosters().get(0)).resize(150, 110).centerCrop().into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;

        protected ImageView itemImage;
        protected  TextView dateTimeTextView;
        protected TextView tvClubName;


        public SingleItemRowHolder(View view) {
            super(view);

            this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            this.itemImage = (ImageView) view.findViewById(R.id.itemImage);
            this.dateTimeTextView=(TextView) view.findViewById(R.id.tv_dateTime);
            this.tvClubName = (TextView) view.findViewById(R.id.tv_clubname);
//
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position  = getLayoutPosition();
                    Toast.makeText(mContext, itemsList.get(position).getEventId(), Toast.LENGTH_LONG).show();
                    mContext.startActivity(new Intent(mContext, EventsDetailsActivity.class).putExtra("eventToken", itemsList.get(position).getEventId()));

                }
            });


        }

    }

}