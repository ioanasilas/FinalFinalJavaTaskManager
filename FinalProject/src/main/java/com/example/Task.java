package com.example;
import java.time.LocalDate;
// we need serializable to convert to stream of bytes
public class Task implements Comparable<Task>
{
    private String title;
    private String description;
    private int priority;
    private LocalDate deadline;

    public Task(String title, String description, int priority, LocalDate dueDate)
    {
        this.title = title;
        this.description = description;
        this.priority = priority;
        deadline = dueDate;
    }

    public String getTitle()
    {
        return title;
    }
    public String getDescription() { return description; }
    public int getPriority() { return priority; }
    public LocalDate getDeadline() { return deadline; }

    @Override
    public String toString() {
        return String.format("\uD83D\uDD39  Task: [%s] - %s (Priority: %d, Due: %s)", title, description, priority, deadline);
    }

    @Override
    public int compareTo(Task other)
    {
        // compare by deadline first and if they are the same, compare by priority
        int deadlineComparison = this.deadline.compareTo(other.deadline);
        if (deadlineComparison != 0)
        {
            return deadlineComparison;
        }
        return Integer.compare(this.priority, other.priority);
    }

    // for files, Lab5
    public String toCSV()
    {
        return String.format("%s,%s,%d,%s", title, description, priority, deadline);
    }

    public static Task fromCSV(String csvLine)
    {
        String[] parts = csvLine.split(",");
        if (parts.length != 5)
        {
            throw new IllegalArgumentException("Invalid CSV format for Task");
        }
        String title = parts[0];
        String description = parts[1];
        int priority = Integer.parseInt(parts[2]);
        LocalDate deadline = LocalDate.parse(parts[3]);


        return new Task(title, description, priority, deadline);
    }
}
