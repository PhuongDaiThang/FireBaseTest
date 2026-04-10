package com.movieticketapp.movieticket.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.movieticketapp.movieticket.R;
import com.movieticketapp.movieticket.firebase.FirebaseHelper;
import com.movieticketapp.movieticket.firebase.ShowtimeReminderReceiver;
import com.movieticketapp.movieticket.models.Ticket;

import java.util.ArrayList;
import java.util.List;

public class SeatSelectionActivity extends AppCompatActivity {

    private GridLayout gridSeats;
    private TextView tvMovieTitle, tvShowInfo, tvSelectedSeats, tvTotalPrice;
    private Button btnConfirmBooking;

    private String showtimeId, movieId, movieTitle, posterUrl;
    private String theaterName, theaterId, date, time;
    private long showtimeTimestamp;
    private double pricePerSeat;

    private List<String> selectedSeats = new ArrayList<>();
    private List<String> bookedSeats = new ArrayList<>();

    private static final int ROWS = 8;
    private static final int COLS = 10;
    private static final String[] ROW_LABELS = { "A", "B", "C", "D", "E", "F", "G", "H" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chọn ghế");
        }

        getIntentData();
        initViews();
        loadBookedSeats();
    }

    private void getIntentData() {
        showtimeId = getIntent().getStringExtra("showtimeId");
        movieId = getIntent().getStringExtra("movieId");
        movieTitle = getIntent().getStringExtra("movieTitle");
        posterUrl = getIntent().getStringExtra("posterUrl");
        theaterName = getIntent().getStringExtra("theaterName");
        theaterId = getIntent().getStringExtra("theaterId");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        showtimeTimestamp = getIntent().getLongExtra("showtimeTimestamp", 0);
        pricePerSeat = getIntent().getDoubleExtra("price", 0);
    }

    private void initViews() {
        gridSeats = findViewById(R.id.gridSeats);
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvShowInfo = findViewById(R.id.tvShowInfo);
        tvSelectedSeats = findViewById(R.id.tvSelectedSeats);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        tvMovieTitle.setText(movieTitle);
        tvShowInfo.setText(theaterName + " | " + date + " | " + time);
        updateSelectionInfo();

        btnConfirmBooking.setOnClickListener(v -> confirmBooking());
    }

    private void loadBookedSeats() {
        FirebaseHelper.getInstance().getShowtime(showtimeId).addOnSuccessListener(doc -> {
            List<String> booked = (List<String>) doc.get("bookedSeats");
            if (booked != null) {
                bookedSeats.addAll(booked);
            }
            buildSeatGrid();
        });
    }

    private void buildSeatGrid() {
        gridSeats.removeAllViews();
        gridSeats.setColumnCount(COLS);
        gridSeats.setRowCount(ROWS);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                String seatId = ROW_LABELS[row] + (col + 1);
                TextView seat = new TextView(this);
                seat.setText(seatId);
                seat.setTextSize(10);
                seat.setPadding(8, 12, 8, 12);
                seat.setGravity(android.view.Gravity.CENTER);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(col, 1f);
                params.rowSpec = GridLayout.spec(row);
                params.setMargins(2, 2, 2, 2);
                seat.setLayoutParams(params);

                if (bookedSeats.contains(seatId)) {
                    seat.setBackgroundResource(R.drawable.seat_booked);
                    seat.setTextColor(getResources().getColor(android.R.color.white));
                    seat.setEnabled(false);
                } else {
                    seat.setBackgroundResource(R.drawable.seat_available);
                    seat.setTextColor(getResources().getColor(android.R.color.black));
                    seat.setOnClickListener(v -> toggleSeat(seat, seatId));
                }

                gridSeats.addView(seat);
            }
        }
    }

    private void toggleSeat(TextView seatView, String seatId) {
        if (selectedSeats.contains(seatId)) {
            selectedSeats.remove(seatId);
            seatView.setBackgroundResource(R.drawable.seat_available);
            seatView.setTextColor(getResources().getColor(android.R.color.black));
        } else {
            selectedSeats.add(seatId);
            seatView.setBackgroundResource(R.drawable.seat_selected);
            seatView.setTextColor(getResources().getColor(android.R.color.white));
        }
        updateSelectionInfo();
    }

    private void updateSelectionInfo() {
        if (selectedSeats.isEmpty()) {
            tvSelectedSeats.setText("Chưa chọn ghế");
            tvTotalPrice.setText("0 VNĐ");
            btnConfirmBooking.setEnabled(false);
        } else {
            tvSelectedSeats.setText("Ghế: " + String.join(", ", selectedSeats));
            double total = selectedSeats.size() * pricePerSeat;
            tvTotalPrice.setText(String.format("%,.0f VNĐ", total));
            btnConfirmBooking.setEnabled(true);
        }
    }

    private void confirmBooking() {
        if (selectedSeats.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất 1 ghế", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseHelper.getInstance().getCurrentUser().getUid();
        double totalPrice = selectedSeats.size() * pricePerSeat;

        Ticket ticket = new Ticket(userId, movieId, movieTitle, posterUrl,
                theaterId, theaterName, showtimeId,
                date, time, showtimeTimestamp,
                new ArrayList<>(selectedSeats), totalPrice);

        btnConfirmBooking.setEnabled(false);
        btnConfirmBooking.setText("Đang đặt vé...");

        FirebaseHelper.getInstance().bookTicket(ticket, showtimeId, selectedSeats)
                .addOnSuccessListener(ticketRef -> {
                    // Schedule showtime reminder notification
                    ShowtimeReminderReceiver.scheduleReminder(this,
                            ticketRef.getId(), movieTitle, time, theaterName, showtimeTimestamp);

                    Toast.makeText(this, "Đặt vé thành công!", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnConfirmBooking.setEnabled(true);
                    btnConfirmBooking.setText("Xác nhận đặt vé");
                    Toast.makeText(this, "Đặt vé thất bại: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
