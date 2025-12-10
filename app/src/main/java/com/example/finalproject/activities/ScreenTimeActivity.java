package com.example.finalproject.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.example.finalproject.utils.GamificationManager;

public class ScreenTimeActivity extends AppCompatActivity {

    Chronometer chronometer;
    Button btnStart, btnStop;

    boolean isRunning = false;
    long pauseOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_time);

        chronometer = findViewById(R.id.chronometer);
        btnStart = findViewById(R.id.btnStartScreen);
        btnStop = findViewById(R.id.btnStopScreen);

        // Initial State
        btnStop.setEnabled(false);

        btnStart.setOnClickListener(v -> startFocusSession());
        btnStop.setOnClickListener(v -> stopFocusSession(true));
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("focus_prefs", MODE_PRIVATE);
        boolean violation = prefs.getBoolean("strictViolation", false);
        boolean wasRunning = prefs.getBoolean("timerRunning", false);
        long savedBase = prefs.getLong("chronometerBase", 0);

        if (violation) {
            // HUKUMAN: Jika user kabur saat layar menyala
            new AlertDialog.Builder(this)
                    .setTitle("Session Failed! ❌")
                    .setMessage("You left the app while the timer was running. No points or time were saved.")
                    .setPositiveButton("I promise to focus", (dialog, which) -> resetTimer())
                    .setCancelable(false)
                    .show();

            // Reset violation flag
            prefs.edit().putBoolean("strictViolation", false).apply();
        } else if (wasRunning) {
            // AMAN: User kembali dari layar mati (atau tidak melanggar)
            // Lanjutkan timer dari waktu yang sudah berjalan
            // SystemClock terus berjalan saat layar mati, jadi hitungannya otomatis akurat
            chronometer.setBase(savedBase);
            chronometer.start();
            isRunning = true;
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // LOGIKA PENTING: Cek apakah layar mati atau user keluar app
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive(); // true jika layar menyala

        SharedPreferences prefs = getSharedPreferences("focus_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (isRunning) {
            if (isScreenOn) {
                // KASUS 1: Layar NYALA tapi onStop dipanggil
                // Artinya user tekan HOME/BACK atau pindah aplikasi -> CURANG!
                editor.putBoolean("strictViolation", true);

                // Matikan sesi
                isRunning = false;
                chronometer.stop();
            } else {
                // KASUS 2: Layar MATI (User tekan tombol Power)
                // Artinya user mau fokus -> AMAN!
                // Biarkan isRunning = true agar pas balik (onStart) timer lanjut

                // Kita stop visualnya saja biar hemat resource,
                // tapi base time-nya tetap kita simpan.
                chronometer.stop();
            }
        }

        // Simpan data state terakhir
        editor.putBoolean("timerRunning", isRunning);
        editor.putLong("chronometerBase", chronometer.getBase());
        editor.apply();
    }

    private void startFocusSession() {
        if (!isRunning) {
            // Set waktu mulai ke "Sekarang"
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            isRunning = true;

            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            Toast.makeText(this, "Focus Mode ON! You can turn off screen, but don't exit app!", Toast.LENGTH_LONG).show();
        }
    }

    private void stopFocusSession(boolean isSuccess) {
        if (isRunning) {
            chronometer.stop();
            // Hitung durasi dalam menit
            long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
            long elapsedMinutes = elapsedMillis / 60000;

            if (isSuccess) {
                // Minimal 1 menit baru dihitung
                if (elapsedMinutes >= 1) {
                    // 1. Simpan Total Waktu ke Leaderboard (Firestore)
                    GamificationManager.addFocusTime(elapsedMinutes);

                    // 2. Beri Poin (2 poin per menit)
                    int pointsEarned = (int) (elapsedMinutes * 2);
                    GamificationManager.addPoints(pointsEarned);

                    Toast.makeText(this, "Great job! " + elapsedMinutes + " mins recorded.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Session too short to record (< 1 min).", Toast.LENGTH_SHORT).show();
                }
            }

            resetTimer();
        }
    }

    private void resetTimer() {
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
        isRunning = false;

        btnStart.setEnabled(true);
        btnStop.setEnabled(false);

        // Bersihkan SharedPreferences
        SharedPreferences prefs = getSharedPreferences("focus_prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}