package com.example;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TaskStats implements Statsable {
    private final List<Task> tasks;

    public TaskStats(String filename) {
        this.tasks = TaskManager.parseFile(filename);
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
