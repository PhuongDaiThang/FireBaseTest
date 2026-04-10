package com.movieticketapp.movieticket.firebase;

import com.movieticketapp.movieticket.models.*;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {

    private static FirebaseHelper instance;
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    private FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public static synchronized FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    // ==================== USERS ====================

    public Task<Void> saveUser(User user) {
        return db.collection("users").document(user.getUid()).set(user);
    }

    public Task<DocumentSnapshot> getUser(String uid) {
        return db.collection("users").document(uid).get();
    }

    // ==================== MOVIES ====================

    public Task<QuerySnapshot> getAllMovies() {
        return db.collection("movies").orderBy("title").get();
    }

    public Task<DocumentSnapshot> getMovie(String movieId) {
        return db.collection("movies").document(movieId).get();
    }

    public ListenerRegistration listenMovies(EventListener<QuerySnapshot> listener) {
        return db.collection("movies").orderBy("title")
                .addSnapshotListener(listener);
    }

    // ==================== THEATERS ====================

    public Task<QuerySnapshot> getAllTheaters() {
        return db.collection("theaters").get();
    }

    // ==================== SHOWTIMES ====================

    public Task<QuerySnapshot> getShowtimesForMovie(String movieId) {
        return db.collection("showtimes")
                .whereEqualTo("movieId", movieId)
                .whereGreaterThan("timestamp", System.currentTimeMillis())
                .orderBy("timestamp")
                .get();
    }

    public Task<DocumentSnapshot> getShowtime(String showtimeId) {
        return db.collection("showtimes").document(showtimeId).get();
    }

    // ==================== TICKETS ====================

    public Task<DocumentReference> bookTicket(Ticket ticket, String showtimeId, List<String> newSeats) {
        // Use transaction to atomically book seats and create ticket
        WriteBatch batch = db.batch();

        // Create ticket document
        DocumentReference ticketRef = db.collection("tickets").document();
        batch.set(ticketRef, ticket);

        // Update showtime booked seats
        DocumentReference showtimeRef = db.collection("showtimes").document(showtimeId);
        batch.update(showtimeRef, "bookedSeats", FieldValue.arrayUnion(newSeats.toArray()));

        return batch.commit().continueWith(task -> ticketRef);
    }

    public Task<QuerySnapshot> getUserTickets(String userId) {
        return db.collection("tickets")
                .whereEqualTo("userId", userId)
                .orderBy("bookedAt", Query.Direction.DESCENDING)
                .get();
    }

    public ListenerRegistration listenUserTickets(String userId, EventListener<QuerySnapshot> listener) {
        return db.collection("tickets")
                .whereEqualTo("userId", userId)
                .orderBy("bookedAt", Query.Direction.DESCENDING)
                .addSnapshotListener(listener);
    }

    public Task<Void> cancelTicket(String ticketId) {
        return db.collection("tickets").document(ticketId)
                .update("status", "cancelled");
    }

    // ==================== SEED DATA (for testing) ====================

    public void seedSampleData() {
        // Check if data already exists
        db.collection("movies").limit(1).get().addOnSuccessListener(snapshot -> {
            if (snapshot.isEmpty()) {
                seedMovies();
                seedTheaters();
            }
        });
    }

    private void seedMovies() {
        List<Map<String, Object>> movies = new ArrayList<>();

        Map<String, Object> m1 = new HashMap<>();
        m1.put("title", "Avengers: Secret Wars");
        m1.put("description",
                "The Avengers face their greatest threat as the multiverse collides in an epic battle for survival.");
        m1.put("posterUrl", "https://via.placeholder.com/300x450?text=Avengers");
        m1.put("genre", "Action, Sci-Fi");
        m1.put("duration", 165);
        m1.put("rating", 8.5);
        m1.put("releaseDate", "2026-04-01");
        movies.add(m1);

        Map<String, Object> m2 = new HashMap<>();
        m2.put("title", "The Batman 2");
        m2.put("description", "Batman returns to Gotham to face a new villain threatening the city.");
        m2.put("posterUrl", "https://via.placeholder.com/300x450?text=Batman");
        m2.put("genre", "Action, Crime");
        m2.put("duration", 150);
        m2.put("rating", 8.2);
        m2.put("releaseDate", "2026-03-15");
        movies.add(m2);

        Map<String, Object> m3 = new HashMap<>();
        m3.put("title", "Interstellar 2");
        m3.put("description", "A new mission into deep space reveals secrets about the nature of time and existence.");
        m3.put("posterUrl", "https://via.placeholder.com/300x450?text=Interstellar");
        m3.put("genre", "Sci-Fi, Drama");
        m3.put("duration", 180);
        m3.put("rating", 9.0);
        m3.put("releaseDate", "2026-05-01");
        movies.add(m3);

        Map<String, Object> m4 = new HashMap<>();
        m4.put("title", "Spider-Man: Beyond");
        m4.put("description", "Peter Parker discovers a new dimension of power and responsibility.");
        m4.put("posterUrl", "https://via.placeholder.com/300x450?text=SpiderMan");
        m4.put("genre", "Action, Adventure");
        m4.put("duration", 140);
        m4.put("rating", 8.0);
        m4.put("releaseDate", "2026-06-20");
        movies.add(m4);

        Map<String, Object> m5 = new HashMap<>();
        m5.put("title", "Frozen 3");
        m5.put("description", "Elsa and Anna embark on a new adventure beyond the enchanted forest.");
        m5.put("posterUrl", "https://via.placeholder.com/300x450?text=Frozen3");
        m5.put("genre", "Animation, Family");
        m5.put("duration", 110);
        m5.put("rating", 7.5);
        m5.put("releaseDate", "2026-07-10");
        movies.add(m5);

        for (Map<String, Object> movie : movies) {
            db.collection("movies").add(movie);
        }
    }

    private void seedTheaters() {
        List<Map<String, Object>> theaters = new ArrayList<>();

        Map<String, Object> t1 = new HashMap<>();
        t1.put("name", "CGV Vincom Center");
        t1.put("address", "72 Lê Thánh Tôn, Q.1, TP.HCM");
        t1.put("totalSeats", 120);
        List<String> f1 = new ArrayList<>();
        f1.add("IMAX");
        f1.add("Dolby Atmos");
        t1.put("facilities", f1);
        theaters.add(t1);

        Map<String, Object> t2 = new HashMap<>();
        t2.put("name", "Lotte Cinema Nowzone");
        t2.put("address", "235 Nguyễn Văn Cừ, Q.1, TP.HCM");
        t2.put("totalSeats", 100);
        List<String> f2 = new ArrayList<>();
        f2.add("4DX");
        t2.put("facilities", f2);
        theaters.add(t2);

        Map<String, Object> t3 = new HashMap<>();
        t3.put("name", "Galaxy Nguyễn Du");
        t3.put("address", "116 Nguyễn Du, Q.1, TP.HCM");
        t3.put("totalSeats", 80);
        List<String> f3 = new ArrayList<>();
        f3.add("Standard");
        t3.put("facilities", f3);
        theaters.add(t3);

        for (Map<String, Object> theater : theaters) {
            db.collection("theaters").add(theater).addOnSuccessListener(ref -> {
                // Seed showtimes for each theater
                seedShowtimes(ref.getId(), (String) theater.get("name"));
            });
        }
    }

    private void seedShowtimes(String theaterId, String theaterName) {
        db.collection("movies").get().addOnSuccessListener(snapshot -> {
            String[] times = { "10:00", "13:30", "17:00", "19:30", "22:00" };
            String[] dates = { "2026-04-15", "2026-04-16", "2026-04-17" };
            double[] prices = { 85000, 95000, 110000, 120000, 100000 };
            int idx = 0;

            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                String movieId = doc.getId();
                String movieTitle = doc.getString("title");

                for (String date : dates) {
                    String time = times[idx % times.length];
                    double price = prices[idx % prices.length];

                    Map<String, Object> showtime = new HashMap<>();
                    showtime.put("movieId", movieId);
                    showtime.put("theaterId", theaterId);
                    showtime.put("theaterName", theaterName);
                    showtime.put("movieTitle", movieTitle);
                    showtime.put("date", date);
                    showtime.put("time", time);
                    showtime.put("timestamp", parseDateTime(date, time));
                    showtime.put("price", price);
                    showtime.put("totalSeats", 80);
                    showtime.put("bookedSeats", new ArrayList<>());

                    db.collection("showtimes").add(showtime);
                    idx++;
                }
            }
        });
    }

    private long parseDateTime(String date, String time) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            java.util.Date d = sdf.parse(date + " " + time);
            return d != null ? d.getTime() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
