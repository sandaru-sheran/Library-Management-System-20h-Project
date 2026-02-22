package controller;

import Model.UserTM;
import db.DBConnection; // Your database gateway
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UserManagementController implements Initializable {

    @FXML private TextField txtSearchUser;
    @FXML private TextField txtUserId;
    @FXML private TextField txtUsername;
    @FXML private TextField txtPassword;
    @FXML private ComboBox<String> cmbRole;

    @FXML private Button btnSaveUser;
    @FXML private Button btnUpdateUser;
    @FXML private Button btnDeleteUser;

    @FXML private TableView<UserTM> tblUsers;
    @FXML private TableColumn<UserTM, String> colUserId;
    @FXML private TableColumn<UserTM, String> colUsername;
    @FXML private TableColumn<UserTM, String> colRole;

    private final ObservableList<UserTM> userList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Map Table Columns
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        // 2. Add roles to ComboBox
        cmbRole.setItems(FXCollections.observableArrayList("Admin", "Librarian"));

        // 3. Listen for table selection
        tblUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillFields(newVal);
            }
        });

        // 4. Lock the User ID field and generate the first ID
        txtUserId.setEditable(false);
        txtUserId.setStyle("-fx-text-fill: #94a3b8; -fx-background-color: #0f172a;");
        generateNewUserId();

        // 5. Load table data
        loadAllUsers();

        // 6. FORCE the floating notification to use your dark theme CSS
        Platform.runLater(() -> {
            try {
                String css = getClass().getResource("/view/css/style.css").toExternalForm();
                tblUsers.getScene().getStylesheets().add(css);
            } catch (Exception e) {
                System.err.println("Could not load CSS for notifications.");
            }
        });
    }

    private void loadAllUsers() {
        userList.clear();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Users");
            ResultSet resultSet = pstm.executeQuery();

            while (resultSet.next()) {
                userList.add(new UserTM(
                        resultSet.getString("user_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("role")
                ));
            }
            tblUsers.setItems(userList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load users.");
        }
    }

    private void fillFields(UserTM user) {
        txtUserId.setText(user.getUserId());
        txtUsername.setText(user.getUsername());
        txtPassword.setText(user.getPassword());
        cmbRole.setValue(user.getRole());
    }

    private void clearFields() {
        txtUsername.clear();
        txtPassword.clear();
        cmbRole.getSelectionModel().clearSelection();
        tblUsers.getSelectionModel().clearSelection();
        generateNewUserId();
    }

    @FXML
    void saveUserOnAction(ActionEvent event) {
        String userId = txtUserId.getText();
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String role = cmbRole.getValue();

        if (userId.isEmpty() || username.isEmpty() || password.isEmpty() || role == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Data", "Please fill in all fields before saving.");
            return;
        }

        if (username.length() <= 3) {
            showAlert(Alert.AlertType.WARNING, "Invalid Username", "Username must be longer than 3 characters.");
            return;
        }

        if (!username.matches("^[A-Za-z].*")) {
            showAlert(Alert.AlertType.WARNING, "Invalid Username", "Username must start with a letter.");
            return;
        }

        if (password.length() < 8) {
            showAlert(Alert.AlertType.WARNING, "Weak Password", "Password must be at least 8 characters long.");
            return;
        }

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "INSERT INTO Users (user_id, username, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, userId);
            pstm.setString(2, username);
            pstm.setString(3, password);
            pstm.setString(4, role);

            int affectedRows = pstm.executeUpdate();
            if (affectedRows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User " + username + " registered successfully!");
                loadAllUsers();
                clearFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "User ID or Username already exists.");
        }
    }

    @FXML
    void updateUserOnAction(ActionEvent event) {
        UserTM selectedUser = tblUsers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a user from the table to update.");
            return;
        }

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "UPDATE Users SET username = ?, password = ?, role = ? WHERE user_id = ?";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, txtUsername.getText());
            pstm.setString(2, txtPassword.getText());
            pstm.setString(3, cmbRole.getValue());
            pstm.setString(4, txtUserId.getText());

            int affectedRows = pstm.executeUpdate();
            if (affectedRows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User updated successfully!");
                loadAllUsers();
                clearFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Update Error", "Could not update user.");
        }
    }

    private void generateNewUserId() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            ResultSet rs = connection.createStatement().executeQuery("SELECT user_id FROM Users ORDER BY user_id DESC LIMIT 1");

            if (rs.next()) {
                String lastId = rs.getString("user_id");
                int idNum = Integer.parseInt(lastId.substring(1)) + 1;
                txtUserId.setText(String.format("U%03d", idNum));
            } else {
                txtUserId.setText("U001");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            txtUserId.setText("U001");
        }
    }

    // --- NEW: THE DARK DELETION ACTION ---
    @FXML
    void deleteUserOnAction(ActionEvent event) {
        UserTM selectedUser = tblUsers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a user from the table to delete.");
            return;
        }

        // 1. Trigger the dark modal
        boolean confirm = showDarkConfirmation(
                "Confirm Deletion",
                "Are you sure you want to permanently delete user '" + selectedUser.getUsername() + "'?"
        );

        // 2. Only execute if they clicked "Yes"
        if (confirm) {
            try {
                Connection connection = DBConnection.getInstance().getConnection();
                String sql = "DELETE FROM Users WHERE user_id = ?";
                PreparedStatement pstm = connection.prepareStatement(sql);
                pstm.setString(1, selectedUser.getUserId());

                int affectedRows = pstm.executeUpdate();
                if (affectedRows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully!");
                    loadAllUsers();
                    clearFields();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Delete Error", "Could not delete user.");
            }
        }
    }

    // --- NEW: THE CUSTOM DARK CONFIRMATION MODAL ---
    private boolean showDarkConfirmation(String title, String message) {
        javafx.stage.Stage dialogStage = new javafx.stage.Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

        boolean[] result = {false};

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new javafx.geometry.Insets(25));
        root.setStyle(
                "-fx-background-color: #1e293b; " +
                        "-fx-border-color: #ef4444; " +
                        "-fx-border-width: 2; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-radius: 8; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 20, 0, 0, 0);"
        );

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label lblMessage = new Label(message);
        lblMessage.setWrapText(true);
        lblMessage.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px; -fx-alignment: center;");

        Button btnYes = new Button("Yes, Delete");
        btnYes.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 20; -fx-background-radius: 5;");
        btnYes.setOnAction(e -> {
            result[0] = true;
            dialogStage.close();
        });

        Button btnCancel = new Button("Cancel");
        btnCancel.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 20; -fx-background-radius: 5;");
        btnCancel.setOnAction(e -> {
            result[0] = false;
            dialogStage.close();
        });

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(btnCancel, btnYes);

        root.getChildren().addAll(lblTitle, lblMessage, buttonBox);

        javafx.scene.Scene scene = new javafx.scene.Scene(root, 350, 180);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialogStage.setScene(scene);

        // Center it relative to the main window
        javafx.stage.Window mainWindow = tblUsers.getScene().getWindow();
        dialogStage.setX(mainWindow.getX() + (mainWindow.getWidth() / 2) - 175);
        dialogStage.setY(mainWindow.getY() + (mainWindow.getHeight() / 2) - 90);

        dialogStage.showAndWait();

        return result[0];
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        String accentColor = "#10b981";
        String symbol = "✔";

        if (alertType == Alert.AlertType.ERROR) {
            accentColor = "#ef4444";
            symbol = "✖";
        } else if (alertType == Alert.AlertType.WARNING) {
            accentColor = "#f59e0b";
            symbol = "⚠";
        }

        Label lblIcon = new Label(symbol);
        lblIcon.setStyle("-fx-text-fill: " + accentColor + "; -fx-font-size: 26px; -fx-font-weight: bold;");

        VBox container = new VBox(2);
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");

        Label lblMsg = new Label(message);
        lblMsg.setWrapText(true);
        lblMsg.setMaxWidth(250);
        lblMsg.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");
        container.getChildren().addAll(lblTitle, lblMsg);

        HBox notificationBox = new HBox(15);
        notificationBox.setAlignment(Pos.CENTER_LEFT);
        notificationBox.setPrefWidth(320);
        notificationBox.setPadding(new javafx.geometry.Insets(15));
        notificationBox.setStyle(
                "-fx-background-color: #0f172a; " +
                        "-fx-border-color: " + accentColor + "; " +
                        "-fx-border-width: 0 0 0 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-border-radius: 0 5 5 0; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 15, 0, 0, 8);"
        );
        notificationBox.getChildren().addAll(lblIcon, container);

        Popup popup = new Popup();
        popup.getContent().add(notificationBox);
        popup.setAutoHide(true);

        javafx.stage.Window window = tblUsers.getScene().getWindow();
        double x = window.getX() + window.getWidth() - 340;
        double y = window.getY() + window.getHeight() - 110;

        popup.show(window, x, y);

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> popup.hide());
        delay.play();
    }

    public void clearIdOnAction(ActionEvent actionEvent) {
        clearFields();
    }
}