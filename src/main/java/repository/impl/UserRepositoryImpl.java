package repository.impl;

import db.DBConnection;
import model.User;
import repository.UserRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {

    @Override
    public List<User> findAll() throws SQLException {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM Users";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                userList.add(new User(
                        rs.getString("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                ));
            }
        }
        return userList;
    }

    @Override
    public String getNextUserId() throws SQLException {
        String sql = "SELECT user_id FROM Users ORDER BY user_id DESC LIMIT 1";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            if (rs.next()) {
                int idNum = Integer.parseInt(rs.getString("user_id").substring(1)) + 1;
                return String.format("U%03d", idNum);
            } else {
                return "U001";
            }
        }
    }

    @Override
    public void save(User user) throws SQLException {
        String sql = "INSERT INTO Users (user_id, username, password, role) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, user.getId());
            pstm.setString(2, user.getUsername());
            pstm.setString(3, user.getPassword());
            pstm.setString(4, user.getRole());
            pstm.executeUpdate();
        }
    }

    @Override
    public void update(User user) throws SQLException {
        String sql = "UPDATE Users SET username = ?, password = ?, role = ? WHERE user_id = ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, user.getUsername());
            pstm.setString(2, user.getPassword());
            pstm.setString(3, user.getRole());
            pstm.setString(4, user.getId());
            pstm.executeUpdate();
        }
    }

    @Override
    public void deleteById(String userId) throws SQLException {
        String sql = "DELETE FROM Users WHERE user_id = ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, userId);
            pstm.executeUpdate();
        }
    }
}