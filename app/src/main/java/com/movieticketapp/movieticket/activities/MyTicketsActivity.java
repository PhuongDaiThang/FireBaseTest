package com.movieticketapp.movieticket.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.movieticketapp.movieticket.R;
import com.movieticketapp.movieticket.adapters.TicketAdapter;
import com.movieticketapp.movieticket.firebase.FirebaseHelper;
import com.movieticketapp.movieticket.models.Ticket;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyTicketsActivity extends AppCompatActivity {

    private RecyclerView rvTickets;
    private TextView tvEmpty;
    private TicketAdapter adapter;
    private List<Ticket> ticketList = new ArrayList<>();
    private ListenerRegistration ticketListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Vé của tôi");
        }

        rvTickets = findViewById(R.id.rvTickets);
        tvEmpty = findViewById(R.id.tvEmpty);

        rvTickets.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TicketAdapter(ticketList);
        rvTickets.setAdapter(adapter);

        loadTickets();
    }

    private void loadTickets() {
        String userId = FirebaseHelper.getInstance().getCurrentUser().getUid();

        ticketListener = FirebaseHelper.getInstance().listenUserTickets(userId, (snapshots, error) -> {
            if (error != null || snapshots == null)
                return;

            ticketList.clear();
            for (QueryDocumentSnapshot doc : snapshots) {
                Ticket ticket = doc.toObject(Ticket.class);
                ticket.setId(doc.getId());
                ticketList.add(ticket);
            }
            adapter.notifyDataSetChanged();

            tvEmpty.setVisibility(ticketList.isEmpty() ? View.VISIBLE : View.GONE);
            rvTickets.setVisibility(ticketList.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ticketListener != null)
            ticketListener.remove();
    }
}
