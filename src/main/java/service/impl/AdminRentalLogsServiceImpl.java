package service.impl;

import dto.RentalLogDTO;
import model.Rental;
import repository.AdminRentalLogsRepository;
import repository.impl.AdminRentalLogsRepositoryImpl;
import service.AdminRentalLogsService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.stream.Collectors;

public class AdminRentalLogsServiceImpl implements AdminRentalLogsService {

    private final AdminRentalLogsRepository adminRentalLogsRepository;

    public AdminRentalLogsServiceImpl() {
        this.adminRentalLogsRepository = new AdminRentalLogsRepositoryImpl();
    }

    @Override
    public ObservableList<RentalLogDTO> getRentalLogs() throws SQLException {
        return FXCollections.observableArrayList(
                adminRentalLogsRepository.getRentalLogs().stream()
                        .map(this::mapToRentalLogDTO)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public ObservableList<RentalLogDTO> filterLogs(ObservableList<RentalLogDTO> logList, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return logList;
        }
        ObservableList<RentalLogDTO> filteredList = FXCollections.observableArrayList();
        String searchWord = keyword.toLowerCase();

        for (RentalLogDTO log : logList) {
            if (log.getRentalId().toLowerCase().contains(searchWord) ||
                    log.getBookId().toLowerCase().contains(searchWord) ||
                    log.getCustId().toLowerCase().contains(searchWord) ||
                    log.getStatus().toLowerCase().contains(searchWord)) {
                filteredList.add(log);
            }
        }
        return filteredList;
    }

    private RentalLogDTO mapToRentalLogDTO(Rental rental) {
        String returnDate = rental.getReturnDate() != null ? rental.getReturnDate().toString() : "Not Returned";
        return new RentalLogDTO(
                rental.getId(),
                rental.getBookId(),
                rental.getCustomerId(),
                rental.getIssueDate().toString(),
                returnDate,
                rental.getStatus(),
                rental.getFine(),
                rental.getPaymentStatus()
        );
    }
}