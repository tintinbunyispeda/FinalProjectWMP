package com.example.finalproject.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.adapters.EventAdapter;
import com.example.finalproject.database.DBHelper;
import com.example.finalproject.models.EventModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class EventListActivity extends AppCompatActivity {

    RecyclerView rvEvents;
    FloatingActionButton btnAddEvent;
    TextView tvCount;
    LinearLayout emptyState;

    DBHelper db;
    ArrayList<EventModel> list;
    EventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        rvEvents = findViewById(R.id.recyclerViewEvents);
        btnAddEvent = findViewById(R.id.fabAddEvent);
        tvCount = findViewById(R.id.textViewEventCount);
        emptyState = findViewById(R.id.emptyStateEvents);

        db = new DBHelper(this);
        list = new ArrayList<>();

        // Setup Adapter dengan Listener (Hapus & Edit)
        adapter = new EventAdapter(this, list, new EventAdapter.OnEventClickListener() {
            @Override
            public void onEdit(EventModel event) {
                // Untuk Edit, kita perlu buat logic update di DBHelper nanti
                // Sementara tampilkan Toast dulu
                Toast.makeText(EventListActivity.this, "Edit feature coming soon!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDelete(EventModel event) {
                // Hapus dari Database
                boolean deleted = db.deleteEvent(event.getId());
                if (deleted) {
                    Toast.makeText(EventListActivity.this, "Event deleted", Toast.LENGTH_SHORT).show();
                    loadEvents(); // Refresh list
                } else {
                    Toast.makeText(EventListActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.setAdapter(adapter);

        // Load data awal
        loadEvents();

        if (btnAddEvent != null) {
            btnAddEvent.setOnClickListener(v ->
                    startActivity(new Intent(this, AddEventActivity.class)));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    private void loadEvents() {
        list.clear();
        Cursor c = db.getAllEvents();

        if (c != null) {
            while (c.moveToNext()) {
                // Pastikan urutan kolom sesuai DBHelper (id, eventName, eventDate, notes)
                list.add(new EventModel(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3)
                ));
            }
            c.close(); // Tutup cursor agar hemat memori
        }

        adapter.notifyDataSetChanged();
        updateUI();
    }

    private void updateUI() {
        int count = list.size();
        tvCount.setText(count + " upcoming events");

        if (count == 0) {
            emptyState.setVisibility(View.VISIBLE);
            rvEvents.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            rvEvents.setVisibility(View.VISIBLE);
        }
    }
}