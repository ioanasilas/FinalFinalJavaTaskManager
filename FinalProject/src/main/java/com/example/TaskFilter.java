package com.example;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
public class TaskFilter implements Filterable {
    String filename;

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
    public Task[] filterByDeadline(String deadlineInput) {
        List<Task> matchingTasks = new ArrayList<>();

        // attempt to parse the deadline input
        LocalDate deadline = null;
        try {
            deadline = LocalDate.parse(deadlineInput, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (DateTimeParseException e) {
            System.out.println("⚠️ Invalid date format: " + deadlineInput + ". Please use dd.MM.yyyy");
            return new Task[0];  // return an empty array if the date is invalid
        }

        // read file and filter tasks by the parsed deadline
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
