package service.impl;

import dto.RentalDTO;
import factory.RepositoryFactory;
import model.Rental;
import repository.RentalManagementRepository;
import service.RentalManagementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class RentalManagementServiceImpl implements RentalManagementService {

    private final RentalManagementRepository rentalManagementRepository;

    public RentalManagementServiceImpl() {
        this.rentalManagementRepository = RepositoryFactory.getInstance().getRepository(RentalManagementRepository.class);
    }

    @Override
    public ObservableList<RentalDTO> getAllRentals() throws SQLException {
        return FXCollections.observableArrayList(
                rentalManagementRepository.findAll().stream()
                        .map(this::mapToRentalDTO)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void issueBook(String custId, String bookId, LocalDate dueDate) throws SQLException {
        validateRental(custId, bookId, dueDate);
        rentalManagementRepository.issueBook(custId, bookId, dueDate);
    }

    @Override
    public void returnBook(String rentalId, String bookId) throws SQLException {
        rentalManagementRepository.returnBook(rentalId, bookId);
    }

    @Override
    public void payFine(String rentalId) throws SQLException {
        rentalManagementRepository.payFine(rentalId);
    }

    @Override
    public void validateRental(String custId, String bookId, LocalDate dueDate) throws IllegalArgumentException {
        if (custId.isEmpty() || bookId.isEmpty() || dueDate == null) {
            throw new IllegalArgumentException("Please fill in Customer ID, Book ID, and Due Date.");
        }
        if (dueDate.isBefore(LocalDate.now()) || dueDate.isEqual(LocalDate.now())) {
            throw new IllegalArgumentException("Due date must be in the future.");
        }
    }

    private RentalDTO mapToRentalDTO(Rental rental) {
        return new RentalDTO(
                rental.getId(),
                rental.getBookId(),
                rental.getCustomerId(),
                rental.getIssueDate().toString(),
                rental.getDueDate() != null ? rental.getDueDate().toString() : "Not Returned",
                rental.getStatus(),
                rental.getFine()
        );
    }
}