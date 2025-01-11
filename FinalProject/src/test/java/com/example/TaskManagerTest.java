package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    private final String testFilename = "testfile.csv";

    @AfterEach
    void cleanupTestFile() {
        File file = new File(testFilename);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("✅ Test file deleted successfully: " + testFilename);
            } else {
                System.out.println("❌ Failed to delete test file: " + testFilename);
            }
        }
    }

    @Test
    void testTaskManagerConstructor() {
        TaskCategory[] categories = new TaskCategory[]{
                new TaskCategory("Work"),
                new TaskCategory("Personal")
        };

        // Create TaskManager instance
        TaskManager manager = new TaskManager(null, categories, testFilename);

        // Validate fields
        assertNull(manager.getCurrentTask(), "Current task should be null initially");
        assertEquals(categories, TaskManager.categories, "Categories should be correctly set");
        assertEquals(testFilename, manager.filename, "Filename should be correctly set");
    }

    @Test
    void testAddTask() throws Exception {
        TaskCategory[] categories = new TaskCategory[]{new TaskCategory("UniStuff"), new TaskCategory("Health")};
        TaskManager manager = new TaskManager(null, categories, testFilename);

        boolean result = manager.addTask("Task1", "Test Description", 50, LocalDate.of(2025, 1, 31), "UniStuff");
        assertTrue(result);
        assertEquals("Task1", manager.getCurrentTask().getTitle());
    }

    @Test
    void testAddDuplicateTask() throws Exception {
        TaskCategory[] categories = new TaskCategory[]{new TaskCategory("UniStuff")};
        TaskManager manager = new TaskManager(null, categories, testFilename);

        manager.addTask("Task1", "Test Description", 50, LocalDate.of(2025, 1, 31), "UniStuff");

        Exception exception = assertThrows(DuplicateTaskException.class, () -> {
            manager.addTask("Task1", "Duplicate Task", 60, LocalDate.of(2025, 2, 1), "UniStuff");
        });

        assertEquals("Task with title Task1 already exists", exception.getMessage());
    }

    @Test
    void testRemoveTask() throws Exception {
        TaskCategory[] categories = new TaskCategory[]{new TaskCategory("UniStuff")};
        TaskManager manager = new TaskManager(null, categories, testFilename);

        manager.addTask("Task1", "Test Description", 50, LocalDate.of(2025, 1, 31), "UniStuff");
        manager.removeFromFile("Task1");

        assertNull(categories[0].getTaskByName("Task1"));
    }
}
