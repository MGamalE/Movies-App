package com.example.android.popularmovie1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mohammad on 21/10/2017.
 */

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MyViewHolder> {

    private Context mContext;
    private List<Review> reviewList;

    public MovieReviewAdapter(Context mContext, List<Review> reviewList) {
        this.mContext = mContext;
        this.reviewList = reviewList;

    }

    @Override
    public MovieReviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.review_item, viewGroup, false);
        return new MyViewHolder(view);

    }


    @Override
    public void onBindViewHolder(final MovieReviewAdapter.MyViewHolder viewHolder, int i) {
        viewHolder.author.setText(reviewList.get(i).getAuthor());
        viewHolder.content.setText(reviewList.get(i).getContent());

    }

    @Override
    public int getItemCount() {

        return reviewList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView author;
        public TextView content;


        public MyViewHolder(View view) {
            super(view);
            author = (TextView) view.findViewById(R.id.author);
            content = (TextView) view.findViewById(R.id.content);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String reviewId = reviewList.get(position).getUrl();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(reviewId));
                        intent.putExtra("REVIEW_ID", reviewId);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);

                    }
                }
            });

        }
    }
}
