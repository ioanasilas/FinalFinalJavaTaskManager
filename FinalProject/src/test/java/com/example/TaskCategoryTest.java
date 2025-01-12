package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskCategoryTest {

    @Test
    void testTaskCategoryConstructor() {
        TaskCategory category = new TaskCategory(1, "Work");
        assertEquals("Work", category.getCategoryName());
        assertEquals(1, category.getId());
    }

    @Test
    void testGetTotalTasks() {
        TaskCategory category = new TaskCategory(1, "Work");
        TaskStats stats = new TaskStats(category.getId());
        int totalTasks = stats.getTotalTasks();
        assertTrue(totalTasks >= 0, "Total tasks should be non-negative.");
    }

    @Test
    void testGetAveragePriority() {
        TaskCategory category = new TaskCategory(1, "Work");
        TaskStats stats = new TaskStats(category.getId());
        double avgPriority = stats.getAveragePriority();
        assertTrue(avgPriority >= 0.0, "Average priority should be non-negative.");
    }

    @Test
    void testToString() {
        TaskCategory category = new TaskCategory(1, "Work");
        String result = category.toString();
        assertTrue(result.contains("Category: Work"), "ToString should contain the category name.");
        assertTrue(result.contains("ID: 1"), "ToString should contain the category ID.");
    }

    @Test
    void testCompareTo() {
        TaskCategory category1 = new TaskCategory(1, "Work");
        TaskCategory category2 = new TaskCategory(2, "Personal");

        TaskStats stats1 = new TaskStats(category1.getId());
        TaskStats stats2 = new TaskStats(category2.getId());

        int category1TotalTasks = stats1.getTotalTasks();
        int category2TotalTasks = stats2.getTotalTasks();

        if (category1TotalTasks < category2TotalTasks) {
            assertTrue(category1.compareTo(category2) < 0, "Category1 should compare as less than Category2.");
        } else if (category1TotalTasks > category2TotalTasks) {
            assertTrue(category1.compareTo(category2) > 0, "Category1 should compare as greater than Category2.");
        } else {
            assertEquals(0, category1.compareTo(category2), "Categories with the same total tasks should be equal.");
        }
    }
}
