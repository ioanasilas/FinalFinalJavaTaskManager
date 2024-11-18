package com.example;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String nameOfFile = "";
        TaskManager taskManager = null;
        TaskCategory [] taskCategories = initializeCategories();
        boolean loadedOrCreated = false;

        // check for command-line arguments for loading or creating file
        if (args.length == 2) {
            String action = args[0];
            nameOfFile = args[1];

            if (action.equalsIgnoreCase("load")) {
                taskManager = new TaskManager(null, taskCategories, nameOfFile);
                taskManager.actionOnFile(nameOfFile);
                // add already existing tasks to categories
                TaskManager.parseFile(nameOfFile);
                loadedOrCreated = true;
            } else if (action.equalsIgnoreCase("create")) {
                taskManager = new TaskManager(null, taskCategories, nameOfFile);
                System.out.println("Creating the file: " + nameOfFile);
                taskManager = new TaskManager(null, taskCategories, nameOfFile);
                loadedOrCreated = true;
            } else {
                System.out.println("Invalid action. Use 'load' or 'create' as the first argument, or no arguments.");
                return;
            }
        } else {
            // if no args, go on with interactive mode
            System.out.println("No arguments passed. Starting interactive mode.");
        }

        // first load or create new file
        // if we have not already
        if (!loadedOrCreated) {
            while (true) {
                System.out.println("\uD83D\uDDC3\uFE0F  Before we begin, how to operate with data?");
                System.out.println("1. Load from file.");
                System.out.println("2. Start new file.");
                System.out.println("Enter your choice (1 or 2): ");

                String choice = scanner.nextLine();
                if (choice.equals("1")) {
                    System.out.println("What file to load from?");
                    String fileName = scanner.nextLine();
                    nameOfFile = fileName;
                    taskManager = new TaskManager(null, taskCategories, nameOfFile);
                    taskManager.actionOnFile(fileName);
                    // add already existing tasks to categories
                    TaskManager.parseFile(nameOfFile);
                    break;
                } else if (choice.equals("2")) {
                    System.out.println("Name of file to start?");
                    String fileName = scanner.nextLine();
                    System.out.println("Creating new file " + fileName);
                    nameOfFile = fileName;
                    taskManager = new TaskManager(null, taskCategories, nameOfFile);
                    break;
                } else {
                    System.out.println("Invalid option, please enter 1 or 2.");
                }
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
            System.out.println("8. viewTasks");
            System.out.println("9. viewCategories");
            System.out.println("10. Exit");
            System.out.println("Enter your command:");

            String command = scanner.nextLine();
            if (command.equals("9") || command.equalsIgnoreCase("exit")) {
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
                    if (promptForReturnToMenu(scanner)) {
                        return; // exit the application
                    }
                    break;
                }

                case "removeTask": {
                    if (parts.length == 2) {
                        taskManager.removeFromFile(parts[1]);
                    } else {
                        System.out.println("❌ Invalid arguments. Usage: removeTask <title>");
                    }
                    if (promptForReturnToMenu(scanner)) {
                        return; // exit the application
                    }
                    break;
                }
                case "filterByPriority": {
                    if (parts.length == 2) {
                        try{
                            int priority = Integer.parseInt(parts[1]);
                            TaskFilter filter = new TaskFilter(nameOfFile);
                            Task[] filteredTasks = filter.filterByPriority(priority);
                            System.out.println();
                            for (Task filteredTask : filteredTasks)
                            {
                                System.out.println(filteredTask);
                                System.out.println();
                            }
                        }
                        catch (NumberFormatException e) {
                            System.out.println("#\uFE0F⃣  Please input an integer for the priority.");}
                    }
                    else {
                        System.out.println("❌ Invalid arguments. Usage: filterByPriority <priority>");
                       }
                    if (promptForReturnToMenu(scanner)) {
                        return; // exit the application
                    }
                    break;
                }
                case "filterByDeadline": {
                    if (parts.length == 2) {
                        String deadline = parts[1];
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
                    if (promptForReturnToMenu(scanner)) {
                        return; // exit the application
                    }
                    break;
                }
                case "overallStats": {
                    TaskStats stats = new TaskStats(nameOfFile);
                    System.out.println("\uD83C\uDF8F Total tasks: " + stats.getTotalTasks());
                    System.out.println("Avg priority: " + stats.getAveragePriority());
                    if (promptForReturnToMenu(scanner)) {
                        return; // exit the application
                    }
                    break;
                }
                case "categoryStats": {
                    if (parts.length == 2) {
                        String categoryName = parts[1];
                        TaskCategory category = taskManager.findCategory(categoryName);
                        if (category != null) {
                            System.out.println("\uD83D\uDD35  Category: " + categoryName);
                            System.out.println("Total tasks: " + category.getTotalTasks());
                            System.out.println("Avg priority: " + category.getAveragePriority());
                        } else {
                            System.out.println("❌ Category not found.");
                        }
                    } else {
                        System.out.println("❌ Invalid arguments. Usage: categoryStats <category name>");
                    }
                    if (promptForReturnToMenu(scanner)) {
                        return; // exit the application
                    }
                    break;
                }
                case "sortTasksByDeadline": {
                    List<Task> tasksFromFile = taskManager.parseFile(nameOfFile);
                    tasksFromFile.sort(Task::compareTo);
                    System.out.println("\uD83D\uDC7E Sorted tasks by deadline:");
                    tasksFromFile.forEach(System.out::println);
                    if (promptForReturnToMenu(scanner)) {
                        return; // exit the application
                    }
                    break;
                }
                case "viewTasks": {
                    taskManager.viewTasks(nameOfFile);
                    if (promptForReturnToMenu(scanner)) {
                        return; // exit the application
                    }
                    break;
                }
                case "viewCategories": {
                    for (TaskCategory taskCategory : taskCategories) {
                        System.out.println(taskCategory);
                    }
                    if (promptForReturnToMenu(scanner)) {
                        return; // exit the application
                    }
                    break;
                }
                default:
                    System.out.println("Invalid command.");
            }
        }
    }

    private static TaskCategory[] initializeCategories() {
        return new TaskCategory[]{
                new TaskCategory("UniStuff"),
                new TaskCategory("Health"),
                new TaskCategory("Personal")
        };
    }

    // prompt the user to go back to the menu or exit
    private static boolean promptForReturnToMenu(Scanner scanner) {
        System.out.println("Would you like to go back to the menu? (y/n)");
        String goBack = scanner.nextLine();
        if (goBack.equalsIgnoreCase("y")) {
            return false; // go back to the main menu
        } else {
            System.out.println("Exiting.");
            return true; // exit the application
        }
    }
}
