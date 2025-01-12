package com.example;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testTaskConstructor() {
        // create a task and verify all fields
        Task task = new Task(1, "Task1", "Description", 50, LocalDate.of(2025, 1, 31));
        assertEquals("Task1", task.getTitle(), "Title should match the provided value");
        assertEquals("Description", task.getDescription(), "Description should match the provided value");
        assertEquals(50, task.getPriority(), "Priority should match the provided value");
        assertEquals(LocalDate.of(2025, 1, 31), task.getDeadline(), "Deadline should match the provided value");
        assertEquals(1, task.getId(), "ID should match the provided value");
    }

    @Test
    void testToString() {
        // verify the string representation of the task
        Task task = new Task(1, "Task1", "Description", 50, LocalDate.of(2025, 1, 31));
        String expected = "\uD83D\uDD39  Task: [Task1] - Description (Priority: 50, Due: 2025-01-31)";
        assertEquals(expected, task.toString(), "toString() should format the task details correctly");
    }

    @Test
    void testCompareTo() {
        // create tasks for comparison
        Task task1 = new Task(1, "Task1", "Description", 50, LocalDate.of(2025, 1, 31));
        Task task2 = new Task(2, "Task2", "Description", 30, LocalDate.of(2025, 1, 30));

        // Task 1 is later than Task 2 by deadline
        assertTrue(task1.compareTo(task2) > 0, "Task1 should be after Task2 by deadline");

        // Task 2 is earlier than Task 1 by deadline
        assertTrue(task2.compareTo(task1) < 0, "Task2 should be before Task1 by deadline");

        // test case where deadlines are equal but priorities differ
        Task task3 = new Task(3, "Task3", "Description", 70, LocalDate.of(2025, 1, 31));
        assertTrue(task3.compareTo(task1) > 0, "Task3 should come before Task1 due to higher priority");
        assertTrue(task1.compareTo(task3) < 0, "Task1 should come after Task3 due to lower priority");

        // test case where deadlines and priorities are equal
        Task task4 = new Task(4, "Task4", "Description", 50, LocalDate.of(2025, 1, 31));
        assertEquals(0, task1.compareTo(task4), "Tasks with the same deadline and priority should be equal");
    }
}
