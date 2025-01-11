package com.example;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testTaskConstructor() {
        Task task = new Task("Task1", "Description", 50, LocalDate.of(2025, 1, 31));
        assertEquals("Task1", task.getTitle());
        assertEquals("Description", task.getDescription());
        assertEquals(50, task.getPriority());
        assertEquals(LocalDate.of(2025, 1, 31), task.getDeadline());
    }

    @Test
    void testToString() {
        Task task = new Task("Task1", "Description", 50, LocalDate.of(2025, 1, 31));
        String expected = "\uD83D\uDD39  Task: [Task1] - Description (Priority: 50, Due: 2025-01-31)";
        assertEquals(expected, task.toString());
    }

    @Test
    void testCompareTo() {
        Task task1 = new Task("Task1", "Description", 50, LocalDate.of(2025, 1, 31));
        Task task2 = new Task("Task2", "Description", 30, LocalDate.of(2025, 1, 30));
        assertTrue(task1.compareTo(task2) > 0);
        assertTrue(task2.compareTo(task1) < 0);
    }
}
