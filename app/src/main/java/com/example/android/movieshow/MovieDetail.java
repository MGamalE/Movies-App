package com.example.android.movieshow;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;


public class MovieDetail extends AppCompatActivity {
    String insert = "Inserted To Favourite";
    String delete = "Deleted From Favourite";
    TextView nameOfMovie, plotSynopsis, userRating, releaseDate;
    ImageView imageView;
    Movie movie;
    String thumbnail, movieName, synopsis, rating, dateOfRelease;
    int movie_id;
    private RecyclerView recyclerView;
    private MovieTrailerAdapter adapter;
    private List<Trailer> trailerList;

    private RecyclerView recyclerViewReview;
    private MovieReviewAdapter adapterReview;
    private List<Review> reviewList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        imageView = (ImageView) findViewById(R.id.thumbnail_header);
        nameOfMovie = (TextView) findViewById(R.id.title);
        plotSynopsis = (TextView) findViewById(R.id.plotsynopsis);
        userRating = (TextView) findViewById(R.id.userrating);
        releaseDate = (TextView) findViewById(R.id.releasedate);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra("movie")) {
            movie = getIntent().getParcelableExtra("movie");
            thumbnail = movie.getPosterPath();
            movieName = movie.getOriginalTitle();
            synopsis = movie.getOverview();
            rating = Double.toString(movie.getVoteAverage());
            dateOfRelease = movie.getReleaseDate();
            movie_id = movie.getId();
            String poster = "https://image.tmdb.org/t/p/w185" + thumbnail;
            Picasso.with(this)
                    .load(poster)
                    .into(imageView);

            nameOfMovie.setText(movieName);
            plotSynopsis.setText(synopsis);
            userRating.setText(rating);
            releaseDate.setText(dateOfRelease);

        } else {
            Toast.makeText(this, "No Api Data", Toast.LENGTH_SHORT).show();
        }


        LikeButton likeButton = (LikeButton) findViewById(R.id.like);

        try {
            Cursor c = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, "id= " + movie.id, null, null);

            boolean flag = false;
            if (c.moveToFirst()) {
                do {
                    flag = true;
                } while (c.moveToNext());

            }

            if (flag) {
                likeButton.setLiked(true);
            }
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

                ContentValues values = new ContentValues();
                values.put(MovieContract.MovieEntry.ID, movie.getId());
                values.put(MovieContract.MovieEntry.TITLE, movie.getOriginalTitle());
                values.put(MovieContract.MovieEntry.OVERVIEW,
                        movie.getOverview());
                values.put(MovieContract.MovieEntry.RELEASE_DATE,
                        movie.getReleaseDate());
                values.put(MovieContract.MovieEntry.VOTE,
                        movie.getVoteAverage());
                values.put(MovieContract.MovieEntry.POSTER_PATH,
                        movie.getPosterPath());


                getActivity().getContentResolver().insert(
                        MovieContract.MovieEntry.CONTENT_URI, values);
                Toast.makeText(MovieDetail.this, insert, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void unLiked(LikeButton likeButton) {
                getActivity().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, "id= " + movie.id, null);
                Toast.makeText(MovieDetail.this, delete, Toast.LENGTH_SHORT).show();
            }
        });


        trailerList = new ArrayList<>();
        adapter = new MovieTrailerAdapter(this, trailerList);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_trailer);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        JSONTrailer();


        reviewList = new ArrayList<>();
        adapterReview = new MovieReviewAdapter(this, reviewList);
        recyclerViewReview = (RecyclerView) findViewById(R.id.recycler_view_review);
        RecyclerView.LayoutManager mLayoutManagerReview = new LinearLayoutManager(getApplicationContext());
        recyclerViewReview.setLayoutManager(mLayoutManagerReview);
        recyclerViewReview.setAdapter(adapterReview);
        adapterReview.notifyDataSetChanged();

        JSONReview();
    }

    private void JSONTrailer() {
        int movie_id = getIntent().getExtras().getInt("id");
        String no_api = getResources().getString(R.string.no_api);
        final String fetch_trailer = getResources().getString(R.string.fetch__trailer_data);
        try {

            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), no_api, Toast.LENGTH_SHORT).show();
                return;
            }
            MovieApi movieApi = new MovieApi();
            MovieService apiService = MovieApi.getClient().create(MovieService.class);
            retrofit2.Call<MovieTrailerResponse> call = apiService.getMovieTrailer(movie_id, BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<MovieTrailerResponse>() {

                @Override
                public void onResponse(retrofit2.Call<MovieTrailerResponse> call, Response<MovieTrailerResponse> response) {
                    List<Trailer> trailer = response.body().getResults();
                    recyclerView.setAdapter(new MovieTrailerAdapter(getApplicationContext(), trailer));
                    recyclerView.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(retrofit2.Call<MovieTrailerResponse> call, Throwable t) {
                    Log.d("Error", "" + t.getMessage());
                    Toast.makeText(MovieDetail.this, fetch_trailer, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }


    private void JSONReview() {
        int movie_id = getIntent().getExtras().getInt("id");
        String no_api = getResources().getString(R.string.no_api);
        final String fetch_review = getResources().getString(R.string.fetch_review_data);

        try {

            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), no_api, Toast.LENGTH_SHORT).show();
                return;
            }
            MovieApi movieApi = new MovieApi();
            MovieService apiService = MovieApi.getClient().create(MovieService.class);
            retrofit2.Call<MovieReviewResponse> call = apiService.getMovieReview(movie_id, BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<MovieReviewResponse>() {

                @Override
                public void onResponse(retrofit2.Call<MovieReviewResponse> call, Response<MovieReviewResponse> response) {
                    List<Review> review = response.body().getResults();
                    recyclerViewReview.setAdapter(new MovieReviewAdapter(getApplicationContext(), review));
                    recyclerViewReview.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(retrofit2.Call<MovieReviewResponse> call, Throwable t) {
                    Log.d("Error", "" + t.getMessage());
                    Toast.makeText(MovieDetail.this, fetch_review, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    public Activity getActivity() {
        Context context = this;
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;

    }
}

