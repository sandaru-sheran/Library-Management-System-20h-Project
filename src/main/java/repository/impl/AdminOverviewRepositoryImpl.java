package repository.impl;

import db.DBConnection;
import model.Transaction;
import repository.AdminOverviewRepository;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AdminOverviewRepositoryImpl implements AdminOverviewRepository {

    @Override
    public int getTotalUsers() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet rsUsers = statement.executeQuery("SELECT COUNT(*) FROM Customers")) {
            if (rsUsers.next()) {
                return rsUsers.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public int getUnreturnedBooks() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet rsUnreturned = statement.executeQuery("SELECT COUNT(*) FROM Rentals WHERE return_date IS NULL")) {
            if (rsUnreturned.next()) {
                return rsUnreturned.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public double getTotalPaidFines() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet rsPaid = statement.executeQuery("SELECT SUM(fine) FROM Rentals WHERE payment_status = 'Paid'")) {
            if (rsPaid.next()) {
                return rsPaid.getDouble(1);
            }
        }
        return 0.0;
    }

    @Override
    public double getTotalUnpaidFines() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet rsUnpaid = statement.executeQuery("SELECT SUM(fine) FROM Rentals WHERE payment_status = 'Pending'")) {
            if (rsUnpaid.next()) {
                return rsUnpaid.getDouble(1);
            }
        }
        return 0.0;
    }

    @Override
    public void saveFinePerDay(double newFineRate) throws SQLException {
        String sql = "UPDATE Settings SET fine_per_day = ? WHERE setting_id = 1";
        try (Connection connection = DBConnection.getInstance().getConnection();
             java.sql.PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setDouble(1, newFineRate);
            pstm.executeUpdate();
        }
    }

    @Override
    public List<Transaction> getRecentTransactions() throws SQLException {
        List<Transaction> transactionList = new ArrayList<>();
        String sql = "SELECT r.issue_date, c.name, b.title, r.status " +
                "FROM Rentals r " +
                "JOIN Customers c ON r.cust_id = c.cust_id " +
                "JOIN Books b ON r.book_id = b.book_id " +
                "ORDER BY r.issue_date DESC LIMIT 15";

        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Transaction transaction = new Transaction(
                        null, // rentalId is not needed for this view
                        resultSet.getString("name"),
                        resultSet.getString("title"),
                        resultSet.getDate("issue_date").toLocalDate()
                );
                transactionList.add(transaction);
            }
        }
        return transactionList;
    }
}