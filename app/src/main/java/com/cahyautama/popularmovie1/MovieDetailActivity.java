package com.cahyautama.popularmovie1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    private Movie movie;
    @BindView(R.id.movie_title_view) TextView movieTitleView;
    @BindView(R.id.movie_image_view) ImageView moviePosterView;
    @BindView(R.id.movie_release_date) TextView movieReleaseDateView;
    @BindView(R.id.movie_vote_average) TextView movieVoteView;
    @BindView(R.id.movie_synopsis) TextView movieSynopsisView;

    public static final String IMAGE_URL_BASE_PATH="http://image.tmdb.org/t/p/w185/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        movie = intent.getParcelableExtra("movie");

        movieTitleView.setText(movie.getOriginalTitle());
        movieReleaseDateView.setText(movie.getReleaseDate());
        movieVoteView.setText(movie.getVoteAverage() + " / 10");
        movieSynopsisView.setText(movie.getOverview());

        String image_url = IMAGE_URL_BASE_PATH + movie.getPosterPath();
        Picasso.with(this)
                .load(image_url)
                .placeholder(android.R.drawable.sym_def_app_icon)
                .error(android.R.drawable.sym_def_app_icon)
                .into(moviePosterView);
    }
}
