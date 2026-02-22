package controller;

import Model.RentalTM;
import db.DBConnection;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class RentalManagementController implements Initializable {

    @FXML private TextField txtRentalCustId;
    @FXML private TextField txtRentalBookId;
    @FXML private DatePicker dtDueDate;
    @FXML private TextField txtSearchRentals;

    @FXML private TableView<RentalTM> tblRentals;
    @FXML private TableColumn<RentalTM, String> colRentalId;
    @FXML private TableColumn<RentalTM, String> colRentalBookId;
    @FXML private TableColumn<RentalTM, String> colRentalCustId;
    @FXML private TableColumn<RentalTM, String> colIssueDate;
    @FXML private TableColumn<RentalTM, String> colDueDate;
    @FXML private TableColumn<RentalTM, String> colStatus;
    @FXML private TableColumn<RentalTM, Double> colFine;

    private final ObservableList<RentalTM> rentalList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Map Columns to RentalTM Properties
        colRentalId.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        colRentalBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colRentalCustId.setCellValueFactory(new PropertyValueFactory<>("custId"));
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colFine.setCellValueFactory(new PropertyValueFactory<>("fine"));

        // 2. Load the Table Data
        loadAllRentals();

        // 3. SEARCH BAR LOGIC
        FilteredList<RentalTM> filteredData = new FilteredList<>(rentalList, r -> true);
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

        SortedList<RentalTM> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblRentals.comparatorProperty());
        tblRentals.setItems(sortedData);
    }

    private void loadAllRentals() {
        rentalList.clear();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();

            // We use a JOIN with your awesome Live_Rentals view to get the real-time status and fine!
            String sql = "SELECT r.rental_id, r.book_id, r.cust_id, r.issue_date, r.due_date, " +
                    "v.live_status, v.live_fine " +
                    "FROM Rentals r " +
                    "JOIN Live_Rentals v ON r.rental_id = v.rental_id " +
                    "ORDER BY r.issue_date DESC";

            ResultSet rs = stm.executeQuery(sql);

            while (rs.next()) {
                rentalList.add(new RentalTM(
                        rs.getString("rental_id"),
                        rs.getString("book_id"),
                        rs.getString("cust_id"),
                        rs.getString("issue_date"),
                        rs.getString("due_date"),
                        rs.getString("live_status"),
                        rs.getDouble("live_fine")
                ));
            }
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

        if (custId.isEmpty() || bookId.isEmpty() || dueDate == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Data", "Please fill in Customer ID, Book ID, and Due Date.");
            return;
        }

        if (dueDate.isBefore(LocalDate.now()) || dueDate.isEqual(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING, "Invalid Date", "Due date must be in the future.");
            return;
        }

        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false); // START TRANSACTION

            // 1. Verify Book exists and check quantity
            PreparedStatement checkBook = connection.prepareStatement("SELECT qty, title FROM Books WHERE book_id = ?");
            checkBook.setString(1, bookId);
            ResultSet rsBook = checkBook.executeQuery();

            if (!rsBook.next()) {
                showAlert(Alert.AlertType.ERROR, "Not Found", "Book ID does not exist.");
                connection.rollback();
                return;
            }
            if (rsBook.getInt("qty") <= 0) {
                showAlert(Alert.AlertType.WARNING, "Out of Stock", "The book '" + rsBook.getString("title") + "' is currently unavailable.");
                connection.rollback();
                return;
            }

            // 2. Verify Customer exists
            PreparedStatement checkCust = connection.prepareStatement("SELECT name FROM Customers WHERE cust_id = ?");
            checkCust.setString(1, custId);
            ResultSet rsCust = checkCust.executeQuery();
            if (!rsCust.next()) {
                showAlert(Alert.AlertType.ERROR, "Not Found", "Customer ID does not exist.");
                connection.rollback();
                return;
            }

            // 3. Generate new Rental ID
            ResultSet rsId = connection.createStatement().executeQuery("SELECT rental_id FROM Rentals ORDER BY rental_id DESC LIMIT 1");
            String newRentalId = "R001";
            if (rsId.next()) {
                int idNum = Integer.parseInt(rsId.getString("rental_id").substring(1)) + 1;
                newRentalId = String.format("R%03d", idNum);
            }

            // 4. Insert the Rental Record
            String insertSql = "INSERT INTO Rentals (rental_id, book_id, cust_id, issue_date, due_date, status) VALUES (?, ?, ?, CURRENT_DATE, ?, 'Pending')";
            PreparedStatement insertRental = connection.prepareStatement(insertSql);
            insertRental.setString(1, newRentalId);
            insertRental.setString(2, bookId);
            insertRental.setString(3, custId);
            insertRental.setDate(4, java.sql.Date.valueOf(dueDate));
            insertRental.executeUpdate();

            // 5. Update the Book Quantity (-1)
            PreparedStatement updateBook = connection.prepareStatement("UPDATE Books SET qty = qty - 1 WHERE book_id = ?");
            updateBook.setString(1, bookId);
            updateBook.executeUpdate();

            connection.commit(); // CONFIRM TRANSACTION

            showAlert(Alert.AlertType.INFORMATION, "Success", "Book successfully issued to " + rsCust.getString("name"));
            clearFields();
            loadAllRentals();

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (connection != null) connection.rollback(); // CANCEL TRANSACTION ON ERROR
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            showAlert(Alert.AlertType.ERROR, "System Error", "Failed to issue the book.");
        } finally {
            try {
                if (connection != null) connection.setAutoCommit(true); // RESET
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void returnBookOnAction(ActionEvent event) {
        RentalTM selectedRental = tblRentals.getSelectionModel().getSelectedItem();

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
            Connection connection = null;
            try {
                connection = DBConnection.getInstance().getConnection();
                connection.setAutoCommit(false); // START TRANSACTION

                // 1. Set the return_date to today.
                // NOTE: Your SQL Trigger 'Calculate_Fine_On_Return' will automatically calculate fines and set status to 'Returned'!
                PreparedStatement returnStmt = connection.prepareStatement("UPDATE Rentals SET return_date = CURRENT_DATE WHERE rental_id = ?");
                returnStmt.setString(1, selectedRental.getRentalId());
                returnStmt.executeUpdate();

                // 2. Add the book back to inventory (+1 qty)
                PreparedStatement updateBook = connection.prepareStatement("UPDATE Books SET qty = qty + 1 WHERE book_id = ?");
                updateBook.setString(1, selectedRental.getBookId());
                updateBook.executeUpdate();

                connection.commit(); // CONFIRM TRANSACTION

                showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully. Fines updated if applicable.");
                loadAllRentals();

            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    if (connection != null) connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                showAlert(Alert.AlertType.ERROR, "System Error", "Failed to process the return.");
            } finally {
                try {
                    if (connection != null) connection.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void clearFields() {
        txtRentalCustId.clear();
        txtRentalBookId.clear();
        dtDueDate.setValue(null);
        tblRentals.getSelectionModel().clearSelection();
    }

    // --- CUSTOM DARK NOTIFICATION UI ---
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        String accentColor = "#10b981"; // Success Green
        String symbol = "✔";

        if (alertType == Alert.AlertType.ERROR) {
            accentColor = "#ef4444"; // Red
            symbol = "✖";
        } else if (alertType == Alert.AlertType.WARNING) {
            accentColor = "#f59e0b"; // Yellow
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

    // --- CUSTOM DARK CONFIRMATION MODAL ---
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
                        "-fx-border-color: #3b82f6; " + // Blue border for return confirmation
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
        // 1. Get the selected rental from the table
        RentalTM selectedRental = tblRentals.getSelectionModel().getSelectedItem();

        if (selectedRental == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a rental record from the table to process payment.");
            return;
        }

        // 2. Check if there is actually a fine to pay
        if (selectedRental.getFine() <= 0) {
            showAlert(Alert.AlertType.INFORMATION, "No Fine", "There are no pending fines for this rental.");
            return;
        }

        // 3. Confirm payment with the user
        boolean confirm = showDarkConfirmation(
                "Confirm Payment",
                "Has the customer paid the fine of " + selectedRental.getFine() + " LKR for Rental ID: " + selectedRental.getRentalId() + "?"
        );

        if (confirm) {
            try {
                Connection connection = DBConnection.getInstance().getConnection();

                // 4. Verify it hasn't already been paid (Since RentalTM doesn't store payment status)
                PreparedStatement checkStatus = connection.prepareStatement("SELECT payment_status FROM Rentals WHERE rental_id = ?");
                checkStatus.setString(1, selectedRental.getRentalId());
                ResultSet rs = checkStatus.executeQuery();

                if (rs.next() && "Paid".equalsIgnoreCase(rs.getString("payment_status"))) {
                    showAlert(Alert.AlertType.WARNING, "Already Paid", "This fine has already been settled.");
                    return;
                }

                // 5. Update the payment status in the database
                String sql = "UPDATE Rentals SET payment_status = 'Paid' WHERE rental_id = ?";
                PreparedStatement pstm = connection.prepareStatement(sql);
                pstm.setString(1, selectedRental.getRentalId());

                if (pstm.executeUpdate() > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Payment Successful", "The fine has been successfully marked as Paid.");
                    loadAllRentals(); // Refresh the table to reflect any changes
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to process the payment.");
            }
        }
    }
}