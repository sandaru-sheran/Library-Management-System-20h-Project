package repository.impl;

import db.DBConnection;
import model.Rental;
import repository.AdminRentalLogsRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminRentalLogsRepositoryImpl implements AdminRentalLogsRepository {

    @Override
    public List<Rental> getRentalLogs() throws SQLException {
        List<Rental> logList = new ArrayList<>();
        String sql = "SELECT rental_id, book_id, cust_id, issue_date, due_date, return_date, status, fine, payment_status FROM Rentals ORDER BY issue_date DESC";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet resultSet = pstm.executeQuery()) {

            while (resultSet.next()) {
                logList.add(new Rental(
                        resultSet.getString("rental_id"),
                        resultSet.getString("book_id"),
                        resultSet.getString("cust_id"),
                        resultSet.getDate("issue_date").toLocalDate(),
                        resultSet.getDate("due_date").toLocalDate(),
                        resultSet.getDate("return_date") != null ? resultSet.getDate("return_date").toLocalDate() : null,
                        resultSet.getString("status"),
                        resultSet.getDouble("fine"),
                        resultSet.getString("payment_status")
                ));
            }
        }
        return logList;
    }
}