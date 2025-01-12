package com.example;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskCategory implements Comparable<TaskCategory> {
    private final String categoryName; // category name
    private int id; // unique id for the category in the database

    // constructor for categories with ID
    public TaskCategory(int id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }

    // constructor for new categories without an ID
    public TaskCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    // getters
    public int getId() {
        return id;
    }

    public void setId(int id) { // set ID after saving to the database
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public String toString() {
        return String.format("\uD83D\uDCCA Category: %s (ID: %d)", categoryName, id);
    }

    // total tasks calculated using TaskStats
    public int getTotalTasks() {
        TaskStats stats = new TaskStats(id); // calculate for this category ID
        return stats.getTotalTasks();
    }

    // average priority calculated using TaskStats
    public double getAveragePriority() {
        TaskStats stats = new TaskStats(id); // calculate for this category ID
        return stats.getAveragePriority();
    }

    @Override
    public int compareTo(TaskCategory other) {
        return Integer.compare(this.getTotalTasks(), other.getTotalTasks());
    }

    // static factory method for creating TaskCategory from database result set
    public static TaskCategory fromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String categoryName = rs.getString("name");
        return new TaskCategory(id, categoryName);
    }
}
