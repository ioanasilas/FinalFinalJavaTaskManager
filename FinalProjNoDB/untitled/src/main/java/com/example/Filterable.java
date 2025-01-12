package com.example;

public interface Filterable {
    Task[] filterByPriority(int priority);
    Task[] filterByDeadline(String deadlineString);
}
