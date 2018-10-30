package com.example.android.movieshow;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mohammad on 20/10/2017.
 */

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.MyViewHolder> {

    private Context mContext;
    private List<Trailer> trailerList;

    public MovieTrailerAdapter(Context mContext, List<Trailer> trailerList) {
        this.mContext = mContext;
        this.trailerList = trailerList;

    }

    @Override
    public MovieTrailerAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.trailer_item, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final MovieTrailerAdapter.MyViewHolder viewHolder, int i) {
        viewHolder.title.setText(trailerList.get(i).getName());

    }

    @Override
    public int getItemCount() {

        return trailerList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;
        String youtubeURL = "https://www.youtube.com/watch?v=";

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String videoId = trailerList.get(position).getKey();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeURL + videoId));
                        intent.putExtra("VIDEO_ID", videoId);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);

                    }
                }
            });

        }
    }

}

