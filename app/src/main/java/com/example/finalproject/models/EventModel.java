package com.example.finalproject.models;

public class EventModel {
    private String id; // Changed from int to String for Firebase
    private String eventName;
    private String eventDate;
    private String notes;

    // Empty constructor for Firebase
    public EventModel() {}

    public EventModel(String id, String eventName, String eventDate, String notes) {
        this.id = id;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.notes = notes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventName() { return eventName; }
    public String getEventDate() { return eventDate; }
    public String getNotes() { return notes; }
}