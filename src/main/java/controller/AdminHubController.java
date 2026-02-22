package controller;

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

    // Helper list to manage all sidebar buttons for easy styling
    private List<Button> sidebarButtons;

    public void initialize() {

            btnClose.setOnMouseEntered(e -> {
                btnClose.setStyle("-fx-background-color: #e81123; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 0; -fx-border-radius: 0;");
            });
            btnClose.setOnMouseExited(e -> {
                btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 0; -fx-border-radius: 0;");
            });



        sidebarButtons = Arrays.asList(btnOverview, btnBooks, btnCustomers, btnRentals, btnManageUsers, btnSystemReports);

        // 1. Load the default view and set initial button style
        loadForm("/view/AdminOverview.fxml", btnOverview);

        // 2. Map buttons to load specific forms and update styles
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

            // Update button styles
            updateButtonStyle(selectedButton);

        } catch (IOException e) {
            System.err.println("IOException: Failed to load " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void updateButtonStyle(Button selectedButton) {
        for (Button btn : sidebarButtons) {
            if (btn == selectedButton) {
                // Apply 'Active' style (Purple background, White text)
                btn.setStyle("-fx-background-color: #a855f7; -fx-text-fill: white; -fx-background-radius: 10; " +
                        "-fx-font-weight: bold; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 20; -fx-cursor: hand;");
            } else {
                // Apply 'Inactive' style (Transparent background, Grey/Yellow text)
                String textColor = (btn == btnManageUsers) ? "#fbbf24" : "#94a3b8"; // Keep yellow for Manage Users if needed
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + textColor + "; " +
                        "-fx-font-weight: bold; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 20; -fx-cursor: hand;");
            }
        }
    }

    @FXML
    public void logoutaction(ActionEvent actionEvent) {
        try {
            // 1. Load the Login Screen FXML
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            Scene loginScene = new Scene(root);

            // 2. Find the current window (Stage) using the button that was clicked
            Stage currentStage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();

            // 3. Swap the content (Scene) of the current window
            currentStage.setScene(loginScene);
            currentStage.setTitle("Library Management System - Login");

            // Ensure the icon is still set (optional, as it usually persists)
            try {
                currentStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/book.png")));
            } catch (Exception e) {
                System.out.println("Icon not found, keeping existing icon.");
            }

            // 4. Recenter the window (helpful if the login screen is a different size than the admin dashboard)
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