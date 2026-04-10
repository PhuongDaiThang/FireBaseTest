package com.movieticketapp.movieticket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.movieticketapp.movieticket.R;
import com.movieticketapp.movieticket.adapters.MovieAdapter;
import com.movieticketapp.movieticket.firebase.FirebaseHelper;
import com.movieticketapp.movieticket.models.Movie;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnMovieClickListener {

    private RecyclerView rvMovies;
    private MovieAdapter adapter;
    private List<Movie> movieList = new ArrayList<>();
    private ListenerRegistration movieListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));
        setTitle("Movie Ticket");

        // Seed sample data on first run
        FirebaseHelper.getInstance().seedSampleData();

        rvMovies = findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new MovieAdapter(movieList, this);
        rvMovies.setAdapter(adapter);

        loadMovies();
    }

    private void loadMovies() {
        movieListener = FirebaseHelper.getInstance().listenMovies((snapshots, error) -> {
            if (error != null || snapshots == null)
                return;

            movieList.clear();
            for (QueryDocumentSnapshot doc : snapshots) {
                Movie movie = doc.toObject(Movie.class);
                movie.setId(doc.getId());
                movieList.add(movie);
            }
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("movieId", movie.getId());
        intent.putExtra("movieTitle", movie.getTitle());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_my_tickets) {
            startActivity(new Intent(this, MyTicketsActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            FirebaseHelper.getInstance().getAuth().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (movieListener != null)
            movieListener.remove();
    }
}
