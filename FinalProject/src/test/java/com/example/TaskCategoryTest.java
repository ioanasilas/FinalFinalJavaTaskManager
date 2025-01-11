package com.example;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TaskCategoryTest {

    @Test
    void testTaskCategoryConstructor() {
        TaskCategory category = new TaskCategory("Work");
        assertEquals("Work", category.getCategoryName());
        assertEquals(0, category.getTotalTasks());
    }

    @Test
    void testAddTask() {
        TaskCategory category = new TaskCategory("Work");
        Task task = new Task("Task1", "Description", 50, LocalDate.of(2025, 1, 31));

        category.addTask(task);
        assertEquals(1, category.getTotalTasks());
        assertEquals(task, category.getTaskByName("Task1"));
    }

    @Test
    void testRemoveTask() {
        TaskCategory category = new TaskCategory("Work");
        Task task = new Task("Task1", "Description", 50, LocalDate.of(2025, 1, 31));

        category.addTask(task);
        assertEquals(1, category.getTotalTasks());

        boolean removed = category.removeTask(task);
        assertTrue(removed);
        assertEquals(0, category.getTotalTasks());
        assertNull(category.getTaskByName("Task1"));
    }

    @Test
    void testRemoveNonExistentTask() {
        TaskCategory category = new TaskCategory("Work");
        Task task = new Task("Task1", "Description", 50, LocalDate.of(2025, 1, 31));

        boolean removed = category.removeTask(task);
        assertFalse(removed);
    }

    @Test
    void testGetTotalTasks() {
        TaskCategory category = new TaskCategory("Work");
        assertEquals(0, category.getTotalTasks());

        category.addTask(new Task("Task1", "Description", 50, LocalDate.of(2025, 1, 31)));
        category.addTask(new Task("Task2", "Description", 30, LocalDate.of(2025, 2, 28)));

        assertEquals(2, category.getTotalTasks());
    }

    @Test
    void testGetAveragePriority() {
        TaskCategory category = new TaskCategory("Work");
        assertEquals(0.0, category.getAveragePriority(), 0.01); // Tolerate minor floating-point differences

        category.addTask(new Task("Task1", "Description", 50, LocalDate.of(2025, 1, 31)));
        category.addTask(new Task("Task2", "Description", 30, LocalDate.of(2025, 2, 28)));
        category.addTask(new Task("Task3", "Description", 70, LocalDate.of(2025, 3, 15)));

        assertEquals(50.0, category.getAveragePriority(), 0.01);
    }

    @Test
    void testToString() {
        TaskCategory category = new TaskCategory("Work");
        Task task1 = new Task("Task1", "Description1", 50, LocalDate.of(2025, 1, 31));
        Task task2 = new Task("Task2", "Description2", 30, LocalDate.of(2025, 2, 28));

        category.addTask(task1);
        category.addTask(task2);

        String result = category.toString();
        assertTrue(result.contains("Category name: Work"));
        assertTrue(result.contains("Task count: 2"));
        assertTrue(result.contains("Task1"));
        assertTrue(result.contains("Task2"));
    }

    @Test
    void testCompareTo() {
        TaskCategory category1 = new TaskCategory("Work");
        TaskCategory category2 = new TaskCategory("Personal");

        category1.addTask(new Task("Task1", "Description", 50, LocalDate.of(2025, 1, 31)));
        category2.addTask(new Task("Task2", "Description", 30, LocalDate.of(2025, 2, 28)));
        category2.addTask(new Task("Task3", "Description", 70, LocalDate.of(2025, 3, 15)));

        assertTrue(category1.compareTo(category2) < 0);
        assertTrue(category2.compareTo(category1) > 0);

        // Equal case
        category1.addTask(new Task("Task4", "Description", 90, LocalDate.of(2025, 4, 10)));
        assertEquals(0, category1.compareTo(category2));
    }
}
