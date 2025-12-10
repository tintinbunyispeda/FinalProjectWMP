package com.example.finalproject.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {

    private EditText editTextEventName, editTextEventDate, editTextNotes;
    private Button btnSaveEvent;

    // Changed from DBHelper to Firebase
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        editTextEventName = findViewById(R.id.editTextEventName);
        editTextEventDate = findViewById(R.id.editTextEventDate);
        editTextNotes = findViewById(R.id.editTextNotes);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);

        editTextEventDate.setOnClickListener(v -> showDatePicker());

        btnSaveEvent.setOnClickListener(v -> saveEvent());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, y, m, d) -> {
            String date = d + "/" + (m + 1) + "/" + y;
            editTextEventDate.setText(date);
        }, year, month, day).show();
    }

    private void saveEvent() {
        String name = editTextEventName.getText().toString().trim();
        String date = editTextEventDate.getText().toString().trim();
        String notes = editTextNotes.getText().toString().trim();

        if (name.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (auth.getCurrentUser() == null) return;
        String userId = auth.getCurrentUser().getUid();

        // Create Event Map
        Map<String, Object> event = new HashMap<>();
        event.put("userId", userId); // User Specific
        event.put("eventName", name);
        event.put("eventDate", date);
        event.put("notes", notes);

        // Save to Firestore "events" collection
        db.collection("events")
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Event Saved to Cloud!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}