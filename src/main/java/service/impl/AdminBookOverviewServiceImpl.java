package service.impl;

import dto.BookDTO;
import domain.Book;
import repository.AdminBookOverviewRepository;
import repository.impl.AdminBookOverviewRepositoryImpl;
import service.AdminBookOverviewService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.stream.Collectors;

public class AdminBookOverviewServiceImpl implements AdminBookOverviewService {

    private final AdminBookOverviewRepository adminBookOverviewRepository;

    public AdminBookOverviewServiceImpl() {
        this.adminBookOverviewRepository = new AdminBookOverviewRepositoryImpl();
    }

    @Override
    public ObservableList<BookDTO> getAllBooks() throws SQLException {
        return FXCollections.observableArrayList(
                adminBookOverviewRepository.getAllBooks().stream()
                        .map(this::mapToBookDTO)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public ObservableList<BookDTO> filterBooks(ObservableList<BookDTO> bookList, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return bookList;
        }
        ObservableList<BookDTO> filteredList = FXCollections.observableArrayList();
        String searchWord = keyword.toLowerCase();

        for (BookDTO book : bookList) {
            if (book.getBookId().toLowerCase().contains(searchWord) ||
                    book.getTitle().toLowerCase().contains(searchWord) ||
                    book.getAuthor().toLowerCase().contains(searchWord) ||
                    book.getCategory().toLowerCase().contains(searchWord)) {
                filteredList.add(book);
            }
        }
        return filteredList;
    }

    private BookDTO mapToBookDTO(Book book) {
        return new BookDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCategory(),
                book.getQuantity()
        );
    }
}