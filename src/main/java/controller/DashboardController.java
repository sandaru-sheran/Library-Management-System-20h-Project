package controller;

import Model.TransactionTM;
import db.DBConnection; // Ensure this matches your database connection class
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Label lblTotalBooks;
    @FXML private Label lblTotalMembers;
    @FXML private Label lblOverdueBooks;
    @FXML private TableView<TransactionTM> tblRecentTransactions;
    @FXML private TableColumn<TransactionTM, String> colTransId;
    @FXML private TableColumn<TransactionTM, String> colMemberName;
    @FXML private TableColumn<TransactionTM, String> colBookTitle;
    @FXML private TableColumn<TransactionTM, String> colDate;

    private final ObservableList<TransactionTM> transList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Link table columns to your TransactionTM model properties
        colTransId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMemberName.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        loadDashboardMetrics();
    }



    private void loadDashboardMetrics() {
        transList.clear();

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();

            // 1. Total Books
            ResultSet rsBooks = stm.executeQuery("SELECT SUM(qty) FROM Books");
            if (rsBooks.next()) {
                lblTotalBooks.setText(String.valueOf(rsBooks.getInt(1)));
            }

            // 2. Total Members
            ResultSet rsMembers = stm.executeQuery("SELECT COUNT(*) FROM Customers");
            if (rsMembers.next()) {
                lblTotalMembers.setText(String.valueOf(rsMembers.getInt(1)));
            }

            // 3. Overdue Books (Now using your awesome Live_Rentals view!)
            ResultSet rsOverdue = stm.executeQuery("SELECT COUNT(*) FROM Live_Rentals WHERE live_status = 'Overdue'");
            if (rsOverdue.next()) {
                lblOverdueBooks.setText(String.valueOf(rsOverdue.getInt(1)));
            }

            // 4. Recent Transactions Table (Much cleaner query thanks to the View)
            String sql = "SELECT rental_id, customer_name, book_title, issue_date " +
                    "FROM Live_Rentals " +
                    "ORDER BY issue_date DESC LIMIT 7";

            ResultSet rsTrans = stm.executeQuery(sql);

            while (rsTrans.next()) {
                transList.add(new TransactionTM(
                        rsTrans.getString("rental_id"),
                        rsTrans.getString("customer_name"),
                        rsTrans.getString("book_title"),
                        rsTrans.getString("issue_date")
                ));
            }

            tblRecentTransactions.setItems(transList);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to fetch dashboard metrics.");
            lblTotalBooks.setText("Err");
            lblTotalMembers.setText("Err");
            lblOverdueBooks.setText("Err");
        }
    }
}