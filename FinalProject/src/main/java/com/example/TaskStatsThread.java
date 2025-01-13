package com.example;

import java.util.List;

public class TaskStatsThread extends Thread {
    private final List<Task> tasks;
    private int partialTaskCount = 0;
    private int partialPrioritySum = 0;

    public TaskStatsThread(List<Task> tasks) {
        this.tasks = tasks;
    }

    // when we start thread this runs
    @Override
    public void run() {
        for (Task task : tasks) {
            partialTaskCount++;
            partialPrioritySum += task.getPriority();
        }
    }

    public int getPartialTaskCount() {
        return partialTaskCount;
    }

    public int getPartialPrioritySum() {
        return partialPrioritySum;
    }
}
