package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import Model.*;
import db.DBConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class AdminRentalLogsController implements Initializable {

    @FXML private Label lblTotalTransactions;
    @FXML private TextField txtSearchLogs;
    @FXML private TableView<RentalLogTM> tblRentalLogs;
    @FXML private TableColumn<RentalLogTM, String> colRentalId;
    @FXML private TableColumn<RentalLogTM, String> colBookId;
    @FXML private TableColumn<RentalLogTM, String> colCustId;
    @FXML private TableColumn<RentalLogTM, String> colIssueDate;
    @FXML private TableColumn<RentalLogTM, String> colReturnDate;
    @FXML private TableColumn<RentalLogTM, String> colStatus;
    @FXML private TableColumn<RentalLogTM, Double> colFine;
    @FXML private TableColumn<RentalLogTM, String> colPayment;

    // Master list holding ALL rental logs from the database
    private ObservableList<RentalLogTM> logList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Map TableColumns to the properties in the RentalLogTM model
        colRentalId.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colCustId.setCellValueFactory(new PropertyValueFactory<>("custId"));
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colFine.setCellValueFactory(new PropertyValueFactory<>("fine"));
        colPayment.setCellValueFactory(new PropertyValueFactory<>("payment"));

        // 2. Add a listener to the search TextField for dynamic filtering
        txtSearchLogs.textProperty().addListener((observable, oldValue, newValue) -> {
            filterLogs(newValue);
        });

        // 3. Load initial rental log data
        loadRentalLogs();
    }

    private void loadRentalLogs() {
        logList.clear(); // Clear existing data to prevent duplicates

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement statement = connection.createStatement();

            // Fetching all rentals, sorted with the newest ones at the top
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Rentals ORDER BY issue_date DESC");

            while (resultSet.next()) {
                // Handle NULL return dates cleanly for the UI
                String dbReturnDate = resultSet.getString("return_date");
                if (dbReturnDate == null) {
                    dbReturnDate = "Not Returned";
                }

                RentalLogTM log = new RentalLogTM(
                        resultSet.getString("rental_id"),
                        resultSet.getString("book_id"),
                        resultSet.getString("cust_id"),
                        resultSet.getString("issue_date"),
                        dbReturnDate,
                        resultSet.getString("status"),
                        resultSet.getDouble("fine"),
                        resultSet.getString("payment_status")
                );
                logList.add(log);
            }

            // Set the data to the table and update the label
            tblRentalLogs.setItems(logList);
            updateTotalTransactionsCount();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to load rental logs from database.");
        }
    }

    private void filterLogs(String keyword) {
        // If keyword is empty or null, display the full list
        if (keyword == null || keyword.trim().isEmpty()) {
            tblRentalLogs.setItems(logList);
        } else {
            ObservableList<RentalLogTM> filteredList = FXCollections.observableArrayList();
            String searchWord = keyword.toLowerCase();

            // Loop through the master list and look for matches
            for (RentalLogTM log : logList) {
                if (log.getRentalId().toLowerCase().contains(searchWord) ||
                        log.getBookId().toLowerCase().contains(searchWord) ||
                        log.getCustId().toLowerCase().contains(searchWord) ||
                        log.getStatus().toLowerCase().contains(searchWord)) {

                    filteredList.add(log);
                }
            }
            // Update the table to show ONLY the filtered list
            tblRentalLogs.setItems(filteredList);
        }

        // Ensure the total count updates to reflect the search results
        updateTotalTransactionsCount();
    }

    private void updateTotalTransactionsCount() {
        lblTotalTransactions.setText(String.valueOf(tblRentalLogs.getItems().size()));
    }
}