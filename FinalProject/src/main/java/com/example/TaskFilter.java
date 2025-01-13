package com.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class TaskFilter implements Filterable {
    private final TaskDAO taskDAO;

    public TaskFilter() {
        this.taskDAO = new TaskDAO();
    }

    @Override
    public Task[] filterByPriority(int priority) {
        try {
            List<Task> allTasks = taskDAO.getAllTasks(); // retrieve all tasks from the database
            List<Task> filteredTasks = allTasks.stream()
                    .filter(task -> task.getPriority() == priority)
                    .toList();
            return filteredTasks.toArray(new Task[0]);
        } catch (Exception e) {
            System.out.println("Error filtering tasks by priority: " + e.getMessage());
            return new Task[0];
        }
    }

    @Override
    public Task[] filterByDeadline(String deadlineString) {
        try {
            // DateTimeFormatter to parse the custom date format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate deadline = LocalDate.parse(deadlineString, formatter);

            List<Task> allTasks = taskDAO.getAllTasks(); // Retrieve all tasks from the database
            List<Task> filteredTasks = allTasks.stream()
                    .filter(task -> task.getDeadline().isBefore(deadline) || task.getDeadline().isEqual(deadline))
                    .toList();

            return filteredTasks.toArray(new Task[0]);
        } catch (Exception e) {
            System.out.println("Error filtering tasks by deadline: " + e.getMessage());
            return new Task[0];
        }
    }

}
