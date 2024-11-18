package com.example;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TaskStats implements Statsable {
    private final String filename;
    private List<Task> tasks;

    public TaskStats(String filename) {
        this.filename = filename;
        this.tasks = loadTasksFromFile();
    }

    // load tasks from the file
    List<Task> loadTasksFromFile() {
        List<Task> tasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Task task = Task.fromCSV(line);
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error loading tasks from file: " + e.getMessage());
        }
        return tasks;
    }

    @Override
    public int getTotalTasks() {
        return tasks.size();
    }

    @Override
    public double getAveragePriority() {
        if (tasks.isEmpty()) {
            return 0;
        }
        int totalPriority = 0;
        for (Task task : tasks) {
            totalPriority += task.getPriority();
        }
        return (double) totalPriority / tasks.size();
    }
}
