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

    private List<Button> sidebarButtons;

    public void initialize() {

        // Fix the Close Button hover effect using inline styles
        if (btnClose != null) {
            String closeDefault = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 0; -fx-border-radius: 0; -fx-cursor: hand;";
            String closeHover = "-fx-background-color: #e81123; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 0; -fx-border-radius: 0; -fx-cursor: hand;";

            btnClose.setOnMouseEntered(e -> btnClose.setStyle(closeHover));
            btnClose.setOnMouseExited(e -> btnClose.setStyle(closeDefault));
        }

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
        // 1. Define the exact inline styles as strings based on your FXML
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-weight: bold; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 20; -fx-cursor: hand;";
        String activeStyle = "-fx-background-color: #a855f7; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-weight: bold; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 20; -fx-cursor: hand;";
        String manageUsersDefaultStyle = "-fx-background-color: transparent; -fx-text-fill: #fbbf24; -fx-font-weight: bold; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 20; -fx-cursor: hand;";

        // 2. Loop through all buttons and reset them to their default, unclicked state
        for (Button btn : sidebarButtons) {
            if (btn == btnManageUsers) {
                btn.setStyle(manageUsersDefaultStyle); // Keeps the text yellow when not active
            } else {
                btn.setStyle(defaultStyle); // Standard grey text for the rest
            }
        }

        // 3. Apply the purple active style only to the button that was just clicked
        if (selectedButton != null) {
            selectedButton.setStyle(activeStyle);
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