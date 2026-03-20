package controller;
import dto.RentalDTO;
import factory.ServiceFactory;
import service.RentalManagementService;
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
import java.time.LocalDate;
import java.util.ResourceBundle;

public class RentalManagementController implements Initializable {

    @FXML private TextField txtRentalCustId;
    @FXML private TextField txtRentalBookId;
    @FXML private DatePicker dtDueDate;
    @FXML private TextField txtSearchRentals;

    @FXML private TableView<RentalDTO> tblRentals;
    @FXML private TableColumn<RentalDTO, String> colRentalId;
    @FXML private TableColumn<RentalDTO, String> colRentalBookId;
    @FXML private TableColumn<RentalDTO, String> colRentalCustId;
    @FXML private TableColumn<RentalDTO, String> colIssueDate;
    @FXML private TableColumn<RentalDTO, String> colDueDate;
    @FXML private TableColumn<RentalDTO, String> colStatus;
    @FXML private TableColumn<RentalDTO, Double> colFine;

    private RentalManagementService rentalManagementService;
    private final ObservableList<RentalDTO> rentalList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rentalManagementService = ServiceFactory.getInstance().getService(RentalManagementService.class);
        colRentalId.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        colRentalBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colRentalCustId.setCellValueFactory(new PropertyValueFactory<>("custId"));
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colFine.setCellValueFactory(new PropertyValueFactory<>("fine"));

        loadAllRentals();

        FilteredList<RentalDTO> filteredData = new FilteredList<>(rentalList, r -> true);
        txtSearchRentals.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(rental -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (rental.getRentalId().toLowerCase().contains(lowerCaseFilter)) return true;
                if (rental.getBookId().toLowerCase().contains(lowerCaseFilter)) return true;
                if (rental.getCustId().toLowerCase().contains(lowerCaseFilter)) return true;
                if (rental.getStatus().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });

        SortedList<RentalDTO> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblRentals.comparatorProperty());
        tblRentals.setItems(sortedData);
    }

    private void loadAllRentals() {
        try {
            rentalList.setAll(rentalManagementService.getAllRentals());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load rentals.");
        }
    }

    @FXML
    void issueBookOnAction(ActionEvent event) {
        String custId = txtRentalCustId.getText().trim();
        String bookId = txtRentalBookId.getText().trim();
        LocalDate dueDate = dtDueDate.getValue();

        try {
            rentalManagementService.issueBook(custId, bookId, dueDate);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book successfully issued.");
            clearFields();
            loadAllRentals();
        } catch (IllegalArgumentException | SQLException e) {
            showAlert(Alert.AlertType.ERROR, "System Error", e.getMessage());
        }
    }

    @FXML
    void returnBookOnAction(ActionEvent event) {
        RentalDTO selectedRental = tblRentals.getSelectionModel().getSelectedItem();

        if (selectedRental == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a rental record from the table to return.");
            return;
        }

        if (selectedRental.getStatus().equalsIgnoreCase("Returned")) {
            showAlert(Alert.AlertType.WARNING, "Already Returned", "This book has already been returned.");
            return;
        }

        boolean confirm = showDarkConfirmation("Confirm Return", "Are you sure you want to mark Book '" + selectedRental.getBookId() + "' as returned today?");

        if (confirm) {
            try {
                rentalManagementService.returnBook(selectedRental.getRentalId(), selectedRental.getBookId());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully. Fines updated if applicable.");
                loadAllRentals();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "System Error", "Failed to process the return.");
            }
        }
    }

    private void clearFields() {
        txtRentalCustId.clear();
        txtRentalBookId.clear();
        dtDueDate.setValue(null);
        tblRentals.getSelectionModel().clearSelection();
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

        javafx.stage.Window window = tblRentals.getScene().getWindow();
        double x = window.getX() + window.getWidth() - 340;
        double y = window.getY() + window.getHeight() - 110;
        popup.show(window, x, y);

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> popup.hide());
        delay.play();
    }

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
                        "-fx-border-color: #3b82f6; " +
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

        Button btnYes = new Button("Yes, Confirm");
        btnYes.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 20; -fx-background-radius: 5;");
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

        javafx.stage.Window mainWindow = tblRentals.getScene().getWindow();
        dialogStage.setX(mainWindow.getX() + (mainWindow.getWidth() / 2) - 175);
        dialogStage.setY(mainWindow.getY() + (mainWindow.getHeight() / 2) - 90);

        dialogStage.showAndWait();

        return result[0];
    }

    @FXML
    public void payfinebuttonaction(ActionEvent actionEvent) {
        RentalDTO selectedRental = tblRentals.getSelectionModel().getSelectedItem();

        if (selectedRental == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a rental record from the table to process payment.");
            return;
        }

        if (selectedRental.getFine() <= 0) {
            showAlert(Alert.AlertType.INFORMATION, "No Fine", "There are no pending fines for this rental.");
            return;
        }

        boolean confirm = showDarkConfirmation(
                "Confirm Payment",
                "Has the customer paid the fine of " + selectedRental.getFine() + " LKR for Rental ID: " + selectedRental.getRentalId() + "?"
        );

        if (confirm) {
            try {
                rentalManagementService.payFine(selectedRental.getRentalId());
                showAlert(Alert.AlertType.INFORMATION, "Payment Successful", "The fine has been successfully marked as Paid.");
                loadAllRentals();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to process the payment.");
            }
        }
    }
}