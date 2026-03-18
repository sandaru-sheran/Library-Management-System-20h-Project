package service.impl;

import dto.RecentRentalDTO;
import model.Transaction;
import repository.AdminOverviewRepository;
import repository.impl.AdminOverviewRepositoryImpl;
import service.AdminOverviewService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.stream.Collectors;

public class AdminOverviewServiceImpl implements AdminOverviewService {

    private final AdminOverviewRepository adminOverviewRepository;

    public AdminOverviewServiceImpl() {
        this.adminOverviewRepository = new AdminOverviewRepositoryImpl();
    }

    @Override
    public int getTotalUsers() throws SQLException {
        return adminOverviewRepository.getTotalUsers();
    }

    @Override
    public int getUnreturnedBooks() throws SQLException {
        return adminOverviewRepository.getUnreturnedBooks();
    }

    @Override
    public double getTotalPaidFines() throws SQLException {
        return adminOverviewRepository.getTotalPaidFines();
    }

    @Override
    public double getTotalUnpaidFines() throws SQLException {
        return adminOverviewRepository.getTotalUnpaidFines();
    }

    @Override
    public void saveFinePerDay(double newFineRate) throws SQLException {
        adminOverviewRepository.saveFinePerDay(newFineRate);
    }

    @Override
    public ObservableList<RecentRentalDTO> getRecentRentals() throws SQLException {
        return FXCollections.observableArrayList(
                adminOverviewRepository.getRecentTransactions().stream()
                        .map(this::mapToRecentRentalDTO)
                        .collect(Collectors.toList())
        );
    }

    private RecentRentalDTO mapToRecentRentalDTO(Transaction transaction) {
        return new RecentRentalDTO(
                transaction.getIssueDate().toString(),
                transaction.getCustomerName(),
                transaction.getBookTitle(),
                null // Status is not part of the Transaction model
        );
    }
}