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
import android.widget.Toast;

import com.manan.dev.clubconnect.CircleTransform;
import com.manan.dev.clubconnect.Models.Coordinator;
import com.manan.dev.clubconnect.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CoordinatorAdapter extends ArrayAdapter<Coordinator> {

    private Context context;
    //private final  ArrayList<String> coordinatorNames;
    private int resource;
    private ArrayList<Coordinator> items;//, suggestions;
    private ArrayList<Coordinator> itemsAll;
    private ArrayList<Coordinator> suggestions;


    public CoordinatorAdapter(Context context, int resource, int tvResId, ArrayList<Coordinator> coordiList) {
        super(context, resource, tvResId, coordiList);
        this.context = context;
        this.resource = resource;
        this.items = coordiList;
        this.itemsAll = (ArrayList<Coordinator>) items.clone();
        this.suggestions = new ArrayList<>();

        //suggestions = new ArrayList<>();
    }

    public void updateList(ArrayList<Coordinator> items)
    {
        this.items = items;
        this.itemsAll = (ArrayList<Coordinator>) items.clone();
        suggestions.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            view = inflater.inflate(resource, parent, false);
        }
        Coordinator coordinator = items.get(position);
        if (coordinator != null) {
            TextView lblName = (TextView) view.findViewById(R.id.coordinator_item_name);
            ImageView imgView = (ImageView) view.findViewById(R.id.cancel_coordinator);
            lblName.setText(coordinator.getName());
            Log.d("getView",coordinator.getPhoto());
            Picasso.with(context).load(coordinator.getPhoto()).resize(50,50).centerCrop().transform(new CircleTransform()).into(imgView);
        }
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    private Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return ((Coordinator)(resultValue)).getName();
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                suggestions.clear();
                for (Coordinator coordinator : itemsAll) {
                    if(coordinator.getName().toLowerCase().startsWith(constraint.toString().toLowerCase().trim())){
                        suggestions.add(coordinator);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                Collections.copy(suggestions,itemsAll);
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            }
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<Coordinator> filteredList = (ArrayList<Coordinator>) results.values;
            if(results != null && results.count > 0) {
                clear();
                for (Coordinator c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };
}
