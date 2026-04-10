package com.movieticketapp.movieticket.models;

import com.google.firebase.firestore.DocumentId;
import java.util.List;

public class Ticket {
    @DocumentId
    private String id;
    private String userId;
    private String movieId;
    private String movieTitle;
    private String posterUrl;
    private String theaterId;
    private String theaterName;
    private String showtimeId;
    private String date;
    private String time;
    private long showtimeTimestamp;
    private List<String> seats;
    private double totalPrice;
    private long bookedAt;
    private String status; // "active", "used", "cancelled"

    public Ticket() {
    }

    public Ticket(String userId, String movieId, String movieTitle, String posterUrl,
            String theaterId, String theaterName, String showtimeId,
            String date, String time, long showtimeTimestamp,
            List<String> seats, double totalPrice) {
        this.userId = userId;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.posterUrl = posterUrl;
        this.theaterId = theaterId;
        this.theaterName = theaterName;
        this.showtimeId = showtimeId;
        this.date = date;
        this.time = time;
        this.showtimeTimestamp = showtimeTimestamp;
        this.seats = seats;
        this.totalPrice = totalPrice;
        this.bookedAt = System.currentTimeMillis();
        this.status = "active";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getTheaterId() {
        return theaterId;
    }

    public void setTheaterId(String theaterId) {
        this.theaterId = theaterId;
    }

    public String getTheaterName() {
        return theaterName;
    }

    public void setTheaterName(String theaterName) {
        this.theaterName = theaterName;
    }

    public String getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(String showtimeId) {
        this.showtimeId = showtimeId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getShowtimeTimestamp() {
        return showtimeTimestamp;
    }

    public void setShowtimeTimestamp(long showtimeTimestamp) {
        this.showtimeTimestamp = showtimeTimestamp;
    }

    public List<String> getSeats() {
        return seats;
    }

    public void setSeats(List<String> seats) {
        this.seats = seats;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(long bookedAt) {
        this.bookedAt = bookedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
