package service;

import dto.RecentRentalDTO;
import javafx.collections.ObservableList;
import java.sql.SQLException;

public interface AdminOverviewService {
    int getTotalUsers() throws SQLException;
    int getUnreturnedBooks() throws SQLException;
    double getTotalPaidFines() throws SQLException;
    double getTotalUnpaidFines() throws SQLException;
    void saveFinePerDay(double newFineRate) throws SQLException;
    ObservableList<RecentRentalDTO> getRecentRentals() throws SQLException;
}