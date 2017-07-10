package com.cahyautama.popularmovie1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MoviesGridAdapter.ItemClickListener {

    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    private RecyclerView recyclerView;
    private Config config;
    public Retrofit retrofit;
    private MoviesGridAdapter adapter;
    private MovieDBService movieDBService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
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

        connectAndGetApiData();
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

    public static final String TAG = MainActivity.class.getSimpleName();

    void fetchMoviesByPopularity() {
        fetchMovies(movieDBService.getPopularMovies(config.apiKey));

        setTitle("Popular Movies");
    }

    void fetchMoviesByTopRated() {
        fetchMovies(movieDBService.getTopRatedMovies(config.apiKey));

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

    public void connectAndGetApiData(){
        fetchMoviesByPopularity();
    }

    @Override
    public void onItemClick(View view, int position) {
        Context context = MainActivity.this;
        Class movieDetailActivity = MovieDetailActivity.class;

        Intent showMovieDetail = new Intent(this, movieDetailActivity);
        Movie selectedMovie = adapter.movies.get(position);
        showMovieDetail.putExtra("movie", selectedMovie);
        startActivity(showMovieDetail);

        Log.d(TAG, "Item clicked at position: " + position);
    }
}
