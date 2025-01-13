package com.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    // Add a task to the database with user ID
    public void addTask(Task task, int categoryId, int userId) throws SQLException {
        String query = "INSERT INTO Task (title, description, priority, deadline, category_id, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setInt(3, task.getPriority());
            stmt.setDate(4, Date.valueOf(task.getDeadline()));
            stmt.setInt(5, categoryId);
            stmt.setInt(6, userId);
            stmt.executeUpdate();
        }
    }

    // Retrieve tasks by category ID
    public List<Task> getTasksByCategory(int categoryId) throws SQLException {
        String query = "SELECT * FROM Task WHERE category_id = ?";
        List<Task> tasks = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Task task = Task.fromResultSet(rs);
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    // Retrieve tasks by user ID
    public List<Task> getTasksByUser(int userId) throws SQLException {
        String query = "SELECT * FROM Task WHERE user_id = ?";
        List<Task> tasks = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Task task = Task.fromResultSet(rs);
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    // Retrieve all tasks from the database
    public List<Task> getAllTasks() throws SQLException {
        String query = "SELECT * FROM Task";
        List<Task> tasks = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Task task = Task.fromResultSet(rs);
                tasks.add(task);
            }
        }
        return tasks;
    }

    // Delete a task by ID
    public boolean deleteTaskById(int taskId) throws SQLException {
        String query = "DELETE FROM Task WHERE id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, taskId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public List<Task> getTasksByUserAndCategory(int userId, int categoryId) throws SQLException {
        String query = "select * from Task where user_id = ? and category_id = ?";
        List<Task> tasks = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(Task.fromResultSet(rs));
                }
            }
        }
        return tasks;
    }
}

