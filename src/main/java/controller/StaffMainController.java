package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class StaffMainController {

    @FXML private StackPane contentArea;
    @FXML private Button btnDashboard;
    @FXML private Button btnBooks;
    @FXML private Button btnMembers;
    @FXML private Button btnBorrow;
    @FXML private Button btnClose;

    private List<Button> sidebarButtons;

    public void initialize() {


        if (btnClose != null) {

            btnClose.setOnMouseEntered(e -> {
                btnClose.setStyle("-fx-background-color: #e81123; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 0; -fx-border-radius: 0;");
            });
            btnClose.setOnMouseExited(e -> {
                btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 0; -fx-border-radius: 0;");
            });
        }
        sidebarButtons = Arrays.asList(btnDashboard, btnBooks, btnMembers, btnBorrow);

        // 1. Load default dashboard using the exact filename in your view folder [cite: 137, 225]
        loadForm("/view/DashboardFormView.fxml", btnDashboard);

        // 2. Map buttons to the exact filenames found in your directory [cite: 102, 160, 195, 225]
        btnDashboard.setOnAction(e -> loadForm("/view/DashboardFormView.fxml", btnDashboard));
        btnBooks.setOnAction(e -> loadForm("/view/BookManagementFormView.fxml", btnBooks));
        btnMembers.setOnAction(e -> loadForm("/view/MemberManagementFormView.fxml", btnMembers));
        btnBorrow.setOnAction(e -> loadForm("/view/RentalManagementFormView.fxml", btnBorrow));
    }

    private void loadForm(String fxmlPath, Button selectedButton) {
        try {
            URL resource = getClass().getResource(fxmlPath);

            // Safety check to prevent NullPointerException: Location is required
            if (resource == null) {
                System.err.println("CRITICAL ERROR: FXML not found at: " + fxmlPath);
                return;
            }

            Parent root = FXMLLoader.load(resource);
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);

            // Update button styles dynamically
            updateButtonStyle(selectedButton);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void updateButtonStyle(Button selectedButton) {
        for (Button btn : sidebarButtons) {
            if (btn == selectedButton) {
                // Active style: Purple background
                btn.setStyle("-fx-background-color: #a855f7; -fx-text-fill: white; -fx-background-radius: 10; " +
                        "-fx-font-weight: bold; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 20; -fx-cursor: hand;");
            } else {
                // Inactive style: Transparent [cite: 263]
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; " +
                        "-fx-font-weight: bold; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 20; -fx-cursor: hand;");
            }
        }
    }

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