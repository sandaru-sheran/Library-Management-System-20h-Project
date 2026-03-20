package controller;
import factory.ServiceFactory;
import dto.tm.CustomerTM;
import service.MemberManagementService;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MemberManagementController implements Initializable {

    @FXML private TextField txtSearchCustomer;
    @FXML private TextField txtCustId;
    @FXML private TextField txtCustName;
    @FXML private TextField txtCustAddress;
    @FXML private TextField txtCustContact;
    @FXML private TextField txtCustEmail;

    @FXML private TableView<CustomerTM> tblCustomers;
    @FXML private TableColumn<CustomerTM, String> colCustId;
    @FXML private TableColumn<CustomerTM, String> colCustName;
    @FXML private TableColumn<CustomerTM, String> colCustContact;

    private MemberManagementService memberManagementService;
    private final ObservableList<CustomerTM> customerList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        memberManagementService = ServiceFactory.getInstance().getService(MemberManagementService.class);
        colCustId.setCellValueFactory(new PropertyValueFactory<>("custId"));
        colCustName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCustContact.setCellValueFactory(new PropertyValueFactory<>("contact"));

        txtCustId.setEditable(false);
        txtCustId.setStyle("-fx-text-fill: #94a3b8; -fx-background-color: #0f172a;");

        loadAllCustomers();
        generateNewCustId();

        tblCustomers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillFields(newSelection);
            }
        });

        FilteredList<CustomerTM> filteredData = new FilteredList<>(customerList, c -> true);
        txtSearchCustomer.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(customer -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (customer.getName().toLowerCase().contains(lowerCaseFilter)) return true;
                if (customer.getCustId().toLowerCase().contains(lowerCaseFilter)) return true;
                if (customer.getContact().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });

        SortedList<CustomerTM> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblCustomers.comparatorProperty());
        tblCustomers.setItems(sortedData);
    }

    private void loadAllCustomers() {
        try {
            customerList.setAll(memberManagementService.getAllCustomers());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load customers.");
        }
    }

    private void fillFields(CustomerTM customer) {
        txtCustId.setText(customer.getCustId());
        txtCustName.setText(customer.getName());
        txtCustAddress.setText(customer.getAddress());
        txtCustContact.setText(customer.getContact());
        txtCustEmail.setText(customer.getEmail());
    }

    private void clearFields() {
        txtCustName.clear();
        txtCustAddress.clear();
        txtCustContact.clear();
        txtCustEmail.clear();
        tblCustomers.getSelectionModel().clearSelection();
        generateNewCustId();
    }

    @FXML
    void clearIdOnAction(ActionEvent event) {
        clearFields();
    }

    private void generateNewCustId() {
        try {
            txtCustId.setText(memberManagementService.getNextCustomerId());
        } catch (SQLException e) {
            e.printStackTrace();
            txtCustId.setText("C001");
        }
    }

    @FXML
    void saveCustomerOnAction(ActionEvent event) {
        try {
            CustomerTM customer = new CustomerTM(txtCustId.getText(), txtCustName.getText(), txtCustAddress.getText(), txtCustContact.getText(), txtCustEmail.getText());
            memberManagementService.saveCustomer(customer);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Customer registered successfully!");
            loadAllCustomers();
            clearFields();
        } catch (IllegalArgumentException | SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    void updateCustomerOnAction(ActionEvent event) {
        CustomerTM selectedCust = tblCustomers.getSelectionModel().getSelectedItem();
        if (selectedCust == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a customer from the table to update.");
            return;
        }

        try {
            CustomerTM customer = new CustomerTM(txtCustId.getText(), txtCustName.getText(), txtCustAddress.getText(), txtCustContact.getText(), txtCustEmail.getText());
            memberManagementService.updateCustomer(customer);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Customer updated successfully!");
            loadAllCustomers();
            clearFields();
        } catch (IllegalArgumentException | SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Update Error", e.getMessage());
        }
    }

    @FXML
    void deleteCustomerOnAction(ActionEvent event) {
        CustomerTM selectedCust = tblCustomers.getSelectionModel().getSelectedItem();
        if (selectedCust == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a customer to delete.");
            return;
        }

        boolean confirm = showDarkConfirmation(
                "Confirm Deletion",
                "Are you sure you want to permanently delete customer '" + selectedCust.getName() + "'?"
        );

        if (confirm) {
            try {
                memberManagementService.deleteCustomer(selectedCust.getCustId());
                showAlert(Alert.AlertType.INFORMATION, "Deleted", "Customer has been removed.");
                loadAllCustomers();
                clearFields();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Cannot delete a customer with active rentals.");
            }
        }
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

        javafx.stage.Window window = tblCustomers.getScene().getWindow();
        double x = window.getX() + window.getWidth() - 340;
        double y = window.getY() + window.getHeight() - 110;
        popup.show(window, x, y);

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> popup.hide());
        delay.play();
    }

    private boolean showDarkConfirmation(String title, String message) {
        javafx.stage.Stage dialogStage = new javafx.stage.Stage();

        // --- THE FIX: Grab window and set owner immediately ---
        javafx.stage.Window mainWindow = tblCustomers.getScene().getWindow();
        dialogStage.initOwner(mainWindow);
        // ------------------------------------------------------

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

        dialogStage.setX(mainWindow.getX() + (mainWindow.getWidth() / 2) - 175);
        dialogStage.setY(mainWindow.getY() + (mainWindow.getHeight() / 2) - 90);

        dialogStage.showAndWait();

        return result[0];
    }
}