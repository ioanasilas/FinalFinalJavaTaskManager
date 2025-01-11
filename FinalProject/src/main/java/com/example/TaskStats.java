package com.example;

import java.util.ArrayList;
import java.util.List;

public class TaskStats implements Statsable {
    final List<Task> tasks;

    public TaskStats(String filename) {
        this.tasks = TaskManager.parseFile(filename);
    }

    @Override
    public int getTotalTasks() {
        // using helper method to compute total tasks
        return runTaskStatsThreadsAndAggregate(TaskStatsThread::getPartialTaskCount);
    }

    @Override
    public double getAveragePriority() {
        // using helper method to compute total tasks and total priority
        // getPartialPrioritySum, getPartialTaskCount is the implementation of aggregate
        int totalPrioritySum = runTaskStatsThreadsAndAggregate(TaskStatsThread::getPartialPrioritySum);
        int totalTasks = runTaskStatsThreadsAndAggregate(TaskStatsThread::getPartialTaskCount);

        return (totalTasks == 0) ? 0 : (double) totalPrioritySum / totalTasks;
    }

    /**
     * A generic method to run TaskStatsThreads, wait for their completion, and aggregate results.
     *
     * @param aggregator A function that extracts the desired result from each thread.
     * @return The aggregated result from all threads.
     */
    private int runTaskStatsThreadsAndAggregate(TaskStatsAggregator aggregator) {
        int threadCount = Math.min(4, tasks.size()); // number of threads
        List<TaskStatsThread> threads = new ArrayList<>();
        // decide chunk based on how many tasks and threads
        int chunkSize = tasks.size() / threadCount;

        // divide tasks into chunks and assign to threads
        for (int i = 0; i < threadCount; i++) {
            // each thread starts at start, ends at end
            int start = i * chunkSize;
            int end = (i == threadCount - 1) ? tasks.size() : start + chunkSize;
            threads.add(new TaskStatsThread(tasks.subList(start, end)));
        }

        // start all threads
        for (TaskStatsThread thread : threads) {
            thread.start();
        }

        // main thread waits for all threads to finish and aggregate results
        int result = 0;
        try {
            for (TaskStatsThread thread : threads) {
                thread.join();
                // same as calling result += thread.getPartialTaskCount();
                // same as calling result += thread.getPartialPrioritySum();
                result += aggregator.aggregate(thread);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Functional interface (has just one abstract method) for aggregating results from threads.
     */
    @FunctionalInterface
    private interface TaskStatsAggregator {
        int aggregate(TaskStatsThread thread);
    }
}
