package com.example.finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.adapters.TaskAdapter;
import com.example.finalproject.models.TaskModel;
import com.example.finalproject.utils.GamificationManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class TaskListActivity extends AppCompatActivity {

    RecyclerView rvTasks;
    FloatingActionButton btnAdd;

    // Chip Filters
    Chip chipAll, chipActive, chipCompleted, chipHigh;

    FirebaseFirestore db;
    FirebaseAuth auth;

    ArrayList<TaskModel> fullList;
    ArrayList<TaskModel> adapterList;

    TaskAdapter adapter;
    String currentFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        // Binding Views
        rvTasks = findViewById(R.id.recyclerViewTasks);
        btnAdd = findViewById(R.id.fabAddTask);

        chipAll = findViewById(R.id.chipAll);
        chipActive = findViewById(R.id.chipActive);
        chipCompleted = findViewById(R.id.chipCompleted);
        chipHigh = findViewById(R.id.chipHighPriority);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        fullList = new ArrayList<>();
        adapterList = new ArrayList<>();

        // Setup Adapter
        adapter = new TaskAdapter(this, adapterList, new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onEdit(TaskModel task) {
                Intent intent = new Intent(TaskListActivity.this, AddTaskActivity.class);
                intent.putExtra("taskId", task.getId());
                intent.putExtra("title", task.getTitle());
                intent.putExtra("points", task.getPoints());
                intent.putExtra("dueDate", task.getDueDate());
                intent.putExtra("priority", task.getPriority());
                startActivity(intent);
            }

            @Override
            public void onDelete(TaskModel task) {
                // ANTI-CHEAT: Jika menghapus task yang SUDAH selesai, poin harus ditarik kembali
                if (task.getIsDone() == 1) {
                    int points = task.getPoints() > 0 ? task.getPoints() : 50;
                    GamificationManager.addPoints(-points); // Tarik poin (Negatif)
                    Toast.makeText(TaskListActivity.this, "Task Deleted (Points Reverted)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TaskListActivity.this, "Task Deleted", Toast.LENGTH_SHORT).show();
                }

                // Hapus dari database
                db.collection("tasks").document(task.getId()).delete();
            }

            @Override
            public void onChecked(TaskModel task, boolean isChecked) {
                // Update status di Firebase
                db.collection("tasks").document(task.getId())
                        .update("isDone", isChecked ? 1 : 0);

                // Update data lokal sementara agar UI responsif (terutama filter)
                // (Catatan: Idealnya TaskModel punya setter setIsDone, tapi kita akali lewat update list nanti)

                // LOGIKA POIN BARU (Fair Play)
                int points = task.getPoints() > 0 ? task.getPoints() : 50;

                if (isChecked) {
                    // Jika Dicentang -> TAMBAH Poin
                    GamificationManager.addPoints(points);
                    Toast.makeText(TaskListActivity.this, "Completed! +" + points + " pts", Toast.LENGTH_SHORT).show();
                } else {
                    // Jika Centang Dilepas -> KURANGI Poin
                    GamificationManager.addPoints(-points);
                    Toast.makeText(TaskListActivity.this, "Unchecked! -" + points + " pts", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(adapter);

        // Listener Filter
        chipAll.setOnClickListener(v -> applyFilter("All"));
        chipActive.setOnClickListener(v -> applyFilter("Active"));
        chipCompleted.setOnClickListener(v -> applyFilter("Completed"));
        chipHigh.setOnClickListener(v -> applyFilter("High"));

        listenToFirebase();

        btnAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddTaskActivity.class)));
    }

    private void listenToFirebase() {
        if (auth.getCurrentUser() == null) return;
        String userId = auth.getCurrentUser().getUid();

        db.collection("tasks")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    fullList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            TaskModel task = doc.toObject(TaskModel.class);
                            task.setId(doc.getId());
                            fullList.add(task);
                        }
                        applyFilter(currentFilter);
                    }
                });
    }

    private void applyFilter(String filterType) {
        currentFilter = filterType;
        adapterList.clear();

        chipAll.setChecked(filterType.equals("All"));
        chipActive.setChecked(filterType.equals("Active"));
        chipCompleted.setChecked(filterType.equals("Completed"));
        chipHigh.setChecked(filterType.equals("High"));

        for (TaskModel task : fullList) {
            if (filterType.equals("All")) {
                adapterList.add(task);
            }
            else if (filterType.equals("Active")) {
                if (task.getIsDone() == 0) adapterList.add(task);
            }
            else if (filterType.equals("Completed")) {
                if (task.getIsDone() == 1) adapterList.add(task);
            }
            else if (filterType.equals("High")) {
                if ("High".equalsIgnoreCase(task.getPriority())) adapterList.add(task);
            }
        }
        adapter.notifyDataSetChanged();
    }
}