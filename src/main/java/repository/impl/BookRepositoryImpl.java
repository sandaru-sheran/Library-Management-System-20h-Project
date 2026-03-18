package repository.impl;

import db.DBConnection;
import domain.Book;
import repository.BookRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookRepositoryImpl implements BookRepository {

    @Override
    public List<Book> findAll() throws SQLException {
        List<Book> bookList = new ArrayList<>();
        String sql = "SELECT * FROM Books";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                bookList.add(new Book(
                        rs.getString("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getInt("qty")
                ));
            }
        }
        return bookList;
    }

    @Override
    public String getNextBookId() throws SQLException {
        String sql = "SELECT book_id FROM Books ORDER BY book_id DESC LIMIT 1";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            if (rs.next()) {
                int idNum = Integer.parseInt(rs.getString("book_id").substring(1)) + 1;
                return String.format("B%03d", idNum);
            } else {
                return "B001";
            }
        }
    }

    @Override
    public void save(Book book) throws SQLException {
        String sql = "INSERT INTO Books (book_id, title, author, category, qty) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, book.getId());
            pstm.setString(2, book.getTitle());
            pstm.setString(3, book.getAuthor());
            pstm.setString(4, book.getCategory());
            pstm.setInt(5, book.getQuantity());
            pstm.executeUpdate();
        }
    }

    @Override
    public void update(Book book) throws SQLException {
        String sql = "UPDATE Books SET title=?, author=?, category=?, qty=? WHERE book_id=?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, book.getTitle());
            pstm.setString(2, book.getAuthor());
            pstm.setString(3, book.getCategory());
            pstm.setInt(4, book.getQuantity());
            pstm.setString(5, book.getId());
            pstm.executeUpdate();
        }
    }

    @Override
    public void deleteById(String bookId) throws SQLException {
        String sql = "DELETE FROM Books WHERE book_id=?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, bookId);
            pstm.executeUpdate();
        }
    }
}