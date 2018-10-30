package com.example.android.movieshow;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Mohammad on 21/10/2017.
 */

public class MovieReviewResponse {

    @SerializedName("id")
    private int review_id;
    @SerializedName("results")
    private List<Review> results;

    public int getReview_id() {
        return review_id;
    }

    public void setReview_id(int review_id) {
        this.review_id = review_id;
    }

    public List<Review> getResults() {
        return results;
    }
}
