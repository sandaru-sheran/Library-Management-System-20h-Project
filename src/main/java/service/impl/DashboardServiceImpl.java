package service.impl;

import dto.TransactionDTO;
import model.Transaction;
import repository.DashboardRepository;
import repository.impl.DashboardRepositoryImpl;
import service.DashboardService;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository dashboardRepository;

    public DashboardServiceImpl() {
        this.dashboardRepository = new DashboardRepositoryImpl();
    }

    @Override
    public int getTotalBooks() throws SQLException {
        return dashboardRepository.getTotalBooks();
    }

    @Override
    public int getTotalMembers() throws SQLException {
        return dashboardRepository.getTotalMembers();
    }

    @Override
    public int getOverdueBooks() throws SQLException {
        return dashboardRepository.getOverdueBooks();
    }

    @Override
    public List<TransactionDTO> getRecentTransactions() throws SQLException {
        return dashboardRepository.getRecentTransactions().stream()
                .map(this::mapToTransactionDTO)
                .collect(Collectors.toList());
    }

    private TransactionDTO mapToTransactionDTO(Transaction transaction) {
        return new TransactionDTO(
                transaction.getRentalId(),
                transaction.getCustomerName(),
                transaction.getBookTitle(),
                transaction.getIssueDate().toString()
        );
    }
}