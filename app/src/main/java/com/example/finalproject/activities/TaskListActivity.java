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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class TaskListActivity extends AppCompatActivity {

    RecyclerView rvTasks;
    FloatingActionButton btnAdd;
    FirebaseFirestore db;
    ArrayList<TaskModel> list;
    TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        rvTasks = findViewById(R.id.recyclerViewTasks);
        btnAdd = findViewById(R.id.fabAddTask);

        db = FirebaseFirestore.getInstance();
        list = new ArrayList<>();

        // Implementasi Listener untuk Edit dan Delete
        adapter = new TaskAdapter(this, list, new TaskAdapter.OnTaskClickListener() {
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
                db.collection("tasks").document(task.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> Toast.makeText(TaskListActivity.this, "Task Deleted", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(TaskListActivity.this, "Error deleting", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onChecked(TaskModel task, boolean isChecked) {
                // Opsional: Update status selesai ke Firebase
                db.collection("tasks").document(task.getId())
                        .update("isDone", isChecked ? 1 : 0);
            }
        });

        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(adapter);

        listenToFirebase();

        btnAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddTaskActivity.class)));
    }

    private void listenToFirebase() {
        db.collection("tasks")
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    list.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            TaskModel task = doc.toObject(TaskModel.class);
                            task.setId(doc.getId());
                            list.add(task);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}