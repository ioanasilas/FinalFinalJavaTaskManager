package com.example;
import java.time.LocalDate;
public interface Filterable
{
    Task[] filterByPriority(int priority);
    Task[] filterByDeadline(String deadlineString);
}
