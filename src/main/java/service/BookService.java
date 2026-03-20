package service;

import dto.tm.BookTM;
import javafx.collections.ObservableList;
import java.sql.SQLException;

public interface BookService {
    ObservableList<BookTM> getAllBooks() throws SQLException;
    String getNextBookId() throws SQLException;
    void saveBook(BookTM bookTM) throws SQLException;
    void updateBook(BookTM bookTM) throws SQLException;
    void deleteBook(String bookId) throws SQLException;
    void validateBook(BookTM bookTM) throws IllegalArgumentException;
}