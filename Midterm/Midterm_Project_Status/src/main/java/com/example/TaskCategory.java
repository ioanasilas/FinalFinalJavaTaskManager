package com.example;

import java.util.HashMap;
import java.util.Map;

public class TaskCategory implements Statsable, Comparable<TaskCategory> {
    private final String categoryName;
    private Map<String, Task> taskMap; // store tasks by name

    public TaskCategory(String categoryName) {
        this.categoryName = categoryName;
        this.taskMap = new HashMap<>();
    }

    public void addTask(Task task) {
        taskMap.put(task.getTitle(), task); // add task using the task title as the key
    }

    public boolean removeTask(Task task) {
        return taskMap.remove(task.getTitle(), task); // removes task by name
    }

    public Task getTaskByName(String taskName) {
        return taskMap.get(taskName); // get task by name in constant time
    }

    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\uD83D\uDCCA  Category name: ").append(categoryName).append("\n");
        sb.append("Task count: ").append(taskMap.size()).append("\n");
        for (Task task : taskMap.values()) {
            sb.append(task.toString()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public int getTotalTasks() {
        return taskMap.size(); // size of map is the total task count
    }

    @Override
    public double getAveragePriority() {
        if (taskMap.isEmpty()) {
            return 0;
        }
        int totalPriority = 0;
        for (Task task : taskMap.values()) {
            totalPriority += task.getPriority();
        }
        return (double) totalPriority / taskMap.size();
    }

    @Override
    public int compareTo(TaskCategory other) {
        return Integer.compare(this.getTotalTasks(), other.getTotalTasks());
    }
}
