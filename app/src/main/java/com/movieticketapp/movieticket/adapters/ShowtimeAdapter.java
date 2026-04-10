package com.movieticketapp.movieticket.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.movieticketapp.movieticket.R;
import com.movieticketapp.movieticket.models.Showtime;

import java.util.List;

public class ShowtimeAdapter extends RecyclerView.Adapter<ShowtimeAdapter.ShowtimeViewHolder> {

    public interface OnShowtimeClickListener {
        void onShowtimeClick(Showtime showtime);
    }

    private final List<Showtime> showtimes;
    private final OnShowtimeClickListener listener;

    public ShowtimeAdapter(List<Showtime> showtimes, OnShowtimeClickListener listener) {
        this.showtimes = showtimes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShowtimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_showtime, parent, false);
        return new ShowtimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowtimeViewHolder holder, int position) {
        Showtime showtime = showtimes.get(position);
        holder.tvTheater.setText(showtime.getTheaterName());
        holder.tvDate.setText(showtime.getDate());
        holder.tvTime.setText(showtime.getTime());
        holder.tvPrice.setText(String.format("%,.0f VNĐ", showtime.getPrice()));

        int available = showtime.getTotalSeats();
        if (showtime.getBookedSeats() != null) {
            available -= showtime.getBookedSeats().size();
        }
        holder.tvAvailable.setText(available + " ghế trống");

        holder.itemView.setOnClickListener(v -> listener.onShowtimeClick(showtime));
    }

    @Override
    public int getItemCount() {
        return showtimes.size();
    }

    static class ShowtimeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTheater, tvDate, tvTime, tvPrice, tvAvailable;

        ShowtimeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTheater = itemView.findViewById(R.id.tvTheater);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvAvailable = itemView.findViewById(R.id.tvAvailable);
        }
    }
}
