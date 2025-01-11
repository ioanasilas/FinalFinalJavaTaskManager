package com.example;

import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskStatsTest {

    // helper method to initialize categories for TaskManager
    private void initializeCategories() {
        TaskManager.categories = new TaskCategory[] {
                new TaskCategory("UniStuff"),
                new TaskCategory("Health"),
                new TaskCategory("Personal")
        };
    }

    // helper method to create a temporary file with test data
    private String createTestFileWithCategories() throws IOException {
        String filename = "test_tasks.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("Task1,Description1,10,2025-01-15,UniStuff\n");
            writer.write("Task2,Description2,20,2025-01-16,Health\n");
            writer.write("Task3,Description3,30,2025-01-17,UniStuff\n");
            writer.write("Task4,Description4,40,2025-01-18,Personal\n");
        }
        return filename;
    }

    // helper method to delete the temporary file
    private void deleteTestFile(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testConstructor() throws IOException {
        initializeCategories(); // initialize categories for TaskManager
        String filename = createTestFileWithCategories(); // create test file with sample tasks
        TaskStats taskStats = new TaskStats(filename);

        List<Task> tasks = taskStats.tasks; // access the tasks list
        assertEquals(4, tasks.size(), "The number of tasks should match the file data");

        // validate the first task's details
        Task firstTask = tasks.get(0);
        assertEquals("Task1", firstTask.getTitle());
        assertEquals("Description1", firstTask.getDescription());
        assertEquals(10, firstTask.getPriority());
        assertEquals(LocalDate.parse("2025-01-15"), firstTask.getDeadline());

        deleteTestFile(filename); // clean up after test
    }

    @Test
    public void testGetTotalTasks() throws IOException {
        initializeCategories(); // initialize categories for TaskManager
        String filename = createTestFileWithCategories(); // create test file with sample tasks
        TaskStats taskStats = new TaskStats(filename);

        int totalTasks = taskStats.getTotalTasks();
        assertEquals(4, totalTasks, "The total number of tasks should be 4");

        deleteTestFile(filename); // clean up after test
    }

    @Test
    public void testGetAveragePriority() throws IOException {
        initializeCategories(); // initialize categories for TaskManager
        String filename = createTestFileWithCategories(); // create test file with sample tasks
        TaskStats taskStats = new TaskStats(filename);

        double averagePriority = taskStats.getAveragePriority();
        assertEquals(25.0, averagePriority, 0.001, "The average priority should be 25.0");

        deleteTestFile(filename); // clean up after test
    }

    @Test
    public void testMultiThreadedExecution() throws IOException {
        initializeCategories(); // initialize categories for TaskManager
        String filename = createTestFileWithCategories(); // create test file with sample tasks
        TaskStats taskStats = new TaskStats(filename);

        // measure time for total tasks computation
        long startTime = System.currentTimeMillis();
        int totalTasks = taskStats.getTotalTasks();
        long endTime = System.currentTimeMillis();
        System.out.println("Total tasks: " + totalTasks + " computed in " + (endTime - startTime) + "ms");
        assertEquals(4, totalTasks, "The total number of tasks should be 4");

        // measure time for average priority computation
        startTime = System.currentTimeMillis();
        double averagePriority = taskStats.getAveragePriority();
        endTime = System.currentTimeMillis();
        System.out.println("average priority: " + averagePriority + " computed in " + (endTime - startTime) + "ms");
        assertEquals(25.0, averagePriority, 0.001, "the average priority should be 25.0");

        deleteTestFile(filename); // clean up after test
    }
}
