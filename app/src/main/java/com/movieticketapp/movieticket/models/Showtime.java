package com.movieticketapp.movieticket.models;

import com.google.firebase.firestore.DocumentId;
import java.util.List;

public class Showtime {
    @DocumentId
    private String id;
    private String movieId;
    private String theaterId;
    private String theaterName;
    private String movieTitle;
    private String date; // "2026-04-15"
    private String time; // "19:30"
    private long timestamp; // epoch millis for sorting/notification
    private double price;
    private int totalSeats;
    private List<String> bookedSeats; // e.g. ["A1", "A2", "B5"]

    public Showtime() {
    }

    public Showtime(String movieId, String theaterId, String date, String time,
            long timestamp, double price, int totalSeats) {
        this.movieId = movieId;
        this.theaterId = theaterId;
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
        this.price = price;
        this.totalSeats = totalSeats;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
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

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public List<String> getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(List<String> bookedSeats) {
        this.bookedSeats = bookedSeats;
    }
}
