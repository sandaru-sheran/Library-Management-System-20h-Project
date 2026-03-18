package repository;

import model.Transaction;
import java.sql.SQLException;
import java.util.List;

public interface AdminOverviewRepository {
    int getTotalUsers() throws SQLException;
    int getUnreturnedBooks() throws SQLException;
    double getTotalPaidFines() throws SQLException;
    double getTotalUnpaidFines() throws SQLException;
    void saveFinePerDay(double newFineRate) throws SQLException;
    List<Transaction> getRecentTransactions() throws SQLException;
}