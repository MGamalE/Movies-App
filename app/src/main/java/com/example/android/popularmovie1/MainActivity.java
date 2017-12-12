package com.example.android.popularmovie1;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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
    String most_popular = "Sorting by most popular";
    String favourite = "Sorting by favorite";
    String top_movie = "Sorting by vote average";
    MovieAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<Movie> movieList;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        if (savedInstanceState != null) {
            movieList = savedInstanceState.getParcelableArrayList("State");
        } else {
            movieList = new ArrayList<>();
        }

        gridLayoutManager = new GridLayoutManager(getActivity(), numberOfColumns());
        adapter = new MovieAdapter(this);
        recyclerView.setLayoutManager(gridLayoutManager);

        if (movieList != null && movieList.size() != 0) {
            adapter.setMovies(movieList);
            recyclerView.setAdapter(adapter);
        } else {
            checkSort();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("State", movieList);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
        String sortOrder = preferences.getString(
                this.getString(R.string.pref_sort_order_key),
                this.getString(R.string.favorite)
        );

        if (sortOrder.equals(this.getString(R.string.favorite))) {
            favView();
        }

    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDivider = 200;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
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
        String no_api = getResources().getString(R.string.no_api);
        final String fetch = getResources().getString(R.string.fetch_data);
        final String popular_movie = getResources().getString(R.string.popular_movie);


        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), no_api, Toast.LENGTH_SHORT).show();
                return;
            }

            MovieService apiService = MovieApi.getClient().create(MovieService.class);
            Call<MovieResponse> call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<MovieResponse>() {

                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    movieList = response.body().getResults();
                    adapter.setMovies(movieList);
                    recyclerView.setAdapter(adapter);

                    Toast.makeText(MainActivity.this, popular_movie, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Log.d("Error", "" + t.getMessage());
                    Toast.makeText(MainActivity.this, fetch, Toast.LENGTH_SHORT).show();

                }


            });

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void JSONTopRateMovie() {
        String no_api = getResources().getString(R.string.no_api);
        final String fetch = getResources().getString(R.string.fetch_data);
        final String top_rated = getResources().getString(R.string.top_rated);
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), no_api, Toast.LENGTH_SHORT).show();
                return;
            }

            MovieService apiService = MovieApi.getClient().create(MovieService.class);
            Call<MovieResponse> call = apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<MovieResponse>() {

                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    movieList = response.body().getResults();
                    adapter.setMovies(movieList);
                    recyclerView.setAdapter(adapter);
                    Toast.makeText(MainActivity.this, top_rated, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Log.d("Error", "" + t.getMessage());
                    Toast.makeText(MainActivity.this, fetch, Toast.LENGTH_SHORT).show();

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
        preferences.registerOnSharedPreferenceChangeListener(this);
        String sortOrder = preferences.getString(
                this.getString(R.string.pref_sort_order_key),
                this.getString(R.string.pref_most_popular)
        );
        if (sortOrder.equals(this.getString(R.string.pref_most_popular))) {
            Log.d(LOG_TAG, most_popular);
            JSONMostPopularMovie();
        } else if (sortOrder.equals(this.getString(R.string.favorite))) {
            Log.d(LOG_TAG, favourite);
            favView();

        } else {
            Log.d(LOG_TAG, top_movie);
            JSONTopRateMovie();
        }
    }


    private void favView() {
        String fv = getResources().getString(R.string.fv);

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
            retCursor.close();
            Toast.makeText(MainActivity.this, fv, Toast.LENGTH_SHORT).show();

            adapter.setMovies(movieList);
            gridLayoutManager = new GridLayoutManager(getActivity(), numberOfColumns());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(gridLayoutManager);

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


}