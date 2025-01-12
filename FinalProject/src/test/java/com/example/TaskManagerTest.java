package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TaskManagerTest {

    @Test
    void testTaskManagerConstructor() {
        TaskManager taskManager = new TaskManager();

        // verify that the TaskManager object is created successfully
        assertNotNull(taskManager, "TaskManager object should be created successfully.");

        // verify that the currentTask is initialized to null
        assertNull(taskManager.getCurrentTask(), "Current task should be initialized as null.");
    }
}
