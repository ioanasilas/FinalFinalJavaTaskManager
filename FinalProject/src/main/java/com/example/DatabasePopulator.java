package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Random;

public class DatabasePopulator {
    static final int TOTAL_TASKS = 1_000_000;
    private static final int BATCH_SIZE = 10_000;

    /**
     * Populates the database with tasks if the current count is less than the target.
     *
     * @param connection The database connection to use.
     * @throws SQLException If a database error occurs.
     */
    public static void populateDatabase(Connection connection) throws SQLException {
        int currentTaskCount = getTaskCount(connection);
        if (currentTaskCount >= TOTAL_TASKS) {
            System.out.println("ℹ️ Database already contains " + currentTaskCount + " tasks. No action taken.");
            return;
        }

        int tasksToAdd = TOTAL_TASKS - currentTaskCount;
        System.out.println("ℹ️ Database contains " + currentTaskCount + " tasks. Adding " + tasksToAdd + " more tasks...");

        Random random = new Random();
        String[] categories = {"Work", "Health", "Personal", "UniStuff", "Hobbies"};

        // ensure categories exist in the database, add if not
        TaskCategoryDAO categoryDAO = new TaskCategoryDAO();
        for (String category : categories) {
            if (categoryDAO.getCategoryByName(category) == null) {
                categoryDAO.addCategory(category);
            }
        }

        // get category IDs
        int[] categoryIds = categoryDAO.getAllCategories().stream()
                .mapToInt(TaskCategory::getId)
                .toArray();

        // insert tasks in batches
        String insertTaskSQL = "INSERT INTO Task (title, description, priority, deadline, category_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertTaskSQL)) {
            for (int i = 1; i <= tasksToAdd; i++) {
                String title = "Task" + (currentTaskCount + i);
                String description = "Description for " + title;
                int priority = random.nextInt(101); // random priority 0-100
                LocalDate deadline = LocalDate.now().plusDays(random.nextInt(365)); // random deadline within a year
                int categoryId = categoryIds[random.nextInt(categoryIds.length)]; // random category ID

                stmt.setString(1, title);
                stmt.setString(2, description);
                stmt.setInt(3, priority);
                stmt.setDate(4, java.sql.Date.valueOf(deadline));
                stmt.setInt(5, categoryId);

                stmt.addBatch();

                if (i % BATCH_SIZE == 0) {
                    stmt.executeBatch();
                    System.out.println("Inserted " + (currentTaskCount + i) + " tasks so far...");
                }
            }

            stmt.executeBatch(); // insert remaining tasks
            System.out.println("✅ Successfully added " + tasksToAdd + " tasks to the database.");
        }
    }

    /**
     * Retrieves the current number of tasks in the database.
     *
     * @param connection The database connection to use.
     * @return The number of tasks in the `Task` table.
     * @throws SQLException If a database error occurs.
     */
    private static int getTaskCount(Connection connection) throws SQLException {
        String countSQL = "SELECT COUNT(*) FROM Task";
        try (PreparedStatement stmt = connection.prepareStatement(countSQL);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
