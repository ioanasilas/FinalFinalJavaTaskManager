package com.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskCategoryDAO {

    // add a new category to the database
    public TaskCategory addCategory(String categoryName) throws SQLException {
        String query = "INSERT INTO Category (name) VALUES (?)";
        try (Connection connection = DatabaseUtil.getConnection();
             // return auto generated key, autoincremented id
             PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, categoryName);
            stmt.executeUpdate();
            // resultset is data got from db query
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new TaskCategory(id, categoryName); // return a new immutable object
                }
            }
        }
        throw new SQLException("Failed to insert category into the database.");
    }

    // retrieve all categories from the database
    public List<TaskCategory> getAllCategories() throws SQLException {
        String query = "SELECT * FROM Category";
        List<TaskCategory> categories = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                categories.add(TaskCategory.fromResultSet(rs));
            }
        }
        return categories;
    }

    // retrieve a category by name from the database
    public TaskCategory getCategoryByName(String categoryName) throws SQLException {
        String query = "SELECT * FROM Category WHERE name = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, categoryName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return TaskCategory.fromResultSet(rs);
                }
            }
        }
        return null; // return null if no category is found
    }
}
