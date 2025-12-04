package com.example.finalproject.models;

public class TaskModel {
    private String id;
    private String title;
    private String description;
    private String dueDate;
    private String priority;
    private int points;
    private int isDone;

    // Konstruktor kosong untuk Firebase (WAJIB)
    public TaskModel() {}

    public TaskModel(String id, String title, String description, String dueDate, String priority, int points, int isDone) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.points = points;
        this.isDone = isDone;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDueDate() { return dueDate; }
    public String getPriority() { return priority; }
    public int getPoints() { return points; }
    public int getIsDone() { return isDone; }
}