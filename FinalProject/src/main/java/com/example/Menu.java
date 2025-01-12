package com.example;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Menu {
    private final Scanner scanner;
    private final TaskManager taskManager;
    private final TaskCategoryDAO categoryDAO;

    public Menu(TaskCategory[] taskCategories) {
        this.scanner = new Scanner(System.in);
        this.taskManager = new TaskManager();
        this.categoryDAO = new TaskCategoryDAO();
    }

    public void start() {
        while (true) {
            printMenu();
            String command = scanner.nextLine().trim();
            if (command.equalsIgnoreCase("exit")) {
                System.out.println("Exiting. Goodbye!");
                break;
            }
            handleCommand(command);
        }
    }

    private void printMenu() {
        System.out.println("\n\uD83D\uDD77  Available Commands:");
        System.out.println("1. addTask <title> <description> <priority> <due date> <category>");
        System.out.println("2. removeTask <title>");
        System.out.println("3. filterByPriority <priority>");
        System.out.println("4. filterByDeadline <deadline>");
        System.out.println("5. overallStats");
        System.out.println("6. categoryStats <category name>");
        System.out.println("7. sortTasksByDeadline");
        System.out.println("8. viewTasks");
        System.out.println("9. viewCategories");
        System.out.println("10. viewTasksByCategory <category name>");
        System.out.println("11. measureExecutionTime");
        System.out.println("12. Exit");
        System.out.print("Enter your command:");
    }

    private void handleCommand(String command) {
        Pattern pattern = Pattern.compile("\"([^\"]*)\"|\\S+");
        Matcher matcher = pattern.matcher(command);

        List<String> partsList = new ArrayList<>();
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                partsList.add(matcher.group(1));
            } else {
                partsList.add(matcher.group());
            }
        }

        String[] parts = partsList.toArray(new String[0]);
        try {
            switch (parts[0].toLowerCase()) {
                case "addtask":
                    handleAddTask(parts);
                    break;
                case "removetask":
                    handleRemoveTask(parts);
                    break;
                case "filterbypriority":
                    handleFilterByPriority(parts);
                    break;
                case "filterbydeadline":
                    handleFilterByDeadline(parts);
                    break;
                case "overallstats":
                    handleOverallStats();
                    break;
                case "categorystats":
                    handleCategoryStats(parts);
                    break;
                case "sorttasksbydeadline":
                    handleSortTasksByDeadline();
                    break;
                case "viewtasks":
                    handleViewTasks();
                    break;
                case "viewtasksbycategory":
                    handleViewTasksByCategory(parts);
                    break;
                case "viewcategories":
                    handleViewCategories();
                    break;
                case "measureexecutiontime":
                    handleMeasureExecutionTime();
                    break;
                default:
                    System.out.println("❌ Invalid command.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void handleAddTask(String[] parts) throws Exception {
        if (parts.length == 6) {
            String title = parts[1];
            String description = parts[2];
            int priority = Integer.parseInt(parts[3]);

            LocalDate dueDate;
            try {
                dueDate = LocalDate.parse(parts[4], DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid date format. Please use dd.MM.yyyy.");
                return;
            }

            String category = parts[5];

            if (taskManager.addTask(title, description, priority, dueDate, category)) {
                System.out.println("✅ Task added successfully.");
            }
        } else {
            System.out.println("❌ Invalid arguments. Usage: addTask <title> <description> <priority> <due date> <category>");
        }
    }

    private void handleRemoveTask(String[] parts) {
        if (parts.length == 2) {
            String title = parts[1];

            taskManager.removeTask(title); // No category needed for this version
        } else {
            System.out.println("❌ Invalid arguments. Usage: removeTask <title>");
        }
    }

    private void handleFilterByPriority(String[] parts) {
        if (parts.length == 2) {
            int priority = Integer.parseInt(parts[1]);
            TaskFilter filter = new TaskFilter();
            Task[] filteredTasks = filter.filterByPriority(priority);
            printTasks(filteredTasks);
        } else {
            System.out.println("❌ Invalid arguments. Usage: filterByPriority <priority>");
        }
    }

    private void handleFilterByDeadline(String[] parts) {
        if (parts.length == 2) {
            String deadline = parts[1];
            TaskFilter filter = new TaskFilter();
            Task[] filteredTasks = filter.filterByDeadline(deadline);
            printTasks(filteredTasks);
        } else {
            System.out.println("❌ Invalid arguments. Usage: filterByDeadline <deadline>");
        }
    }

    private void handleOverallStats() {
        try {
            TaskDAO taskDAO = new TaskDAO();
            List<Task> allTasks = taskDAO.getAllTasks();
            System.out.println("\uD83C\uDF8F Total tasks: " + allTasks.size());
        } catch (SQLException e) {
            System.out.println("❌ Error calculating stats: " + e.getMessage());
        }
    }

    private void handleCategoryStats(String[] parts) {
        if (parts.length == 2) {
            String categoryName = parts[1];
            try {
                TaskCategory category = categoryDAO.getCategoryByName(categoryName);
                if (category != null) {
                    System.out.println("\uD83C\uDF8F Total tasks: " + category.getTotalTasks());
                    System.out.println("Avg priority: " + category.getAveragePriority());
                } else {
                    System.out.println("❌ Category not found.");
                }
            } catch (SQLException e) {
                System.out.println("❌ Error calculating stats: " + e.getMessage());
            }
        } else {
            System.out.println("❌ Invalid arguments. Usage: categoryStats <category name>");
        }
    }

    private void handleSortTasksByDeadline() {
        try {
            TaskDAO taskDAO = new TaskDAO();
            List<Task> allTasks = taskDAO.getAllTasks();
            allTasks.sort(Task::compareTo);
            printTasks(allTasks.toArray(new Task[0]));
        } catch (SQLException e) {
            System.out.println("❌ Error sorting tasks: " + e.getMessage());
        }
    }

    private void handleViewTasks() {
        try {
            TaskDAO taskDAO = new TaskDAO();
            List<Task> allTasks = taskDAO.getAllTasks();
            printTasks(allTasks.toArray(new Task[0]));
        } catch (SQLException e) {
            System.out.println("❌ Error retrieving tasks: " + e.getMessage());
        }
    }

    private void handleViewTasksByCategory(String[] parts) {
        if (parts.length == 2) {
            String categoryName = parts[1];
            taskManager.viewTasksByCategory(categoryName);
        } else {
            System.out.println("❌ Invalid arguments. Usage: viewTasksByCategory <category name>");
        }
    }

    private void handleViewCategories() {
        try {
            List<TaskCategory> categories = categoryDAO.getAllCategories();
            if (categories.isEmpty()) {
                System.out.println("No categories found.");
            } else {
                categories.forEach(System.out::println);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error retrieving categories: " + e.getMessage());
        }
    }

    private void handleMeasureExecutionTime() {
        try {
            TaskDAO taskDAO = new TaskDAO();
            List<Task> allTasks = taskDAO.getAllTasks();
            TaskStats stats = new TaskStats(allTasks);
            stats.measureExecutionTimeForDifferentThreads();
        } catch (SQLException e) {
            System.out.println("❌ Error measuring execution time: " + e.getMessage());
        }
    }

    private void printTasks(Task[] tasks) {
        if (tasks.length == 0) {
            System.out.println("No tasks found.");
        } else {
            for (Task task : tasks) {
                System.out.println(task);
            }
        }
    }
}
