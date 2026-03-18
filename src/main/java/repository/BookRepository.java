package repository;

import domain.Book;
import java.sql.SQLException;
import java.util.List;

public interface BookRepository {
    List<Book> findAll() throws SQLException;
    String getNextBookId() throws SQLException;
    void save(Book book) throws SQLException;
    void update(Book book) throws SQLException;
    void deleteById(String bookId) throws SQLException;
}