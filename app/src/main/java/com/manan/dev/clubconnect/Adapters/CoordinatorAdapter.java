package com.manan.dev.clubconnect.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.manan.dev.clubconnect.R;

import java.util.ArrayList;

public class CoordinatorAdapter extends ArrayAdapter {

    Context context;
    private ArrayList<String> coordinatorList;
    int resource;

    public CoordinatorAdapter(@NonNull Context context, int resource, String[] coordiList) {
        super(context, resource, coordiList);
        this.context = context;
        this.resource = resource;
        this.coordinatorList = new ArrayList<>();
        for(int i=0;coordiList[i]!=null;i++){
            coordinatorList.add(coordiList[i]);
            Log.d("aap",coordinatorList.get(i));
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);
        }

        String name = coordinatorList.get(position);
        if (name != null) {
            TextView lblName = (TextView) view.findViewById(R.id.coordinator_item_name);
            ImageView imgView = (ImageView) view.findViewById(R.id.cancel_button);
            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // removeCoordinatorfromEvent();
                }
            });
            if (lblName != null)
                lblName.setText(name);
        }
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return super.getFilter();
    }

}
