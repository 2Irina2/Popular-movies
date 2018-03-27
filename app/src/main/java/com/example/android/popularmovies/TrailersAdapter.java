package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by irina on 10.03.2018.
 */

public class TrailersAdapter extends ArrayAdapter<Trailer> {
    public TrailersAdapter(@NonNull Context context, List<Trailer> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Trailer trailer = getItem(position);


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_item, parent, false);
        }

        TextView trailerName = convertView.findViewById(R.id.trailer_item_name_tv);
        trailerName.setText(trailer.getTrailerName());

        return convertView;
    }
}
