package com.example.finalproject.activities;

import android.content.Intent;
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
import com.example.finalproject.models.EventModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class EventListActivity extends AppCompatActivity {

    RecyclerView rvEvents;
    FloatingActionButton btnAddEvent;
    TextView tvCount;
    LinearLayout emptyState;

    // Changed to Firebase
    FirebaseFirestore db;
    FirebaseAuth auth;

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

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        list = new ArrayList<>();

        adapter = new EventAdapter(this, list, new EventAdapter.OnEventClickListener() {
            @Override
            public void onEdit(EventModel event) {
                Toast.makeText(EventListActivity.this, "Edit coming soon", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDelete(EventModel event) {
                // Delete from Firestore
                db.collection("events").document(event.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> Toast.makeText(EventListActivity.this, "Event deleted", Toast.LENGTH_SHORT).show());
            }
        });

        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.setAdapter(adapter);

        listenToEvents();

        if (btnAddEvent != null) {
            btnAddEvent.setOnClickListener(v ->
                    startActivity(new Intent(this, AddEventActivity.class)));
        }
    }

    private void listenToEvents() {
        if (auth.getCurrentUser() == null) return;
        String userId = auth.getCurrentUser().getUid();

        // Listen for real-time updates from Firestore filtered by User ID
        db.collection("events")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    list.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            EventModel event = doc.toObject(EventModel.class);
                            event.setId(doc.getId()); // Set String ID
                            list.add(event);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    updateUI();
                });
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