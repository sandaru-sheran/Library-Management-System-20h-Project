package controller;

import dto.UserDTO;
import factory.ServiceFactory;
import service.UserService;
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
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML public Label errorMasageLable;
    @FXML private PasswordField passwordTextField;
    @FXML private TextField usernameTExtField;

    private UserService userService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userService = ServiceFactory.getInstance().getService(UserService.class);
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

        try {
            UserDTO user = userService.login(inputUser, inputPass);
            if (user != null) {
                if (user.getRole().equals("Admin")) {
                    navigateTo("/view/AdminMainView.fxml");
                } else if (user.getRole().equals("Librarian")) {
                    navigateTo("/view/StaffMainView.fxml");
                }
            } else {
                errorMasageLable.setText("Invalid username or password. Please try again.");
                passwordTextField.clear();
            }
        } catch (SQLException e) {
            errorMasageLable.setText("Cannot login. Database disconnected.");
            System.err.println("Database is offline. Redirecting to DB Setup...");
            Platform.runLater(() -> {
                try {
                    navigateTo("/view/DBSetupView.fxml");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateTo(String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) usernameTExtField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setFullScreen(true);
        stage.show();
    }

    @FXML
    public void closeApp(ActionEvent actionEvent) {
        System.exit(0);
    }
}