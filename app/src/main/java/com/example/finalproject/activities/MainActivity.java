                                                                                                                                                                                                                                                                                                                package com.example.finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    View btnTodo, btnEvent, btnPomodoro, btnScreenTime, btnLeaderboard, btnSplitBill;
    Button btnLogout, btnProfile; // Tambahkan btnProfile
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        // View Binding
        btnTodo = findViewById(R.id.btnTodoList);
        btnEvent = findViewById(R.id.btnEvents);
        btnPomodoro = findViewById(R.id.btnPomodoro);
        btnScreenTime = findViewById(R.id.btnScreenTime);
        btnLeaderboard = findViewById(R.id.btnLeaderboard);
        btnSplitBill = findViewById(R.id.btnSplitBill);

        // Binding Tombol Baru
        btnProfile = findViewById(R.id.btnProfile);
        btnLogout = findViewById(R.id.btnLogout);

        // Feature Navigation Listeners
        btnTodo.setOnClickListener(v -> startActivity(new Intent(this, TaskListActivity.class)));
        btnEvent.setOnClickListener(v -> startActivity(new Intent(this, EventListActivity.class)));
        btnPomodoro.setOnClickListener(v -> startActivity(new Intent(this, PomodoroActivity.class)));
        btnScreenTime.setOnClickListener(v -> startActivity(new Intent(this, ScreenTimeActivity.class)));
        btnLeaderboard.setOnClickListener(v -> startActivity(new Intent(this, LeaderboardActivity.class)));
        btnSplitBill.setOnClickListener(v -> startActivity(new Intent(this, SplitBillActivity.class)));

        // Profile Listener (Baru)
        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        // Logout Listener (Tetap Ada)
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}