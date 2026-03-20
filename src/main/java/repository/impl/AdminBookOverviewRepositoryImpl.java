package repository.impl;

import db.DBConnection;
import model.Book;
import repository.AdminBookOverviewRepository;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AdminBookOverviewRepositoryImpl implements AdminBookOverviewRepository {

    @Override
    public List<Book> getAllBooks() throws SQLException {
        List<Book> bookList = new ArrayList<>();
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Books")) {

            while (resultSet.next()) {
                bookList.add(new Book(
                        resultSet.getString("book_id"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getInt("qty")
                ));
            }
        }
        return bookList;
    }
}