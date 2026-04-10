package com.movieticketapp.movieticket.models;

import com.google.firebase.firestore.DocumentId;
import java.util.List;

public class Theater {
    @DocumentId
    private String id;
    private String name;
    private String address;
    private int totalSeats;
    private List<String> facilities; // e.g. "IMAX", "4DX", "Dolby Atmos"

    public Theater() {
    }

    public Theater(String name, String address, int totalSeats) {
        this.name = name;
        this.address = address;
        this.totalSeats = totalSeats;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public List<String> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<String> facilities) {
        this.facilities = facilities;
    }
}
