package com.example;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String nameOfFile;
        TaskManager taskManager = null;

        // first load or create new file
        while (true) {
            System.out.println("Before we begin, how to operate with data?");
            System.out.println("1. Load from file.");
            System.out.println("2. Start new file.");
            System.out.println("Enter your choice (1 or 2): ");

            String choice = scanner.nextLine();
            if (choice.equals("1")) {
                System.out.println("What file to load from?");
                String fileName = scanner.nextLine();
                nameOfFile = fileName;
                taskManager = new TaskManager(null, initializeCategories(), nameOfFile);
                taskManager.createFile(fileName);
                // load existing tasks into an array
//                Task [] allTasks = null;
                // create a dynamic array of given size
//                allTasks = taskManager.loadTasksFromFile().toArray(new Task[0]);
                break;
            } else if (choice.equals("2")) {
                System.out.println("Name of file to start?");
                String fileName = scanner.nextLine();
                System.out.println("Creating new file " + fileName);
                nameOfFile = fileName;
                taskManager = new TaskManager(null, initializeCategories(), nameOfFile);
                break;
            } else {
                System.out.println("Invalid option, please enter 1 or 2.");
            }
        }

        while (true) {
            System.out.println("\uD83D\uDD77  Available Commands:");
            System.out.println("1. addTask <title> <description> <priority> <due date> <category>");
            System.out.println("2. removeTask <title>");
            System.out.println("3. filterByPriority <priority>");
            System.out.println("4. filterByDeadline <deadline>");
            System.out.println("5. overallStats");
            System.out.println("6. categoryStats <category name>");
            System.out.println("7. sortTasksByDeadline");
            System.out.println("8. Exit");
            System.out.println("Enter your command:");

            String command = scanner.nextLine();
            if (command.equals("8") || command.equalsIgnoreCase("exit")) {
                System.out.println("Exiting.");
                break;
            }

            String[] parts = command.split(" ");
            switch (parts[0]) {
                case "addTask": {
                    if (parts.length == 6) {
                        String taskTitle = parts[1];
                        String taskDescription = parts[2];
                        int taskPriority = Integer.parseInt(parts[3]);

                        try {
                            // custom date format "dd.MM.yyyy"
                            LocalDate taskDueDate = LocalDate.parse(parts[4], DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                            String taskCategory = parts[5];

                            try {
                                boolean taskAdded = taskManager.addTask(taskTitle, taskDescription, taskPriority, taskDueDate, taskCategory);
                                if (taskAdded) {
                                    System.out.println("✅ Task added successfully.");
                                }
                            } catch (DuplicateTaskException e) {
                                System.out.println("⚠️ " + e.getMessage());
                            }
                        } catch (DateTimeParseException e) {
                            System.out.println("⚠️ Invalid date format. Please use <DD.MM.YYYY>.");
                        }
                    } else {
                        System.out.println("❌ Invalid arguments. Usage: addTask <title> <description> <priority> <due date> <category>");
                    }
                    break;
                }

                case "removeTask": {
                    if (parts.length == 2) {
                        taskManager.removeFromFile(parts[1]);
                    } else {
                        System.out.println("❌ Invalid arguments. Usage: removeTask <title>");
                    }
                    break;
                }
                case "filterByPriority": {
                    if (parts.length == 2) {
                        int priority = Integer.parseInt(parts[1]);
                        TaskFilter filter = new TaskFilter(nameOfFile);
                        Task[] filteredTasks = filter.filterByPriority(priority);
                        System.out.println();
                        for (Task filteredTask : filteredTasks)
                         {
                             System.out.println(filteredTask);
                             System.out.println();

                         }
                    } else {
                        System.out.println("❌ Invalid arguments. Usage: filterByPriority <priority>");
                    }
                    break;
                }
                case "filterByDeadline": {
                    if (parts.length == 2) {
                        LocalDate deadline = LocalDate.parse(parts[1]);
                        TaskFilter filter = new TaskFilter(nameOfFile);
                        Task[] filteredTasks = filter.filterByDeadline(deadline);
                        System.out.println();
                        for (Task filteredTask : filteredTasks)
                        {
                            System.out.println(filteredTask);
                            System.out.println();
                        }
                    } else {
                        System.out.println("❌ Invalid arguments. Usage: filterByDeadline <deadline>");
                    }
                    break;
                }
                case "overallStats": {
                    TaskStats stats = new TaskStats(nameOfFile);
                    System.out.println("\uD83C\uDF8F Total tasks: " + stats.getTotalTasks());
                    System.out.println("Avg priority: " + stats.getAveragePriority());
                    break;
                }
                case "categoryStats": {
                    if (parts.length == 2) {
                        String categoryName = parts[1];
                        TaskCategory category = taskManager.findCategory(categoryName);
                        if (category != null) {
                            System.out.println("Category: " + categoryName);
                            System.out.println("Total tasks: " + category.getTotalTasks());
                            System.out.println("Avg priority: " + category.getAveragePriority());
                        } else {
                            System.out.println("❌ Category not found.");
                        }
                    } else {
                        System.out.println("❌ Invalid arguments. Usage: categoryStats <category name>");
                    }
                    break;
                }
                case "sortTasksByDeadline": {
                    List<Task> tasksFromFile = taskManager.parseFile(nameOfFile);
                    tasksFromFile.sort(Task::compareTo);
                    System.out.println("\uD83D\uDC7E Sorted tasks by deadline:");
                    tasksFromFile.forEach(System.out::println);
                    break;
                }
                default:
                    System.out.println("Invalid command.");
            }
        }
    }

    private static TaskCategory[] initializeCategories() {
        return new TaskCategory[]{
                new TaskCategory("UniStuff", 10),
                new TaskCategory("Health", 10),
                new TaskCategory("Personal", 10)
        };
    }
}
