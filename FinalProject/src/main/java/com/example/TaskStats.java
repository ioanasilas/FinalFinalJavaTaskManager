package com.example;

import com.example.TaskStatsThread;

import java.util.ArrayList;
import java.util.List;

public class TaskStats implements Statsable {
    final List<Task> tasks;

    // constructor for a specific category
    public TaskStats(int categoryId) {
        TaskDAO taskDAO = new TaskDAO();

        // get tasks for the category from the database
        List<Task> fetchedTasks;
        try {
            fetchedTasks = taskDAO.getTasksByCategory(categoryId);
        } catch (Exception e) {
            System.out.println("Error fetching tasks: " + e.getMessage());
            fetchedTasks = new ArrayList<>();
        }
        this.tasks = fetchedTasks; // initialize tasks
    }

    // constructor for all tasks
    public TaskStats(List<Task> tasks) {
        this.tasks = tasks != null ? tasks : new ArrayList<>();
    }

    @Override
    public int getTotalTasks() {
        return runTaskStatsThreadsAndAggregate(TaskStatsThread::getPartialTaskCount, 4); // default is 4 threads
    }

    @Override
    public double getAveragePriority() {
        int totalPrioritySum = runTaskStatsThreadsAndAggregate(TaskStatsThread::getPartialPrioritySum, 4); // sum priorities
        int totalTasks = runTaskStatsThreadsAndAggregate(TaskStatsThread::getPartialTaskCount, 4); // total task count

        return (totalTasks == 0) ? 0 : (double) totalPrioritySum / totalTasks; // avoid division by zero
    }

    public void measureExecutionTimeForDifferentThreads() {
        int[] threadCounts = {1, 2, 4, 8, 16}; // thread counts to measure execution time

        System.out.println("Measuring execution time for different thread counts:");
        for (int threadCount : threadCounts) {
            long startTime = System.nanoTime();
            runTaskStatsThreadsAndAggregate(TaskStatsThread::getPartialTaskCount, threadCount);
            long endTime = System.nanoTime();

            long durationMs = (endTime - startTime) / 1_000_000; // nanos to millis
            System.out.printf("Using %d thread(s): %d ms%n", threadCount, durationMs);
        }
    }

    private int runTaskStatsThreadsAndAggregate(TaskStatsAggregator aggregator, int threadCount) {
        List<TaskStatsThread> threads = new ArrayList<>();
        // add threadCount so we round up not truncate so we do not remain uncovered
        // not just thread count cuz we might end up rounding up too much
        int chunkSize = (tasks.size() + threadCount - 1) / threadCount; // divide tasks into nearly equal chunks

        for (int i = 0; i < threadCount; i++) {
            int start = i * chunkSize;
            // we do not want to process beyond the size
            int end = Math.min(start + chunkSize, tasks.size());
            if (start < end) {
                threads.add(new TaskStatsThread(tasks.subList(start, end)));
            }
        }

        // each thread begins execution async (run method)
        // main thread running loop does not wait for these just continues
        for (TaskStatsThread thread : threads) {
            thread.start();
        }

        int result = 0;
        try {
            // main thread waits until current finishes execution
            // then aggregates result
            // so main thread processes just after all threads are done
            for (TaskStatsThread thread : threads) {
                thread.join();
                result += aggregator.aggregate(thread);
            }
        } catch (InterruptedException e) {
            System.out.println("Thread execution interrupted: " + e.getMessage());
        }
        return result;
    }

    @FunctionalInterface
    private interface TaskStatsAggregator {
        int aggregate(TaskStatsThread thread);
    }
}
