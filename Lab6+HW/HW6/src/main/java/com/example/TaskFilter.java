package com.example;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class TaskFilter implements Filterable {
    private String filename;

    public TaskFilter(String filename) {
        this.filename = filename;
    }

    // load tasks from file and filter by priority
    @Override
    public Task[] filterByPriority(int priority) {
        List<Task> matchingTasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = Task.fromCSV(line);
                if (task.getPriority() == priority) {
                    matchingTasks.add(task);
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading tasks from file: " + e.getMessage());
        }

        return matchingTasks.toArray(new Task[0]);
    }

    // load tasks from file and filter by deadline
    @Override
    public Task[] filterByDeadline(LocalDate deadline) {
        List<Task> matchingTasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = Task.fromCSV(line);
                if (task.getDeadline().isBefore(deadline) || task.getDeadline().isEqual(deadline)) {
                    matchingTasks.add(task);
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading tasks from file: " + e.getMessage());
        }

        return matchingTasks.toArray(new Task[0]);
    }
}
