package com.example.finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    ImageView ivProfile;
    TextView tvUsername, tvEmail, tvLevelName, tvLevel, tvPoints;
    Button btnLogout;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase (Tanpa Storage)
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind Views
        ivProfile = findViewById(R.id.ivProfilePicture);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvLevelName = findViewById(R.id.tvLevelName);
        tvLevel = findViewById(R.id.tvLevel);
        tvPoints = findViewById(R.id.tvPoints);
        btnLogout = findViewById(R.id.btnProfileLogout);

        // Set gambar default secara manual (opsional, karena di XML sudah ada)
        ivProfile.setImageResource(R.mipmap.ic_launcher_round);

        loadUserData();

        // Logout Button
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserData() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        db.collection("users").document(user.getUid())
                .addSnapshotListener((document, error) -> {
                    if (error != null) return;
                    if (document != null && document.exists()) {
                        String name = document.getString("username");
                        String email = document.getString("email");
                        Long points = document.getLong("currentPoints");
                        Long level = document.getLong("level");
                        String levelName = document.getString("levelName");

                        // Update UI
                        tvUsername.setText(name);
                        tvEmail.setText(email);
                        tvPoints.setText(String.valueOf(points));
                        tvLevel.setText("Lv. " + level);
                        tvLevelName.setText(levelName);
                    }
                });
    }
}