package com.example.android.popularmovie1;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.popularmovie1.MovieDBHelper.LOG_TAG;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView recyclerView;
    private ArrayList<Movie> movieList;
    private Parcelable mList;
    MovieAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        movieList = new ArrayList<>();
        adapter = new MovieAdapter(this, movieList);
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }
        recyclerView.setAdapter(adapter);
        checkSort();



    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        movieList = (ArrayList<Movie>) recyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelableArrayList("movie", movieList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null)
            movieList = savedInstanceState.getParcelable("movie");

    }

    @Override
    public void onResume() {
        super.onResume();
        if (movieList != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(movieList);
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

    private void JSONMostPopularMovie() {
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "There Is No API Key!", Toast.LENGTH_SHORT).show();
                return;
            }

            MovieApi movieapi = new MovieApi();
            MovieService apiService = MovieApi.getClient().create(MovieService.class);
            Call<MovieResponse> call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<MovieResponse>() {

                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    ArrayList<Movie> movies = response.body().getResults();
                    recyclerView.setAdapter(new MovieAdapter(getApplicationContext(), movies));
                    Toast.makeText(MainActivity.this, "Displaying By Most Popular Movies", Toast.LENGTH_SHORT).show();
                    recyclerView.smoothScrollToPosition(0);

                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Log.d("Error", "" + t.getMessage());
                    Toast.makeText(MainActivity.this, "Error Fetching Data! And Internet Connection", Toast.LENGTH_SHORT).show();

                }


            });

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void JSONTopRateMovie() {
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "There Is No API Key", Toast.LENGTH_SHORT).show();
                return;
            }

            MovieApi movieapi = new MovieApi();
            MovieService apiService = MovieApi.getClient().create(MovieService.class);
            Call<MovieResponse> call = apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<MovieResponse>() {

                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    ArrayList<Movie> movies = response.body().getResults();
                    recyclerView.setAdapter(new MovieAdapter(getApplicationContext(), movies));
                    Toast.makeText(MainActivity.this, "Displaying By Top Rated Movies", Toast.LENGTH_SHORT).show();
                    recyclerView.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Log.d("Error", "" + t.getMessage());
                    Toast.makeText(MainActivity.this, "Error Fetching Data! And Internet Connection", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent setting = new Intent(this, MovieSettings.class);
                startActivity(setting);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        checkSort();
    }

    private void checkSort() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = preferences.getString(
                this.getString(R.string.pref_sort_order_key),
                this.getString(R.string.pref_most_popular)
        );
        if (sortOrder.equals(this.getString(R.string.pref_most_popular))) {
            Log.d(LOG_TAG, "Sorting by most popular");
            JSONMostPopularMovie();
        } else if (sortOrder.equals(this.getString(R.string.favorite))) {
            Log.d(LOG_TAG, "Sorting by favorite");
            favView();

        } else {
            Log.d(LOG_TAG, "Sorting by vote average");
            JSONTopRateMovie();
        }
    }

    private void favView() {

        try {

            movieList.clear();
            Cursor retCursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
            if (retCursor.moveToFirst()) {
                do {
                    Movie favourite = new Movie();
                    favourite.id = retCursor.getInt(retCursor.getColumnIndex(MovieContract.MovieEntry.ID));
                    favourite.originalTitle = retCursor.getString(retCursor.getColumnIndex(MovieContract.MovieEntry.TITLE));
                    favourite.overview = retCursor.getString(retCursor.getColumnIndex(MovieContract.MovieEntry.OVERVIEW));
                    favourite.releaseDate = retCursor.getString(retCursor.getColumnIndex(MovieContract.MovieEntry.RELEASE_DATE));
                    favourite.voteAverage = retCursor.getDouble(retCursor.getColumnIndex(MovieContract.MovieEntry.VOTE));
                    favourite.posterPath = retCursor.getString(retCursor.getColumnIndex(MovieContract.MovieEntry.POSTER_PATH));

                    movieList.add(favourite);
                } while (retCursor.moveToNext());
            }
            Toast.makeText(MainActivity.this, "Favourite List", Toast.LENGTH_SHORT).show();

            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            adapter = new MovieAdapter(this, movieList);
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
            }
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


}

