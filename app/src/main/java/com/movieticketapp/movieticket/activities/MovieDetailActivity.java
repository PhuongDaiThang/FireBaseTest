package com.movieticketapp.movieticket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.movieticketapp.movieticket.R;
import com.movieticketapp.movieticket.adapters.ShowtimeAdapter;
import com.movieticketapp.movieticket.firebase.FirebaseHelper;
import com.movieticketapp.movieticket.models.Movie;
import com.movieticketapp.movieticket.models.Showtime;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity implements ShowtimeAdapter.OnShowtimeClickListener {

    private ImageView imgPoster;
    private TextView tvTitle, tvGenre, tvDuration, tvRating, tvDescription;
    private RecyclerView rvShowtimes;
    private ShowtimeAdapter showtimeAdapter;
    private List<Showtime> showtimeList = new ArrayList<>();

    private String movieId;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        movieId = getIntent().getStringExtra("movieId");
        String movieTitle = getIntent().getStringExtra("movieTitle");

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(movieTitle);
        }

        initViews();
        loadMovieDetail();
        loadShowtimes();
    }

    private void initViews() {
        imgPoster = findViewById(R.id.imgPoster);
        tvTitle = findViewById(R.id.tvTitle);
        tvGenre = findViewById(R.id.tvGenre);
        tvDuration = findViewById(R.id.tvDuration);
        tvRating = findViewById(R.id.tvRating);
        tvDescription = findViewById(R.id.tvDescription);
        rvShowtimes = findViewById(R.id.rvShowtimes);

        rvShowtimes.setLayoutManager(new LinearLayoutManager(this));
        showtimeAdapter = new ShowtimeAdapter(showtimeList, this);
        rvShowtimes.setAdapter(showtimeAdapter);
    }

    private void loadMovieDetail() {
        FirebaseHelper.getInstance().getMovie(movieId).addOnSuccessListener(doc -> {
            movie = doc.toObject(Movie.class);
            if (movie != null) {
                movie.setId(doc.getId());
                tvTitle.setText(movie.getTitle());
                tvGenre.setText(movie.getGenre());
                tvDuration.setText(movie.getDuration() + " phút");
                tvRating.setText("⭐ " + movie.getRating());
                tvDescription.setText(movie.getDescription());

                Glide.with(this)
                        .load(movie.getPosterUrl())
                        .placeholder(R.drawable.ic_movie_placeholder)
                        .into(imgPoster);
            }
        });
    }

    private void loadShowtimes() {
        FirebaseHelper.getInstance().getShowtimesForMovie(movieId)
                .addOnSuccessListener(snapshots -> {
                    showtimeList.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Showtime showtime = doc.toObject(Showtime.class);
                        showtime.setId(doc.getId());
                        showtimeList.add(showtime);
                    }
                    showtimeAdapter.notifyDataSetChanged();

                    if (showtimeList.isEmpty()) {
                        Toast.makeText(this, "Không có suất chiếu nào", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi tải suất chiếu", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onShowtimeClick(Showtime showtime) {
        Intent intent = new Intent(this, SeatSelectionActivity.class);
        intent.putExtra("showtimeId", showtime.getId());
        intent.putExtra("movieId", movieId);
        intent.putExtra("movieTitle", movie.getTitle());
        intent.putExtra("posterUrl", movie.getPosterUrl());
        intent.putExtra("theaterName", showtime.getTheaterName());
        intent.putExtra("theaterId", showtime.getTheaterId());
        intent.putExtra("date", showtime.getDate());
        intent.putExtra("time", showtime.getTime());
        intent.putExtra("showtimeTimestamp", showtime.getTimestamp());
        intent.putExtra("price", showtime.getPrice());
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
