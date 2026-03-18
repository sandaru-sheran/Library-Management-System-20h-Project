package service;

import dto.RentalDTO;
import javafx.collections.ObservableList;
import java.sql.SQLException;
import java.time.LocalDate;

public interface RentalManagementService {
    ObservableList<RentalDTO> getAllRentals() throws SQLException;
    void issueBook(String custId, String bookId, LocalDate dueDate) throws SQLException;
    void returnBook(String rentalId, String bookId) throws SQLException;
    void payFine(String rentalId) throws SQLException;
    void validateRental(String custId, String bookId, LocalDate dueDate) throws IllegalArgumentException;
}