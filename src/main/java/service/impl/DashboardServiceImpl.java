package service.impl;

import dto.TransactionDTO;
import factory.RepositoryFactory;
import model.Transaction;
import repository.DashboardRepository;
import service.DashboardService;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository dashboardRepository;

    public DashboardServiceImpl() {
        this.dashboardRepository = RepositoryFactory.getInstance().getRepository(DashboardRepository.class);
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