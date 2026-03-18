package controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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
                btnClose.getStyleClass().add("close-button-hover");
            });
            btnClose.setOnMouseExited(e -> {
                btnClose.getStyleClass().remove("close-button-hover");
            });
        }
        sidebarButtons = Arrays.asList(btnDashboard, btnBooks, btnMembers, btnBorrow);

        // 1. Load default dashboard
        loadForm("/view/DashboardFormView.fxml", btnDashboard);

        // 2. Map buttons to load specific forms
        btnDashboard.setOnAction(e -> loadForm("/view/DashboardFormView.fxml", btnDashboard));
        btnBooks.setOnAction(e -> loadForm("/view/BookManagementFormView.fxml", btnBooks));
        btnMembers.setOnAction(e -> loadForm("/view/MemberManagementFormView.fxml", btnMembers));
        btnBorrow.setOnAction(e -> loadForm("/view/RentalManagementFormView.fxml", btnBorrow));
    }

    private void loadForm(String fxmlPath, Button selectedButton) {
        try {
            URL resource = getClass().getResource(fxmlPath);

            if (resource == null) {
                System.err.println("CRITICAL ERROR: FXML not found at: " + fxmlPath);
                return;
            }

            Parent root = FXMLLoader.load(resource);
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);

            updateButtonStyle(selectedButton);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void updateButtonStyle(Button selectedButton) {
        for (Button btn : sidebarButtons) {
            if (btn == selectedButton) {
                btn.getStyleClass().add("staff-sidebar-button-active");
            } else {
                btn.getStyleClass().remove("staff-sidebar-button-active");
            }
        }
    }

    public void logoutaction(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            Scene loginScene = new Scene(root);

            Stage currentStage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();

            currentStage.setScene(loginScene);
            currentStage.setTitle("Library Management System - Login");

            try {
                currentStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/book.png")));
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