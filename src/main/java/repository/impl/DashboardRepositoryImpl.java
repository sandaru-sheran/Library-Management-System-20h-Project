package repository.impl;

import db.DBConnection;
import model.Transaction;
import repository.DashboardRepository;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DashboardRepositoryImpl implements DashboardRepository {

    @Override
    public int getTotalBooks() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stm = connection.createStatement();
             ResultSet rsBooks = stm.executeQuery("SELECT SUM(qty) FROM Books")) {
            if (rsBooks.next()) {
                return rsBooks.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public int getTotalMembers() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stm = connection.createStatement();
             ResultSet rsMembers = stm.executeQuery("SELECT COUNT(*) FROM Customers")) {
            if (rsMembers.next()) {
                return rsMembers.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public int getOverdueBooks() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stm = connection.createStatement();
             ResultSet rsOverdue = stm.executeQuery("SELECT COUNT(*) FROM Live_Rentals WHERE live_status = 'Overdue'")) {
            if (rsOverdue.next()) {
                return rsOverdue.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public List<Transaction> getRecentTransactions() throws SQLException {
        List<Transaction> transList = new ArrayList<>();
        String sql = "SELECT rental_id, customer_name, book_title, issue_date " +
                "FROM Live_Rentals " +
                "ORDER BY issue_date DESC LIMIT 7";
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stm = connection.createStatement();
             ResultSet rsTrans = stm.executeQuery(sql)) {

            while (rsTrans.next()) {
                transList.add(new Transaction(
                        rsTrans.getString("rental_id"),
                        rsTrans.getString("customer_name"),
                        rsTrans.getString("book_title"),
                        rsTrans.getDate("issue_date").toLocalDate()
                ));
            }
        }
        return transList;
    }
}