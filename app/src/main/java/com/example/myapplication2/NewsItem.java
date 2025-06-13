package com.example.myapplication2;

import java.io.Serializable;

public class NewsItem implements Serializable {
    private String title;
    private String description;
    private String category;
    private String timeAgo;
    private int imageResource;
    private boolean isRead;

    public NewsItem(String title, String description, String category, String timeAgo, int imageResource) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.timeAgo = timeAgo;
        this.imageResource = imageResource;
        this.isRead = false; // Default to unread
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public int getImageResource() {
        return imageResource;
    }

    public boolean isRead() {
        return isRead;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public void setRead(boolean read) {
        this.isRead = read;
    }
}