package com.example.finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText etUsername, etEmail, etPassword;
    Button btnRegister;
    TextView tvLogin;
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etRegUsername);
        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvBackToLogin);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1. Buat Akun di Authentication
            auth.createUserWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(authResult -> {
                        // 2. Simpan Data Awal User ke Firestore
                        saveUserToFirestore(user, email);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Register Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        tvLogin.setOnClickListener(v -> finish());
    }

    private void saveUserToFirestore(String username, String email) {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();

            // Data Awal untuk Gamification
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", username);
            userData.put("email", email);
            userData.put("currentPoints", 0);      // Poin awal 0
            userData.put("level", 1);              // Level awal 1
            userData.put("levelName", "Novice");   // Title awal
            userData.put("photoUrl", "");          // Foto kosong dulu

            db.collection("users").document(uid)
                    .set(userData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finishAffinity(); // Hapus history agar tidak bisa back ke login
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}