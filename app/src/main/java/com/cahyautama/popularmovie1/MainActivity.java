package com.cahyautama.popularmovie1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

        connectAndGetApiData();
    }

    public static final String TAG = MainActivity.class.getSimpleName();

    public void connectAndGetApiData(){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        MovieDBService movieDBService = retrofit.create(MovieDBService.class);
        Call<MovieResponse> call = movieDBService.getTopRatedMovies(config.apiKey);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                List<Movie> movies = response.body().getResults();

                adapter.movies = movies;

                recyclerView.setAdapter(adapter);

                Log.d(TAG, "Number of movies received: " + movies.size());
            }
            @Override
            public void onFailure(Call<MovieResponse> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "Item clicked at position: " + position);
    }
}
