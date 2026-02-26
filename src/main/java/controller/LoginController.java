package controller;

import Model.UserTM;
import db.DBConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML public Label errorMasageLable;
    @FXML private PasswordField passwordTextField;
    @FXML private TextField usernameTExtField;

    private ArrayList<UserTM> userlist;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Attempt to load users. If the DB is down, this will throw an exception.
        try {
            userlist = getAllUsers();
        } catch (SQLException e) {
            System.err.println("Database is offline. Redirecting to DB Setup...");
            // Platform.runLater ensures the UI finishes loading before we switch scenes
            Platform.runLater(() -> {
                try {
                    navigateTo("/view/DBSetupView.fxml");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    @FXML
    public void login(ActionEvent actionEvent) {
        String inputUser = usernameTExtField.getText();
        String inputPass = passwordTextField.getText();

        errorMasageLable.setText("");
        errorMasageLable.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 11px; -fx-font-weight: bold;");

        if (inputUser.isEmpty() || inputPass.isEmpty()) {
            errorMasageLable.setText("Please enter both username and password.");
            return;
        }

        if (userlist == null) {
            errorMasageLable.setText("Cannot login. Database disconnected.");
            return;
        }

        boolean isFound = false;

        for (UserTM user : userlist) {
            if (user.getUsername().equals(inputUser) && user.getPassword().equals(inputPass)) {
                isFound = true;
                try {
                    if (user.getRole().equals("Admin")) {
                        navigateTo("/view/AdminMainView.fxml");
                    } else if (user.getRole().equals("Librarian")) {
                        navigateTo("/view/StaffMainView.fxml");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        if (!isFound) {
            errorMasageLable.setText("Invalid username or password. Please try again.");
            passwordTextField.clear();
        }
    }

    // Now throws SQLException so the initialize method can catch it and redirect
    public ArrayList<UserTM> getAllUsers() throws SQLException {
        ArrayList<UserTM> list = new ArrayList<>();
        Connection connection = DBConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM Users");

        while (resultSet.next()) {
            list.add(new UserTM(
                    resultSet.getString("user_id"),
                    resultSet.getString("username"),
                    resultSet.getString("password"),
                    resultSet.getString("role")
            ));
        }
        return list;
    }

    private void navigateTo(String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) usernameTExtField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    public void closeApp(ActionEvent actionEvent) {
        System.exit(0);
    }
}