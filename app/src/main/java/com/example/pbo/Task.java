package com.example.pbo;

import java.io.Serializable;

public class Task implements Serializable {
    private String id;
    private String title;
    private String category;
    private String startDate;
    private String endDate;
    private String status;
    private String userId; // Tambahkan userId

    // Constructor kosong diperlukan untuk Firestore
    public Task() {
    }

    public Task(String title, String category, String startDate, String endDate, String status, String userId) {
        this.title = title;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.userId = userId;
    }

    // Getter dan Setter untuk semua properti

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
