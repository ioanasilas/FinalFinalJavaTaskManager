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
    private final UserDAO userDAO;
    private User loggedInUser;

    public Menu(TaskCategory[] taskCategories) {
        this.scanner = new Scanner(System.in);
        this.taskManager = new TaskManager();
        this.categoryDAO = new TaskCategoryDAO();
        this.userDAO = new UserDAO();
    }

    public void start() {
        while (true) {
            if (loggedInUser == null) {
                showPreMenu();
            } else {
                printMenu();
                String command = scanner.nextLine().trim();
                if (command.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting. Goodbye!");
                    System.exit(0);
                }
                handleCommand(command);
            }
        }
    }

    private void showPreMenu() {
        System.out.println("\n\uD83C\uDF10 Welcome to Task Manager");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                handleLogin();
                break;
            case "2":
                handleRegister();
                break;
            case "3":
                System.out.println("Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("❌ Invalid choice. Please try again.");
        }
    }

    private void handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        try {
            User user = userDAO.getUserByUsername(username)
                    .filter(u -> u.getPassword().equals(password))
                    .orElse(null);

            if (user != null) {
                loggedInUser = user;
                System.out.println("✅ Login successful! Welcome, " + loggedInUser.getUsername() + " (" + loggedInUser.getRole() + ").");
            } else {
                System.out.println("❌ Invalid username or password. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error during login: " + e.getMessage());
        }
    }

    private void handleRegister() {
        System.out.print("Enter a new username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter a new password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Enter role (Admin/User/Viewer): ");
        String role = scanner.nextLine().trim();

        try {
            if (userDAO.getUserByUsername(username).isPresent()) {
                System.out.println("❌ Username already exists. Please try a different one.");
                return;
            }

            userDAO.createUser(username, password, role);
            System.out.println("✅ Registration successful! You can now log in.");
        } catch (SQLException e) {
            System.out.println("❌ Error during registration: " + e.getMessage());
        }
    }

    private void printMenu() {
        // short disclaimers per role
        if (isViewer(loggedInUser)) {
            System.out.println("ℹ️ note: you have read-only access. some commands won't work.");
        } else if (isUser(loggedInUser)) {
            System.out.println("ℹ️ note: you can view and add tasks, but some commands (like remove) are admin only.");
        } else {
            System.out.println("ℹ️ note: you are an admin, you have full privileges.");
        }

        System.out.println("\n\uD83D\uDD77  available commands:");
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
        System.out.println("12. Logout");
        System.out.print("enter your command: ");
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
                case "logout":
                    handleLogout();
                    break;
                default:
                    System.out.println("❌ Invalid command.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // add based on user id
    private void handleAddTask(String[] parts) throws Exception {
        if (isViewer(loggedInUser)) {
            System.out.println("❌ Viewers are not allowed to add tasks.");
            return;
        }

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

            if (taskManager.addTask(title, description, priority, dueDate, category, loggedInUser.getId())) {
                System.out.println("✅ Task added successfully.");
            }
        } else {
            System.out.println("❌ Invalid arguments. Usage: addTask <title> <description> <priority> <due date> <category>");
        }
    }


    private void handleLogout() {
        System.out.println("Logging out...");
        loggedInUser = null;
        showPreMenu();
    }

    // only admins remove
    private void handleRemoveTask(String[] parts) {
        if (!isAdmin(loggedInUser)) {
            System.out.println("❌ Only admins can remove tasks.");
            return;
        }

        if (parts.length == 2) {
            String title = parts[1];
            taskManager.removeTask(title);
        } else {
            System.out.println("❌ Invalid arguments. Usage: removeTask <title>");
        }
    }

    // each user has its own
    private void handleFilterByPriority(String[] parts) {
        if (parts.length == 2) {
            try {
                int priority = Integer.parseInt(parts[1]);
                TaskDAO taskDAO = new TaskDAO();

                List<Task> tasks;
                if (isAdmin(loggedInUser)) {
                    // admin sees all tasks
                    tasks = taskDAO.getAllTasks();
                } else {
                    // user sees only their tasks
                    tasks = taskDAO.getTasksByUser(loggedInUser.getId());
                }

                // now filter from that list
                List<Task> filteredTasks = tasks.stream()
                        .filter(t -> t.getPriority() == priority)
                        .toList();

                printTasks(filteredTasks.toArray(new Task[0]));
            } catch (NumberFormatException | SQLException e) {
                System.out.println("❌ error filtering tasks by priority: " + e.getMessage());
            }
        } else {
            System.out.println("❌ invalid arguments. usage: filterByPriority <priority>");
        }
    }

    // users filter their own, admin every
    private void handleFilterByDeadline(String[] parts) {
        if (parts.length == 2) {
            try {
                LocalDate deadline = LocalDate.parse(parts[1], DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                TaskDAO taskDAO = new TaskDAO();

                List<Task> tasks;
                if (isAdmin(loggedInUser)) {
                    tasks = taskDAO.getAllTasks();
                } else {
                    tasks = taskDAO.getTasksByUser(loggedInUser.getId());
                }

                List<Task> filteredTasks = tasks.stream()
                        .filter(t -> !t.getDeadline().isAfter(deadline))
                        .toList();

                printTasks(filteredTasks.toArray(new Task[0]));
            } catch (DateTimeParseException | SQLException e) {
                System.out.println("❌ error filtering tasks by deadline: " + e.getMessage());
            }
        } else {
            System.out.println("❌ invalid arguments. usage: filterByDeadline <deadline>");
        }
    }

    // user specific
    private void handleOverallStats() {
        try {
            TaskDAO taskDAO = new TaskDAO();
            List<Task> tasks;

            if (isViewer(loggedInUser)) {
                // viewers can't see stats
                System.out.println("❌ viewer is read-only, cannot view stats.");
                return;
            } else if (isUser(loggedInUser)) {
                // user only sees their own tasks
                tasks = taskDAO.getTasksByUser(loggedInUser.getId());
            } else {
                // admin sees all tasks
                tasks = taskDAO.getAllTasks();
            }

            if (tasks.isEmpty()) {
                System.out.println("🎯 no tasks available to calculate stats.");
                return;
            }

            TaskStats stats = new TaskStats(tasks);
            int totalTasks = stats.getTotalTasks();
            double averagePriority = stats.getAveragePriority();

            System.out.println("🎯 total tasks (for your role): " + totalTasks);
            System.out.println("🎯 average priority: " + String.format("%.2f", averagePriority));

        } catch (SQLException e) {
            System.out.println("❌ error calculating stats: " + e.getMessage());
        }
    }

    // viewers see their own, admins everything
    private void handleCategoryStats(String[] parts) {
        // viewer can't see stats
        if (isViewer(loggedInUser)) {
            System.out.println("❌ viewer is read-only, cannot view stats.");
            return;
        }

        if (parts.length == 2) {
            String categoryName = parts[1];
            try {
                TaskCategory category = categoryDAO.getCategoryByName(categoryName);
                if (category == null) {
                    System.out.println("❌ category not found.");
                    return;
                }

                TaskDAO taskDAO = new TaskDAO();
                List<Task> tasks;

                if (isAdmin(loggedInUser)) {
                    // admin sees all tasks in this category
                    tasks = taskDAO.getTasksByCategory(category.getId());
                } else {
                    // user sees only their tasks in this category
                    tasks = taskDAO.getTasksByUserAndCategory(loggedInUser.getId(), category.getId());
                }

                if (tasks.isEmpty()) {
                    System.out.println("no tasks found in this category for your role.");
                    return;
                }

                // compute stats
                TaskStats stats = new TaskStats(tasks);
                System.out.println("\uD83C\uDF8F total tasks: " + stats.getTotalTasks());
                System.out.println("avg priority: " + stats.getAveragePriority());
            } catch (SQLException e) {
                System.out.println("❌ error calculating stats: " + e.getMessage());
            }
        } else {
            System.out.println("❌ invalid arguments. usage: categoryStats <category name>");
        }
    }

// admin everything, user theirs
    private void handleSortTasksByDeadline() {
        // viewer has no access
        if (isViewer(loggedInUser)) {
            System.out.println("❌ viewers do not have access to tasks.");
            return;
        }

        try {
            TaskDAO taskDAO = new TaskDAO();
            List<Task> tasks;

            if (isAdmin(loggedInUser)) {
                // admin sees all tasks
                tasks = taskDAO.getAllTasks();
            } else {
                // regular user sees only their tasks
                tasks = taskDAO.getTasksByUser(loggedInUser.getId());
            }

            // sort by compareTo (deadline then priority)
            tasks.sort(Task::compareTo);

            // print results
            printTasks(tasks.toArray(new Task[0]));
        } catch (SQLException e) {
            System.out.println("❌ error sorting tasks: " + e.getMessage());
        }
    }


    private void handleViewTasks() {
        try {
            // if admin, get all tasks
            // if user, get only tasks for that user
            // if viewer, show error
            if (isViewer(loggedInUser)) {
                System.out.println("❌ viewers can only view categories.");
                return;
            }

            TaskDAO taskDAO = new TaskDAO();
            List<Task> tasks;
            if (isAdmin(loggedInUser)) {
                tasks = taskDAO.getAllTasks();
            } else {
                tasks = taskDAO.getTasksByUser(loggedInUser.getId());
            }

            printTasks(tasks.toArray(new Task[0]));
        } catch (SQLException e) {
            System.out.println("❌ error retrieving tasks: " + e.getMessage());
        }
    }

// viewer no, user access to own
    private void handleViewTasksByCategory(String[] parts) {
        // viewer has no access
        if (isViewer(loggedInUser)) {
            System.out.println("❌ viewers do not have access to tasks.");
            return;
        }

        if (parts.length == 2) {
            String categoryName = parts[1];
            try {
                TaskCategory category = categoryDAO.getCategoryByName(categoryName);
                if (category == null) {
                    System.out.println("❌ category not found.");
                    return;
                }

                TaskDAO taskDAO = new TaskDAO();
                List<Task> tasks;

                if (isAdmin(loggedInUser)) {
                    // admin sees tasks from any user in this category
                    tasks = taskDAO.getTasksByCategory(category.getId());
                } else {
                    // regular user sees only their own tasks in this category
                    tasks = taskDAO.getTasksByUserAndCategory(loggedInUser.getId(), category.getId());
                }

                if (tasks.isEmpty()) {
                    System.out.println("no tasks found for category: " + categoryName);
                } else {
                    tasks.forEach(System.out::println);
                }
            } catch (SQLException e) {
                System.out.println("❌ error retrieving tasks by category: " + e.getMessage());
            }
        } else {
            System.out.println("❌ invalid arguments. usage: viewTasksByCategory <category name>");
        }
    }


    // everyone, everything
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

    // everyone, every task
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

    private boolean isAdmin(User user) {
        return "Admin".equalsIgnoreCase(user.getRole());
    }

    private boolean isUser(User user) {
        return "User".equalsIgnoreCase(user.getRole());
    }

    private boolean isViewer(User user) {
        return "Viewer".equalsIgnoreCase(user.getRole());
    }
}
