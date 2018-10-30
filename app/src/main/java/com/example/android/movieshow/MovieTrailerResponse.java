package com.example.android.movieshow;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Mohammad on 20/10/2017.
 */

public class MovieTrailerResponse {

    @SerializedName("id")
    private int trailer_id;
    @SerializedName("results")
    private List<Trailer> results;

    public int getTrailer_id() {
        return trailer_id;
    }

    public void setTrailer_id(int trailer_id) {
        this.trailer_id = trailer_id;
    }

    public List<Trailer> getResults() {
        return results;
    }
}
