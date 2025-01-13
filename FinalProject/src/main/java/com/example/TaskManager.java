package com.example;

import java.time.LocalDate;
import java.util.List;

public class TaskManager {
    private Task currentTask;
    private final TaskCategoryDAO categoryDAO;
    private final TaskDAO taskDAO;

    public TaskManager() {
        this.categoryDAO = new TaskCategoryDAO();
        this.taskDAO = new TaskDAO();
    }

    // add a task to the database
    public boolean addTask(String title, String description, int priority, LocalDate deadline, String categoryName, int userId)
            throws DuplicateTaskException, CategoryNotFoundException, InvalidPriorityException {
        if (priority < 0 || priority > 100) {
            throw new InvalidPriorityException("Priority must be an int between 0 and 100");
        }

        try {
            TaskCategory category = categoryDAO.getCategoryByName(categoryName);
            if (category == null) {
                throw new CategoryNotFoundException("Category '" + categoryName + "' does not exist.");
            }

            List<Task> tasks = taskDAO.getTasksByCategory(category.getId());
            for (Task task : tasks) {
                if (task.getTitle().equalsIgnoreCase(title)) {
                    throw new DuplicateTaskException("Task with title " + title + " already exists.");
                }
            }

            Task task = new Task(0, title, description, priority, deadline);
            taskDAO.addTask(task, category.getId(), userId); // Pass userId here
            currentTask = task;
            return true;

        } catch (Exception e) {
            throw new RuntimeException("Error adding task: " + e.getMessage(), e);
        }
    }


    // remove a task by title
    public void removeTask(String title) {
        try {
            // search for the task by title across all categories
            List<TaskCategory> allCategories = categoryDAO.getAllCategories();
            Task taskToRemove = null;
            TaskCategory categoryOfTask = null;

            for (TaskCategory category : allCategories) {
                List<Task> tasks = taskDAO.getTasksByCategory(category.getId());
                for (Task task : tasks) {
                    if (task.getTitle().equalsIgnoreCase(title)) {
                        taskToRemove = task;
                        categoryOfTask = category;
                        break;
                    }
                }
                if (taskToRemove != null) break; // exit loop once task is found
            }

            // if task not found, show error message
            if (taskToRemove == null) {
                System.out.println("❌ Task '" + title + "' not found in any category.");
                return;
            }

            // remove the task from db
            taskDAO.deleteTaskById(taskToRemove.getId());
            System.out.println("✅ Task '" + title + "' removed successfully from category '" + categoryOfTask.getCategoryName() + "'.");

        } catch (Exception e) {
            System.out.println("❌ Error removing task: " + e.getMessage());
        }
    }

    // retrieve all tasks for a category
    public void viewTasksByCategory(String categoryName) {
        try {
            TaskCategory category = categoryDAO.getCategoryByName(categoryName);
            if (category == null) {
                System.out.println("❌ Category '" + categoryName + "' not found.");
                return;
            }

            List<Task> tasks = taskDAO.getTasksByCategory(category.getId());
            if (tasks.isEmpty()) {
                System.out.println("No tasks found for category: " + categoryName);
            } else {
                tasks.forEach(System.out::println);
            }

        } catch (Exception e) {
            System.out.println("Error retrieving tasks: " + e.getMessage());
        }
    }


    public Task getCurrentTask() {
        return currentTask;
    }
}
