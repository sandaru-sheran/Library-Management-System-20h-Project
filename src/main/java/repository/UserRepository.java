package repository;

import model.User;
import java.sql.SQLException;
import java.util.List;

public interface UserRepository {
    List<User> findAll() throws SQLException;
    String getNextUserId() throws SQLException;
    void save(User user) throws SQLException;
    void update(User user) throws SQLException;
    void deleteById(String userId) throws SQLException;
}