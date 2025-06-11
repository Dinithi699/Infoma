package com.example.myapplication2;

public class NewsItem {
    private String title;
    private String description;
    private String category;
    private String timeAgo;
    private int imageResource;

    public NewsItem(String title, String description, String category, String timeAgo, int imageResource) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.timeAgo = timeAgo;
        this.imageResource = imageResource;
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
}