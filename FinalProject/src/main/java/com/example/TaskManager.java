package com.example;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// for task lifecycle
public class TaskManager {
    private Task currentTask;
    static TaskCategory[] categories;
    String filename;

    public TaskManager(Task currentTask, TaskCategory[] categories, String filename) {
        this.currentTask = currentTask;
        this.categories = categories;
        this.filename = filename;
    }

    public boolean addTask(String title, String description, int priority, LocalDate deadline, String categoryName) throws DuplicateTaskException, CategoryNotFoundException, InvalidPriorityException {
        if (priority < 0 || priority > 100) {
            throw new InvalidPriorityException("Priority must be an int between 0 and 100");
        }

        for (TaskCategory category : categories) {
            if (category.getCategoryName().equalsIgnoreCase(categoryName)) {
                if (category.getTaskByName(title) != null) {
                    throw new DuplicateTaskException("Task with title " + title + " already exists");
                }
                Task task = new Task(title, description, priority, deadline);
                category.addTask(task);
                currentTask = task;
                saveToFile(task, categoryName);
                return true;
            }
        }
        throw new CategoryNotFoundException("Category '" + categoryName + "' does not exist.");
    }



    public TaskCategory findCategory(String categoryName) {
        for (TaskCategory category : categories) {
            if (category != null && category.getCategoryName().equals(categoryName)) {
                return category;
            }
        }
        return null;
    }

    public void viewTasks(String filename) {
        File file = new File(filename);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
               Task task = Task.fromCSV(line);
                System.out.println(task);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // Lab 5 files
    public void actionOnFile(String filename) {
        File file = new File(filename);
        try {
            if (file.createNewFile()) {
                System.out.println("📁 File " + filename + " did not already exist.");
                System.out.println(filename + " is now created.");
            } else {
                System.out.println("✅ File " + filename + " exists");
            }
        } catch (IOException e) {
            System.out.println("❌ Error creating file: " + e.getMessage());
        }
    }

    public void saveToFile(Task task, String categoryName) {

        File file = new File(filename);
        //  append to the file
        try (FileWriter writer = new FileWriter(file, true)) {
            if (task != null) {
                String csvLine = task.toCSV() + "," + categoryName + "\n";
                writer.write(csvLine);
                System.out.println("✅ Task saved to " + filename);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Task> parseFile(String filename) {
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            File file = new File(filename);
            List<Task> loadedTasks = new ArrayList<>();
            if (file.exists()) {
                String line;
                while ((line = reader.readLine()) != null)
                {
                    String [] parts = line.split(",");
                    String title = parts[0];
                    String categoryFromCSV = parts[parts.length - 1];
                    for (TaskCategory category : categories) {
                        if (category.getCategoryName().equalsIgnoreCase(categoryFromCSV))
                        {
                            Task task = category.getTaskByName(title);
                            // if task is not yet in category array
                            if (task == null) {
                                task = Task.fromCSV(line);
                                category.addTask(task);
                            }
                            loadedTasks.add(task);
                            break;
                        }
                    }
                }
            }
            return loadedTasks;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeFromFile(String title) {
        boolean foundTask = false;
        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (!parts[0].equalsIgnoreCase(title)) {
                    updatedLines.add(line);
                } else {
                    for (TaskCategory category : categories) {
                        Task task = category.getTaskByName(title);
                        if (task != null) {
                            category.removeTask(task);
                            foundTask = true;
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading from file: " + e.getMessage());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false))) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
            if (foundTask) {
                System.out.println("✅ Task removed from file.");
            } else {
                System.out.println("❌ Task " + title + " was not found.");
            }
        } catch (IOException e) {
            System.out.println("❌ Error writing to file: " + e.getMessage());
        }
    }


    public Task getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(Task currentTask) {
        this.currentTask = currentTask;
    }
}
