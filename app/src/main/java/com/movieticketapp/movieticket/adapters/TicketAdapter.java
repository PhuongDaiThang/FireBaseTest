package com.movieticketapp.movieticket.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.movieticketapp.movieticket.R;
import com.movieticketapp.movieticket.models.Ticket;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private final List<Ticket> tickets;

    public TicketAdapter(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = tickets.get(position);

        holder.tvMovieTitle.setText(ticket.getMovieTitle());
        holder.tvTheater.setText(ticket.getTheaterName());
        holder.tvDateTime.setText(ticket.getDate() + " | " + ticket.getTime());
        holder.tvSeats.setText("Ghế: " + String.join(", ", ticket.getSeats()));
        holder.tvPrice.setText(String.format("%,.0f VNĐ", ticket.getTotalPrice()));
        holder.tvStatus.setText(ticket.getStatus().toUpperCase());

        switch (ticket.getStatus()) {
            case "active":
                holder.tvStatus.setTextColor(0xFF4CAF50); // Green
                break;
            case "used":
                holder.tvStatus.setTextColor(0xFF9E9E9E); // Grey
                break;
            case "cancelled":
                holder.tvStatus.setTextColor(0xFFF44336); // Red
                break;
        }

        Glide.with(holder.imgPoster.getContext())
                .load(ticket.getPosterUrl())
                .placeholder(R.drawable.ic_movie_placeholder)
                .into(holder.imgPoster);
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView tvMovieTitle, tvTheater, tvDateTime, tvSeats, tvPrice, tvStatus;

        TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvTheater = itemView.findViewById(R.id.tvTheater);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvSeats = itemView.findViewById(R.id.tvSeats);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
