package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by irina on 14.03.2018.
 */

public class ReviewsAdapter extends ArrayAdapter<Review> {
    public ReviewsAdapter(@NonNull Context context, List<Review> reviews) {
        super(context, 0, reviews);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Review review = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_item, parent, false);
        }

        TextView user = convertView.findViewById(R.id.review_item_user_tv);
        TextView content = convertView.findViewById(R.id.review_item_content_tv);

        user.setText(review.getUser());
        content.setText(review.getContent());

        return convertView;
    }
}
