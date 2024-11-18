package com.example;
public class TaskCategory implements Statsable, Comparable<TaskCategory>
{
    private String categoryName;
    private Task[] tasks;
    private int taskCount = 0;

    public TaskCategory(String categoryName, int capacity)
    {
        this.categoryName = categoryName;
        // initialize an array with given capacity
        this.tasks = new Task[capacity];
    }

    public void addTask(Task task)
    {
        if (taskCount == tasks.length)
        {
            // create new array with double size and copy els to new array
            Task[] newTasks = new Task[tasks.length * 2];
            System.arraycopy(tasks, 0, newTasks, 0, tasks.length);
            tasks = newTasks;
        }
        // create task object
        tasks[taskCount++] = task;
    }

    public boolean removeTask(Task task)
    {
        for (int i = 0; i < taskCount; i++)
        {
            if (tasks[i].equals(task))
            {
                for (int j = i; j < taskCount - 1; j++)
                {
                    tasks[j] = tasks[j + 1];
                }
                tasks[taskCount - 1] = null;
                taskCount--;
                return true;
            }
        }
        return false;
    }

    public Task getTaskByName(String taskName)
    {
        for (Task task : tasks)
        {
            if (task != null && task.getTitle().equals(taskName))
            {
                return task;
            }
        }
        return null;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public Task[] getTasks()
    {
        return tasks;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Category name: ").append(categoryName).append("\n");
        sb.append("Task count: ").append(taskCount).append("\n");
        for (Task task : tasks)
        {
            if (task != null)
                sb.append(task.toString()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public int getTotalTasks()
    {
        return taskCount;
    }

    @Override
    public double getAveragePriority()
    {
        if (taskCount == 0)
        {
            return 0;
        }
        int totalPriority = 0;
        for (Task task : tasks)
        {
            if (task != null)
            {
                totalPriority += task.getPriority();
            }
        }
        return (double) totalPriority / taskCount;
    }

    @Override
    public int compareTo(TaskCategory other)
    {
        return Integer.compare(this.getTotalTasks(), other.getTotalTasks());
    }
}
