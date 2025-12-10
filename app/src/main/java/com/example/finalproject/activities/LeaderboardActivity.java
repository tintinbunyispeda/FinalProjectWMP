package com.example.finalproject.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.example.finalproject.R;
import com.example.finalproject.adapters.LeaderboardAdapter;
import com.example.finalproject.models.LeaderboardModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LeaderboardActivity extends AppCompatActivity {

    RecyclerView rvLeaderboard;
    LinearLayout emptyState;
    FirebaseFirestore db;
    LeaderboardAdapter adapter;

    // Buttons
    Button btnPoints, btnTime;

    // Top 3 Views
    TextView tvName1, tvScore1, tvName2, tvScore2, tvName3, tvScore3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        // Binding Views
        rvLeaderboard = findViewById(R.id.recyclerViewLeaderboard);
        emptyState = findViewById(R.id.emptyStateLeaderboard);
        btnPoints = findViewById(R.id.btnFilterPoints);
        btnTime = findViewById(R.id.btnFilterTime);

        tvName1 = findViewById(R.id.textViewFirstName);
        tvScore1 = findViewById(R.id.textViewFirstPoints);
        tvName2 = findViewById(R.id.textViewSecondName);
        tvScore2 = findViewById(R.id.textViewSecondPoints);
        tvName3 = findViewById(R.id.textViewThirdName);
        tvScore3 = findViewById(R.id.textViewThirdPoints);

        db = FirebaseFirestore.getInstance();

        adapter = new LeaderboardAdapter(this, new ArrayList<>());
        rvLeaderboard.setLayoutManager(new LinearLayoutManager(this));
        rvLeaderboard.setAdapter(adapter);

        // Default Load: Points
        loadLeaderboardData("currentPoints", "pts");

        // Button Listeners
        btnPoints.setOnClickListener(v -> {
            updateButtonStyles(true);
            loadLeaderboardData("currentPoints", "pts");
        });

        btnTime.setOnClickListener(v -> {
            updateButtonStyles(false);
            loadLeaderboardData("totalFocusMinutes", "mins");
        });
    }

    private void updateButtonStyles(boolean isPointsActive) {
        if (isPointsActive) {
            btnPoints.setBackgroundColor(Color.parseColor("#FF6B4A")); // Tomato
            btnTime.setBackgroundColor(Color.parseColor("#666666"));   // Grey
        } else {
            btnPoints.setBackgroundColor(Color.parseColor("#666666")); // Grey
            btnTime.setBackgroundColor(Color.parseColor("#FF6B4A"));   // Tomato
        }
    }

    private void loadLeaderboardData(String fieldName, String unit) {
        // Update Adapter Unit
        adapter.setUnit(unit);

        db.collection("users")
                .orderBy(fieldName, Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<LeaderboardModel> list = new ArrayList<>();
                    int rank = 1;

                    // Reset Podium text first
                    resetPodium();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("username");
                        Long scoreVal = doc.getLong(fieldName);
                        Long levelVal = doc.getLong("level");
                        String photo = doc.getString("photoUrl");

                        int score = (scoreVal != null) ? scoreVal.intValue() : 0;
                        int level = (levelVal != null) ? levelVal.intValue() : 1;

                        if (rank == 1) {
                            updatePodium(tvName1, tvScore1, name, score, unit);
                        } else if (rank == 2) {
                            updatePodium(tvName2, tvScore2, name, score, unit);
                        } else if (rank == 3) {
                            updatePodium(tvName3, tvScore3, name, score, unit);
                        } else {
                            list.add(new LeaderboardModel(rank, name, score, photo, level));
                        }
                        rank++;
                    }

                    adapter.setData(list);

                    if (queryDocumentSnapshots.isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                        rvLeaderboard.setVisibility(View.GONE);
                    } else {
                        emptyState.setVisibility(View.GONE);
                        rvLeaderboard.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void resetPodium() {
        tvName1.setText("-"); tvScore1.setText("0");
        tvName2.setText("-"); tvScore2.setText("0");
        tvName3.setText("-"); tvScore3.setText("0");
    }

    private void updatePodium(TextView tvName, TextView tvScore, String name, int score, String unit) {
        tvName.setText(name);
        tvScore.setText(score + " " + unit);
    }
}