package com.sanda.librarymanagement.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class AdminHubController {

    @FXML private StackPane contentArea;
    @FXML private Button btnOverview;
    @FXML private Button btnBooks;
    @FXML private Button btnCustomers;
    @FXML private Button btnRentals;
    @FXML private Button btnManageUsers;
    @FXML private Button btnSystemReports;
    @FXML private Button btnClose;

    private List<Button> sidebarButtons;

    public void initialize() {
        sidebarButtons = Arrays.asList(btnOverview, btnBooks, btnCustomers, btnRentals, btnManageUsers, btnSystemReports);

        loadForm("/view/AdminOverview.fxml", btnOverview);

        btnOverview.setOnAction(e -> loadForm("/view/AdminOverview.fxml", btnOverview));
        btnBooks.setOnAction(e -> loadForm("/view/AdminBookOverview.fxml", btnBooks));
        btnCustomers.setOnAction(e -> loadForm("/view/AdminCustomerBase.fxml", btnCustomers));
        btnRentals.setOnAction(e -> loadForm("/view/AdminRentalLogs.fxml", btnRentals));
        btnManageUsers.setOnAction(e -> loadForm("/view/AdminUserManagement.fxml", btnManageUsers));
        btnSystemReports.setOnAction(e -> loadForm("/view/AdminReports.fxml", btnSystemReports));
    }

    private void loadForm(String fxmlPath, Button selectedButton) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                System.err.println("CRITICAL ERROR: Could not find FXML at: " + fxmlPath);
                return;
            }
            Parent root = FXMLLoader.load(resource);

            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);

            updateButtonStyle(selectedButton);

        } catch (IOException e) {
            System.err.println("IOException: Failed to load " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void updateButtonStyle(Button selectedButton) {
        for (Button btn : sidebarButtons) {
            if (btn == selectedButton) {
                btn.getStyleClass().add("sidebar-button-active");
            } else {
                btn.getStyleClass().remove("sidebar-button-active");
            }
            if (btn == btnManageUsers) {
                btn.getStyleClass().add("sidebar-button-users");
            } else {
                btn.getStyleClass().remove("sidebar-button-users");
            }
        }
    }

    @FXML
    public void logoutaction(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            Scene loginScene = new Scene(root);

            Stage currentStage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();

            currentStage.setScene(loginScene);
            currentStage.setTitle("Library Management System - Login");

            try {
                currentStage.getIcons().add(new Image(getClass().getResourceAsStream("/book.png")));
            } catch (Exception e) {
                System.out.println("Icon not found, keeping existing icon.");
            }

            currentStage.centerOnScreen();

        } catch (Exception e) {
            System.err.println("Failed to load the Login screen!");
            e.printStackTrace();
        }
    }

    public void closeApp(ActionEvent actionEvent) {
        System.exit(0);
    }
}