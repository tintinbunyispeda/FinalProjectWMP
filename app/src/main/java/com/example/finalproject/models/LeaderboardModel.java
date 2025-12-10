package com.example.finalproject.models;

public class LeaderboardModel {
    private int rank;
    private String username;
    private int score;
    private String photoUrl; // Tetap ada biar tidak error di Adapter
    private int level;       // Tambahan: Level

    public LeaderboardModel(int rank, String username, int score, String photoUrl, int level) {
        this.rank = rank;
        this.username = username;
        this.score = score;
        this.photoUrl = photoUrl;
        this.level = level;
    }

    public int getRank() { return rank; }
    public String getUsername() { return username; }
    public int getScore() { return score; }
    public String getPhotoUrl() { return photoUrl; }
    public int getLevel() { return level; }
}