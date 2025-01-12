package com.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    // add a task to the database
    public void addTask(Task task, int categoryId) throws SQLException {
        String query = "INSERT INTO Task (title, description, priority, deadline, category_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setInt(3, task.getPriority());
            stmt.setDate(4, Date.valueOf(task.getDeadline()));
            stmt.setInt(5, categoryId);
            stmt.executeUpdate();
        }
    }

    // retrieve tasks by category id from the database
    public List<Task> getTasksByCategory(int categoryId) throws SQLException {
        String query = "SELECT * FROM Task WHERE category_id = ?";
        List<Task> tasks = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Task task = Task.fromResultSet(rs); // reuse Task's fromResultSet method
                tasks.add(task);
            }
        }
        return tasks;
    }

    // retrieve all tasks from the database
    public List<Task> getAllTasks() throws SQLException {
        String query = "SELECT * FROM Task";
        List<Task> tasks = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Task task = Task.fromResultSet(rs); // reuse Task's fromResultSet method
                tasks.add(task);
            }
        }
        return tasks;
    }

    // delete a task by id from the database
    public boolean deleteTaskById(int taskId) throws SQLException {
        String query = "DELETE FROM Task WHERE id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, taskId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }


}