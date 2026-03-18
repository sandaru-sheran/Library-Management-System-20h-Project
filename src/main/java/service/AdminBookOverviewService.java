package service;

import dto.BookDTO;
import javafx.collections.ObservableList;
import java.sql.SQLException;

public interface AdminBookOverviewService {
    ObservableList<BookDTO> getAllBooks() throws SQLException;
    ObservableList<BookDTO> filterBooks(ObservableList<BookDTO> bookList, String keyword);
}