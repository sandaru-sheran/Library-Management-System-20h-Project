package repository.impl;

import db.DBConnection;
import model.Rental;
import repository.RentalManagementRepository;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RentalManagementRepositoryImpl implements RentalManagementRepository {

    @Override
    public List<Rental> findAll() throws SQLException {
        List<Rental> rentalList = new ArrayList<>();
        String sql = "SELECT * FROM Rentals ORDER BY issue_date DESC";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                rentalList.add(new Rental(
                        rs.getString("rental_id"),
                        rs.getString("book_id"),
                        rs.getString("cust_id"),
                        rs.getDate("issue_date").toLocalDate(),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null,
                        rs.getString("status"),
                        rs.getDouble("fine"),
                        rs.getString("payment_status")
                ));
            }
        }
        return rentalList;
    }

    @Override
    public void issueBook(String custId, String bookId, LocalDate dueDate) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        connection.setAutoCommit(false);

        try {
            PreparedStatement checkBook = connection.prepareStatement("SELECT qty, title FROM Books WHERE book_id = ?");
            checkBook.setString(1, bookId);
            ResultSet rsBook = checkBook.executeQuery();

            if (!rsBook.next() || rsBook.getInt("qty") <= 0) {
                connection.rollback();
                throw new SQLException("Book not available");
            }

            PreparedStatement checkCust = connection.prepareStatement("SELECT name FROM Customers WHERE cust_id = ?");
            checkCust.setString(1, custId);
            if (!checkCust.executeQuery().next()) {
                connection.rollback();
                throw new SQLException("Customer not found");
            }

            ResultSet rsId = connection.createStatement().executeQuery("SELECT rental_id FROM Rentals ORDER BY rental_id DESC LIMIT 1");
            String newRentalId = "R001";
            if (rsId.next()) {
                int idNum = Integer.parseInt(rsId.getString("rental_id").substring(1)) + 1;
                newRentalId = String.format("R%03d", idNum);
            }

            String insertSql = "INSERT INTO Rentals (rental_id, book_id, cust_id, issue_date, due_date, status) VALUES (?, ?, ?, CURRENT_DATE, ?, 'Pending')";
            PreparedStatement insertRental = connection.prepareStatement(insertSql);
            insertRental.setString(1, newRentalId);
            insertRental.setString(2, bookId);
            insertRental.setString(3, custId);
            insertRental.setDate(4, java.sql.Date.valueOf(dueDate));
            insertRental.executeUpdate();

            PreparedStatement updateBook = connection.prepareStatement("UPDATE Books SET qty = qty - 1 WHERE book_id = ?");
            updateBook.setString(1, bookId);
            updateBook.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void returnBook(String rentalId, String bookId) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        connection.setAutoCommit(false);

        try {
            PreparedStatement returnStmt = connection.prepareStatement("UPDATE Rentals SET return_date = CURRENT_DATE WHERE rental_id = ?");
            returnStmt.setString(1, rentalId);
            returnStmt.executeUpdate();

            PreparedStatement updateBook = connection.prepareStatement("UPDATE Books SET qty = qty + 1 WHERE book_id = ?");
            updateBook.setString(1, bookId);
            updateBook.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void payFine(String rentalId) throws SQLException {
        String sql = "UPDATE Rentals SET payment_status = 'Paid' WHERE rental_id = ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, rentalId);
            pstm.executeUpdate();
        }
    }
}