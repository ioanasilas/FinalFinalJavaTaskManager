package com.example;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // test db connection
        try (Connection connection = DatabaseUtil.getConnection()) {
            System.out.println("✅ Connected to the database!");

            // populate the database with tasks if the current count is less than the target
            try {
                int currentTaskCount = getCurrentTaskCount(connection);
                if (currentTaskCount < DatabasePopulator.TOTAL_TASKS) {
                    DatabasePopulator.populateDatabase(connection);
                    System.out.println("✅ Database populated with tasks to reach " + DatabasePopulator.TOTAL_TASKS + " total.");
                } else {
                    System.out.println("ℹ️ Database already contains " + currentTaskCount + " tasks. No action taken.");
                }
            } catch (Exception e) {
                System.out.println("❌ Failed to populate database: " + e.getMessage());
            }

            // start app
            TaskCategoryDAO categoryDAO = new TaskCategoryDAO();
            TaskCategory[] taskCategories;

            try {
                initializeCategories(categoryDAO);
                taskCategories = categoryDAO.getAllCategories().toArray(new TaskCategory[0]);
            } catch (SQLException e) {
                System.out.println("❌ Failed to initialize categories: " + e.getMessage());
                return;
            }

            Menu menu = new Menu(taskCategories);
            menu.start();
        } catch (SQLException e) {
            System.out.println("❌ Failed to connect to the database: " + e.getMessage());
        }
    }

    /**
     * Retrieves the current number of tasks in the database.
     *
     * @param connection The database connection.
     * @return The number of tasks in the database.
     * @throws SQLException If a database error occurs.
     */
    private static int getCurrentTaskCount(Connection connection) throws SQLException {
        String query = "SELECT COUNT(*) FROM Task";
        try (var stmt = connection.prepareStatement(query);
             var rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Initializes predefined categories in the database if they don't already exist.
     *
     * @param categoryDAO The DAO for managing task categories.
     * @throws SQLException If a database error occurs.
     */
    private static void initializeCategories(TaskCategoryDAO categoryDAO) throws SQLException {
        String[] predefinedCategories = {
                "UniStuff", "Health", "Personal", "Work", "Hobbies",
                "Family", "Finance", "Travel", "Misc"
        };

        for (String categoryName : predefinedCategories) {
            if (categoryDAO.getCategoryByName(categoryName) == null) {
                categoryDAO.addCategory(categoryName);
                System.out.println("✅ Added category: " + categoryName);
            }
        }
    }
}
