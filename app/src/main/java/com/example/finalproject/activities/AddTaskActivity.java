package com.example.finalproject.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {

    EditText etTitle, etDueDate;
    Spinner spinnerPriority;
    Button btnSave;

    FirebaseFirestore db;
    FirebaseAuth auth;

    String taskId = null;
    boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        etTitle = findViewById(R.id.editTextTaskName);
        etDueDate = findViewById(R.id.editTextDueDate);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        btnSave = findViewById(R.id.btnSaveTask);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Setup Spinner Adapter
        // Pastikan array "priority_levels" ada di strings.xml (Low, Medium, High)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);

        etDueDate.setOnClickListener(v -> showDatePicker());

        // Check for Edit Mode
        if (getIntent().hasExtra("taskId")) {
            isEditMode = true;
            taskId = getIntent().getStringExtra("taskId");
            etTitle.setText(getIntent().getStringExtra("title"));
            etDueDate.setText(getIntent().getStringExtra("dueDate"));

            // Set Spinner Selection berdasarkan data lama
            String currentPriority = getIntent().getStringExtra("priority");
            if (currentPriority != null) {
                int spinnerPosition = adapter.getPosition(currentPriority);
                spinnerPriority.setSelection(spinnerPosition);
            }

            btnSave.setText("Update Task");
        }

        btnSave.setOnClickListener(v -> saveTask());
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year1, month1, dayOfMonth) ->
                etDueDate.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1),
                year, month, day).show();
    }

    private void saveTask() {
        String title = etTitle.getText().toString();
        String dueDate = etDueDate.getText().toString();
        String priority = spinnerPriority.getSelectedItem().toString();

        if (title.isEmpty()) {
            Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "You must be logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        // LOGIKA BARU: Hitung Poin Berdasarkan Prioritas
        int points = 0;
        switch (priority) {
            case "High": points = 100; break;
            case "Medium": points = 50; break;
            case "Low": points = 20; break;
            default: points = 10; break;
        }

        Map<String, Object> task = new HashMap<>();
        task.put("userId", userId);
        task.put("title", title);
        task.put("description", "No Description");
        task.put("points", points); // Poin otomatis masuk
        task.put("dueDate", dueDate);
        task.put("priority", priority);
        task.put("isDone", 0);

        if (isEditMode) {
            db.collection("tasks").document(taskId)
                    .update(task)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Task Updated! Points adjusted.", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show());
        } else {
            db.collection("tasks")
                    .add(task)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Task Added! (" + finalPoints + " pts)", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error adding task", Toast.LENGTH_SHORT).show());
        }
    }

    // Variabel bantu untuk Toast di dalam lambda (karena points harus final/effectively final)
    private int finalPoints = 0;
}