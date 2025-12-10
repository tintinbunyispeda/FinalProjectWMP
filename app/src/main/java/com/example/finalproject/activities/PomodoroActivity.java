package com.example.finalproject.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.example.finalproject.utils.GamificationManager;

public class PomodoroActivity extends AppCompatActivity {

    TextView tvTimer;
    Button btnStart, btnReset;

    CountDownTimer timer;
    long startTimeInMillis = 1500000; // 25 menit default
    long timeLeftInMillis = 1500000;
    long endTime;
    boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);

        tvTimer = findViewById(R.id.textViewTimer);
        btnStart = findViewById(R.id.btnStartPomodoro);
        btnReset = findViewById(R.id.btnResetPomodoro);

        // HAPUS inisialisasi editTextUsername/Password jika ada

        btnStart.setOnClickListener(v -> {
            if (!isRunning) {
                startTimer();
            } else {
                pauseTimer();
            }
        });

        btnReset.setOnClickListener(v -> resetTimer());
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("pomodoro_prefs", MODE_PRIVATE);

        startTimeInMillis = prefs.getLong("startTimeInMillis", 1500000);
        timeLeftInMillis = prefs.getLong("millisLeft", startTimeInMillis);
        isRunning = prefs.getBoolean("timerRunning", false);

        updateCountDownText();

        if (isRunning) {
            endTime = prefs.getLong("endTime", 0);
            timeLeftInMillis = endTime - System.currentTimeMillis();

            if (timeLeftInMillis < 0) {
                timeLeftInMillis = 0;
                isRunning = false;
                updateCountDownText();
                finishPomodoro();
            } else {
                startTimer();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("pomodoro_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("startTimeInMillis", startTimeInMillis);
        editor.putLong("millisLeft", timeLeftInMillis);
        editor.putBoolean("timerRunning", isRunning);
        editor.putLong("endTime", endTime);

        editor.apply();

        if (timer != null) {
            timer.cancel();
        }
    }

    private void startTimer() {
        endTime = System.currentTimeMillis() + timeLeftInMillis;

        timer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                isRunning = false;
                finishPomodoro();
            }
        }.start();

        isRunning = true;
        btnStart.setText("Pause");
    }

    private void pauseTimer() {
        if (timer != null) timer.cancel();
        isRunning = false;
        btnStart.setText("Resume");
    }

    private void resetTimer() {
        if (timer != null) timer.cancel();
        isRunning = false;
        timeLeftInMillis = 1500000;
        updateCountDownText();
        btnStart.setText("Start");

        SharedPreferences prefs = getSharedPreferences("pomodoro_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        tvTimer.setText(timeLeftFormatted);
    }

    private void finishPomodoro() {
        tvTimer.setText("00:00");
        btnStart.setText("Start");

        // Panggil GamificationManager untuk nambah poin ke user yang sedang login
        GamificationManager.addPoints(100);
        Toast.makeText(this, "Session Done! +100 Points added to your profile!", Toast.LENGTH_LONG).show();

        SharedPreferences prefs = getSharedPreferences("pomodoro_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("timerRunning", false);
        editor.apply();
    }
}