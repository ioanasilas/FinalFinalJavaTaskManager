package com.example;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;


public class MainFX extends Application {

    private Scene loginScene;
    private Scene viewerScene;

    private final UserDAO userDAO = new UserDAO();
    private final TaskCategoryDAO categoryDAO = new TaskCategoryDAO();

    // store user
    private User loggedInUser;

    // text area to display results
    private final TextArea resultsArea = new TextArea();

    @Override
    public void start(Stage primaryStage) {
        // vreate scenes
        createLoginScene(primaryStage);
        createViewerScene(primaryStage);

        primaryStage.setTitle("simple javafx demo");
        primaryStage.setScene(loginScene); // Show login by default
        primaryStage.show();
    }

    // scene 1: login
    private void createLoginScene(Stage stage) {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #ADD8E6;"); // Light blue

        Label label = new Label("please log in");
        TextField userField = new TextField();
        userField.setPromptText("username");
        PasswordField passField = new PasswordField();
        passField.setPromptText("password");
        Button loginButton = new Button("login");

        loginButton.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText().trim();
            try {
                // get user from DB
                Optional<User> userOpt = userDAO.getUserByUsername(username);
                if (userOpt.isEmpty()) {
                    showAlert("error", "user not found");
                    return;
                }
                User user = userOpt.get();
                if (!user.getPassword().equals(password)) {
                    showAlert("error", "invalid password");
                    return;
                }

                loggedInUser = user;
                stage.setScene(viewerScene); // redirect to viewer scene for all users
            } catch (SQLException ex) {
                showAlert("error", "db error: " + ex.getMessage());
            }
        });

        layout.getChildren().addAll(label, userField, passField, loginButton);
        loginScene = new Scene(layout, 300, 250);
    }

    // scene 2: viewer
    private void createViewerScene(Stage stage) {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #ADD8E6;"); // Light blue

        Label label = new Label("viewer scene");
        Button viewCategoriesBtn = new Button("view categories");
        Button logoutBtn = new Button("logout");

        // viewer can only see categories
        viewCategoriesBtn.setOnAction(e -> {
            try {
                List<TaskCategory> categories = categoryDAO.getAllCategories();
                if (categories.isEmpty()) {
                    resultsArea.setText("no categories found");
                } else {
                    StringBuilder sb = new StringBuilder("all categories:\n");
                    for (TaskCategory c : categories) {
                        sb.append(c).append("\n");
                    }
                    resultsArea.setText(sb.toString());
                }
            } catch (SQLException ex) {
                resultsArea.setText("error: " + ex.getMessage());
            }
        });

        logoutBtn.setOnAction(e -> {
            loggedInUser = null;
            resultsArea.clear();
            stage.setScene(loginScene);
        });

        layout.getChildren().addAll(label, viewCategoriesBtn, logoutBtn, resultsArea);
        viewerScene = new Scene(layout, 400, 300);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR, content, ButtonType.OK);
        alert.setHeaderText(title);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
