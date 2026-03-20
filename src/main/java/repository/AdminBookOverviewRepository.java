package repository;

import model.Book;
import java.sql.SQLException;
import java.util.List;

public interface AdminBookOverviewRepository {
    List<Book> getAllBooks() throws SQLException;
}