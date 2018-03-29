package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by irina on 10.03.2018.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {

    private List<Trailer> mData;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ItemClickListener mClickListener;

    public TrailersAdapter(Context context, List<Trailer> data){
        mData = data;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public TrailersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.trailer_item, parent, false);
        return new TrailersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Trailer trailer = mData.get(position);
        String videoId = trailer.getTrailerRawPath();
        String thumbnailUrl = "http://img.youtube.com/vi/"+videoId+"/0.jpg";


        Picasso.with(mContext)
                .load(thumbnailUrl)
                .placeholder(R.drawable.ic_clear_black_120dp)
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public Trailer getItem(int id){
        return mData.get(id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.trailer_item_thumbnail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mClickListener != null){
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    void setClickListener(TrailersAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }

}
