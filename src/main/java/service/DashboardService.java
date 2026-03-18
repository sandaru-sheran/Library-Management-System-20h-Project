package service;

import dto.TransactionDTO;
import java.sql.SQLException;
import java.util.List;

public interface DashboardService {
    int getTotalBooks() throws SQLException;
    int getTotalMembers() throws SQLException;
    int getOverdueBooks() throws SQLException;
    List<TransactionDTO> getRecentTransactions() throws SQLException;
}