package com.example.finalproject.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.models.TaskModel;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private ArrayList<TaskModel> taskList;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onEdit(TaskModel task);
        void onDelete(TaskModel task);
        void onChecked(TaskModel task, boolean isChecked);
    }

    public TaskAdapter(Context context, ArrayList<TaskModel> taskList, OnTaskClickListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskModel task = taskList.get(position);

        holder.taskName.setText(task.getTitle());

        // Tampilkan Due Date
        if (task.getDueDate() != null && !task.getDueDate().isEmpty()) {
            holder.dueDate.setText("Due: " + task.getDueDate());
        } else {
            holder.dueDate.setText("No Date");
        }

        // Tampilkan Priority
        if (task.getPriority() != null) {
            holder.priority.setText(task.getPriority());
            // Ubah warna badge berdasarkan priority (Opsional)
            if (task.getPriority().equalsIgnoreCase("High")) {
                holder.priorityBadge.setCardBackgroundColor(Color.parseColor("#E05435")); // Merah
            } else {
                holder.priorityBadge.setCardBackgroundColor(Color.parseColor("#2AB3A3")); // Hijau
            }
        }

        // Tampilkan Points
        holder.points.setText(String.valueOf(task.getPoints()));

        // Status Selesai
        holder.taskDone.setOnCheckedChangeListener(null); // Hindari trigger saat scroll
        holder.taskDone.setChecked(task.getIsDone() == 1);

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(task);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(task);
        });

        holder.taskDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) listener.onChecked(task, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskName, dueDate, priority, points;
        CheckBox taskDone;
        Button btnEdit, btnDelete;
        CardView priorityBadge;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.textViewTaskName);
            dueDate = itemView.findViewById(R.id.textViewDueDate);
            priority = itemView.findViewById(R.id.textViewPriority);
            points = itemView.findViewById(R.id.textViewPoints);
            taskDone = itemView.findViewById(R.id.checkBoxIsDone);
            btnEdit = itemView.findViewById(R.id.btnEditTask);
            btnDelete = itemView.findViewById(R.id.btnDeleteTask);
            priorityBadge = itemView.findViewById(R.id.cardPriorityBadge);
        }
    }
}