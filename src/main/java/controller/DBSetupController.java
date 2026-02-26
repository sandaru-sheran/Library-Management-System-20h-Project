package controller;

import db.DBConnection;
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
import java.sql.SQLException;

public class DBSetupController {

    @FXML private TextField txtHost;
    @FXML private TextField txtPort;
    @FXML private TextField txtDbName;
    @FXML private TextField txtDbUser;
    @FXML private PasswordField txtDbPass;
    @FXML private Label lblStatus;

    @FXML
    void testAndConnectOnAction(ActionEvent event) {
        lblStatus.setText("Attempting to connect...");
        lblStatus.setStyle("-fx-text-fill: #3b82f6;"); // Blue for loading

        String host = txtHost.getText().trim();
        String port = txtPort.getText().trim();
        String dbName = txtDbName.getText().trim();
        String user = txtDbUser.getText().trim();
        String pass = txtDbPass.getText().trim();

        if (host.isEmpty() || port.isEmpty() || dbName.isEmpty() || user.isEmpty()) {
            showError("Please fill in all required fields.");
            return;
        }

        try {
            // Send new credentials to DBConnection
            DBConnection.updateCredentials(host, port, dbName, user, pass);

            // If we reach here, connection was successful! Go back to login.
            navigateToLogin(event);

        } catch (SQLException e) {
            showError("Connection Failed. Check credentials and ensure MySQL is running.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showError(String message) {
        lblStatus.setText(message);
        lblStatus.setStyle("-fx-text-fill: #ef4444;"); // Red for error
    }

    private void navigateToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
    }

    @FXML
    void closeApp(ActionEvent event) {
        System.exit(0);
    }
}