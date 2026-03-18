package repository;

import model.Transaction;
import java.sql.SQLException;
import java.util.List;

public interface DashboardRepository {
    int getTotalBooks() throws SQLException;
    int getTotalMembers() throws SQLException;
    int getOverdueBooks() throws SQLException;
    List<Transaction> getRecentTransactions() throws SQLException;
}