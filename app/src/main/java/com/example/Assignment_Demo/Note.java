package com.example.Assignment_Demo;

public class Note {
    private long id;
    private String content;
    private boolean isComplete;
    private String date;
    private int userId;

    // Constructor
    public Note(long id, String content, boolean isComplete, String date, int userId) {
        this.id = id;
        this.content = content;
        this.isComplete = isComplete;
        this.date = date;
        this.userId = userId;
    }

    // --- Getters ---
    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public String getDate() {
        return date;
    }

    public int getUserId() {
        return userId;
    }

    // --- Setters ---
    public void setContent(String content) {
        this.content = content;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }
}