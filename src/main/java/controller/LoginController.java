package controller;

import Model.UserTM;
import db.DBConnection; // Ensure this imports the correct path to your Gateway
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class LoginController {

    @FXML
    public Label errorMasageLable;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private TextField usernameTExtField;

    private final ArrayList<UserTM> userlist = getAllUsers();

    @FXML
    public void login(ActionEvent actionEvent) throws IOException {
        String inputUser = usernameTExtField.getText();
        String inputPass = passwordTextField.getText();

        errorMasageLable.setText("");
        errorMasageLable.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 11px; -fx-font-weight: bold;");

        if (inputUser.isEmpty() || inputPass.isEmpty()) {
            errorMasageLable.setText("Please enter both username and password.");
            usernameTExtField.clear();
            passwordTextField.clear();
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
            pause.setOnFinished(e -> errorMasageLable.setText(""));
            pause.play();
            return;
        }

        // 3. Authentication Loop
        boolean isFound = false;

        for (UserTM user : userlist) {
            if (user.getUsername().equals(inputUser) && user.getPassword().equals(inputPass)) {
                isFound = true;

                // Check Role and Navigate
                if (user.getRole().equals("Admin")) {
                    navigateTo("/view/AdminMainView.fxml");
                    return;
                } else if (user.getRole().equals("Librarian")) {
                    navigateTo("/view/StaffMainView.fxml");
                    return;
                }
            }
        }

        if (!isFound) {
            errorMasageLable.setText("Invalid username or password. Please try again.");
            passwordTextField.clear();
        }
    }

    public ArrayList<UserTM> getAllUsers() {
        ArrayList<UserTM> userList = new ArrayList<>();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Users");

            while (resultSet.next()) {
                UserTM user = new UserTM(
                        resultSet.getString("user_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("role")
                );
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // If DB fails, show a critical error on the label
            errorMasageLable.setText("Database Connection Failed!");
            errorMasageLable.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");
        }
        return userList;
    }

    private void navigateTo(String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) usernameTExtField.getScene().getWindow();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    public void closeApp(ActionEvent actionEvent) {
        System.exit(0);
    }
}