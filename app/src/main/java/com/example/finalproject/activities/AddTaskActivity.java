package com.example.finalproject.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {

    EditText etTitle, etPoints, etDueDate;
    Spinner spinnerPriority;
    Button btnSave;
    FirebaseFirestore db;

    // Variabel untuk Edit Mode
    String taskId = null;
    boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        etTitle = findViewById(R.id.editTextTaskName);
        etPoints = findViewById(R.id.editTextPoints);
        etDueDate = findViewById(R.id.editTextDueDate);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        btnSave = findViewById(R.id.btnSaveTask);

        db = FirebaseFirestore.getInstance();

        // Setup Spinner Priority
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);

        // Setup Date Picker
        etDueDate.setOnClickListener(v -> showDatePicker());

        // Cek apakah ini mode Edit?
        if (getIntent().hasExtra("taskId")) {
            isEditMode = true;
            taskId = getIntent().getStringExtra("taskId");
            etTitle.setText(getIntent().getStringExtra("title"));
            etPoints.setText(String.valueOf(getIntent().getIntExtra("points", 0)));
            etDueDate.setText(getIntent().getStringExtra("dueDate"));
            // Set spinner selection (Sederhana: default ke 0/Low dulu jika complex)
            // Bisa ditingkatkan dengan logic pencocokan string priority
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
        String pointsStr = etPoints.getText().toString();
        String dueDate = etDueDate.getText().toString();
        String priority = spinnerPriority.getSelectedItem().toString();

        if (title.isEmpty()) {
            Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        int points = pointsStr.isEmpty() ? 0 : Integer.parseInt(pointsStr);

        Map<String, Object> task = new HashMap<>();
        task.put("title", title);
        task.put("description", "No Description"); // Default
        task.put("points", points);
        task.put("dueDate", dueDate);
        task.put("priority", priority);
        task.put("isDone", 0);

        if (isEditMode) {
            // Update Data Lama
            db.collection("tasks").document(taskId)
                    .update(task)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Task Updated!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show());
        } else {
            // Buat Data Baru
            db.collection("tasks")
                    .add(task)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Task Added!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error adding task", Toast.LENGTH_SHORT).show());
        }
    }
}