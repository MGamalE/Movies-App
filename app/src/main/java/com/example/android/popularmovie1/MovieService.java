package com.example.android.popularmovie1;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface MovieService {
    @GET("movie/popular?")
    Call<MovieResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/{movie_id}/videos")
    Call<MovieTrailerResponse>getMovieTrailer(@Path("movie_id") int id,@Query("api_key") String apiKey);

    @GET("movie/{movie_id}/reviews")
    Call<MovieReviewResponse>getMovieReview(@Path("movie_id") int id,@Query("api_key") String apiKey);

}
