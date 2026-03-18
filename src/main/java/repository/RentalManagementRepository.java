package repository;

import model.Rental;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface RentalManagementRepository {
    List<Rental> findAll() throws SQLException;
    void issueBook(String custId, String bookId, LocalDate dueDate) throws SQLException;
    void returnBook(String rentalId, String bookId) throws SQLException;
    void payFine(String rentalId) throws SQLException;
}