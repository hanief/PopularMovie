package com.cahyautama.popularmovie1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MoviesGridAdapter.ItemClickListener {

    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    private Config config;
    public Retrofit retrofit;
    private MoviesGridAdapter adapter;
    private MovieDBService movieDBService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new MoviesGridAdapter(this);
        adapter.setClickListener(this);

        config = new Config(this);

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        movieDBService = retrofit.create(MovieDBService.class);

        fetchMoviesByPopularity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_popular:
                fetchMoviesByPopularity();
                return true;
            case R.id.sort_top_rated:
                fetchMoviesByTopRated();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static final String TAG = MainActivity.class.getSimpleName();

    void fetchMoviesByPopularity() {
        if (isOnline()) {
            fetchMovies(movieDBService.getPopularMovies(config.apiKey));
        } else {
            Toast.makeText(this, "Network not available. Try again later.", Toast.LENGTH_SHORT).show();
        }

        setTitle("Popular Movies");
    }

    void fetchMoviesByTopRated() {
        if (isOnline()) {
            fetchMovies(movieDBService.getTopRatedMovies(config.apiKey));
        } else {
            Toast toast = Toast.makeText(this, "Network not available. Try again later.", Toast.LENGTH_SHORT);
            toast.show();
        }

        setTitle("Top Rated Movies");
    }

    void fetchMovies(Call<MovieResponse> call) {
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                List<Movie> movies = response.body().getResults();

                adapter.movies = movies;

                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onFailure(Call<MovieResponse> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Class movieDetailActivity = MovieDetailActivity.class;

        Intent showMovieDetail = new Intent(this, movieDetailActivity);
        Movie selectedMovie = adapter.movies.get(position);
        showMovieDetail.putExtra("movie", selectedMovie);
        startActivity(showMovieDetail);

        Log.d(TAG, "Item clicked at position: " + position);
    }
}
