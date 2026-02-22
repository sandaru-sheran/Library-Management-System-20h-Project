package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

public class AdminOverviewController implements Initializable {

    // --- Dashboard Metrics Labels ---
    @FXML private Label lblTotalUsers;
    @FXML private Label lblTotalPaid;
    @FXML private Label lblTotalUnpaid;
    @FXML private Label lblUnreturned;

    // --- Daily Fine Update Controls ---
    @FXML private TextField txtFinePerDay;
    @FXML private Label lblFineUpdateStatus;

    // --- Table Controls ---
    @FXML private TableView<RecentRentalTM> tblRecentRentals;
    @FXML private TableColumn<RecentRentalTM, String> colDate;
    @FXML private TableColumn<RecentRentalTM, String> colCustomerName;
    @FXML private TableColumn<RecentRentalTM, String> colBookTitle;
    @FXML private TableColumn<RecentRentalTM, String> colStatus;

    private ObservableList<RecentRentalTM> rentalList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Map columns
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadDashboardMetrics();
        loadRecentRentals();
    }

    private void loadDashboardMetrics() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement statement = connection.createStatement();

            ResultSet rsUsers = statement.executeQuery("SELECT COUNT(*) FROM Customers");
            if (rsUsers.next()) { // MUST call rs.next() before reading data!
                lblTotalUsers.setText(String.valueOf(rsUsers.getInt(1))); // Convert int to String
            }

            ResultSet rsUnreturned = statement.executeQuery("SELECT COUNT(*) FROM Rentals WHERE return_date IS NULL");
            if (rsUnreturned.next()) {
                lblUnreturned.setText(String.valueOf(rsUnreturned.getInt(1)));
            }

            ResultSet rsPaid = statement.executeQuery("SELECT SUM(fine) FROM Rentals WHERE payment_status = 'Paid'");
            if (rsPaid.next()) {
                double paid = rsPaid.getDouble(1);
                lblTotalPaid.setText(String.format("%.2f", paid));
            }

            ResultSet rsUnpaid = statement.executeQuery("SELECT SUM(fine) FROM Rentals WHERE payment_status = 'Pending'");
            if (rsUnpaid.next()) {
                double unpaid = rsUnpaid.getDouble(1);
                lblTotalUnpaid.setText(String.format("%.2f", unpaid));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error loading dashboard metrics from database!");
        }
    }
    @FXML
    void saveFineOnAction(ActionEvent event) {
        String newFineText = txtFinePerDay.getText();

        try {

            double newFineRate = Double.parseDouble(newFineText);

            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "UPDATE Settings SET fine_per_day = " + newFineRate + " WHERE setting_id = 1";
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);

            lblFineUpdateStatus.setText("Updated: " + newFineRate + " LKR");
            lblFineUpdateStatus.setStyle("-fx-text-fill: #10b981;");

        } catch (NumberFormatException e) {

            lblFineUpdateStatus.setText("Enter a valid number!");
            lblFineUpdateStatus.setStyle("-fx-text-fill: #ef4444;"); // Red
        } catch (SQLException e) {
            e.printStackTrace();
            lblFineUpdateStatus.setText("Database Error!");
        }

        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
        pause.setOnFinished(e -> lblFineUpdateStatus.setText(""));
        pause.play();
    }

    private void loadRecentRentals() {
        // 1. Clear the list first so we don't duplicate data if reloaded
        rentalList.clear();

        // 2. The SQL Query using JOINs to get the actual names and titles
        String sql = "SELECT r.issue_date, c.name, b.title, r.status " +
                "FROM Rentals r " +
                "JOIN Customers c ON r.cust_id = c.cust_id " +
                "JOIN Books b ON r.book_id = b.book_id " +
                "ORDER BY r.issue_date DESC LIMIT 15"; // Fetch the 15 most recent

        try {
            // 3. Connect and execute
            Connection connection = DBConnection.getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            // 4. Loop through the results and populate your ObservableList
            while (resultSet.next()) {
                RecentRentalTM rental = new RecentRentalTM(
                        resultSet.getString("issue_date"), // Matches r.issue_date
                        resultSet.getString("name"),       // Matches c.name
                        resultSet.getString("title"),      // Matches b.title
                        resultSet.getString("status")      // Matches r.status
                );
                rentalList.add(rental);
            }

            // 5. Apply the data to the TableView
            tblRecentRentals.setItems(rentalList);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error while loading recent rentals!");
        }
    }
}