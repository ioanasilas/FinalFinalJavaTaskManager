package com.example;

import java.util.List;

/**
 * This is what each thread does, processes task subset to get partial results
 * as in the count and priority sum
 */

class TaskStatsThread extends Thread {
    private final List<Task> tasks;
    private int partialTaskCount = 0;
    private int partialPrioritySum = 0;

    public TaskStatsThread(List<Task> tasks) {
        this.tasks = tasks;
    }

    // executed when using thread.start()
    @Override
    public void run() {
        for (Task task : tasks) {
            partialTaskCount++;
            partialPrioritySum += task.getPriority();
        }
    }

    // getter for no of tasks processed by this thread
    public int getPartialTaskCount() {
        return partialTaskCount;
    }

    // getter for sum of priorities processed by this thread
    public int getPartialPrioritySum() {
        return partialPrioritySum;
    }
}
