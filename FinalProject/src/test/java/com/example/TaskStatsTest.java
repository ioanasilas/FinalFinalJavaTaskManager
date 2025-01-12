package com.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskStatsTest {

    @Test
    void testConstructorWithCategoryId() {
        try {
            TaskStats taskStats = new TaskStats(1);

            // verify that the TaskStats object is created successfully
            assertNotNull(taskStats, "TaskStats object should be created successfully.");
            assertNotNull(taskStats.tasks, "Tasks list inside TaskStats should not be null.");
            assertTrue(taskStats.tasks.size() >= 0, "Tasks list should have valid size.");

            // verify task properties if specific tasks are expected
            if (!taskStats.tasks.isEmpty()) {
                Task firstTask = taskStats.tasks.get(0);
                assertNotNull(firstTask, "First task in the list should not be null.");
                System.out.println("First task: " + firstTask);
            }
        } catch (Exception e) {
            fail("Constructor threw an unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void testConstructorWithTaskList() {
        // sample list of tasks
        List<Task> sampleTasks = new ArrayList<>();
        sampleTasks.add(new Task(1, "Task1", "Description1", 50, java.time.LocalDate.now()));
        sampleTasks.add(new Task(2, "Task2", "Description2", 75, java.time.LocalDate.now().plusDays(1)));

        // initialize TaskStats with the sample list
        TaskStats taskStats = new TaskStats(sampleTasks);

        // verify that the tasks list is correctly set
        assertNotNull(taskStats.tasks, "Tasks list inside TaskStats should not be null.");
        assertEquals(sampleTasks.size(), taskStats.tasks.size(), "Tasks list size should match the input list.");

        // verify task properties
        assertEquals("Task1", taskStats.tasks.get(0).getTitle(), "First task title should match.");
        assertEquals("Task2", taskStats.tasks.get(1).getTitle(), "Second task title should match.");
    }

    @Test
    void testConstructorWithEmptyTaskList() {
        // pass an empty list to the constructor
        TaskStats taskStats = new TaskStats(new ArrayList<>());

        // verify that the tasks list is initialized but empty
        assertNotNull(taskStats.tasks, "Tasks list inside TaskStats should not be null.");
        assertTrue(taskStats.tasks.isEmpty(), "Tasks list should be empty.");
    }

    @Test
    void testConstructorWithNullTaskList() {
        // pass null to the constructor
        TaskStats taskStats = new TaskStats(null);

        // verify that the tasks list is initialized to an empty list
        assertNotNull(taskStats.tasks, "Tasks list inside TaskStats should not be null.");
        assertTrue(taskStats.tasks.isEmpty(), "Tasks list should be empty when initialized with null.");
    }
}
