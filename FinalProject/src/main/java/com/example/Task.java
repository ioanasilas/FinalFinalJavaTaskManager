package com.example;

import java.time.LocalDate;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Task implements Comparable<Task> {
    private final int id;
    private final String title;
    private final String description;
    private final int priority;
    private final LocalDate deadline;

    // use argument (non default) constructor not setters since we want immutability
    public Task(int id, String title, String description, int priority, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.deadline = dueDate;
    }

    // getters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public int getId() {
        return id;
    }

    // override toString for displaying task details
    @Override
    public String toString() {
        return String.format("\uD83D\uDD39  Task: [%s] - %s (Priority: %d, Due: %s)", title, description, priority, deadline);
    }

    // implement compareTo for sorting tasks
    @Override
    public int compareTo(Task other) {
        // compare by deadline first and, if they are the same, compare by priority
        int deadlineComparison = this.deadline.compareTo(other.deadline);
        if (deadlineComparison != 0) {
            return deadlineComparison;
        }
        return Integer.compare(this.priority, other.priority);
    }

    public static Task fromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        int priority = rs.getInt("priority");
        LocalDate deadline = rs.getDate("deadline").toLocalDate();

        return new Task(id, title, description, priority, deadline);
    }

    // remove csv-related methods as we are transitioning to db storage
    /*
    public String toCSV() {
        return String.format("%s,%s,%d,%s", title, description, priority, deadline);
    }

    public static Task fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid CSV format for Task");
        }
        String title = parts[0];
        String description = parts[1];
        int priority = Integer.parseInt(parts[2]);
        LocalDate deadline = LocalDate.parse(parts[3]);

        return new Task(title, description, priority, deadline);
    }
    */
}
