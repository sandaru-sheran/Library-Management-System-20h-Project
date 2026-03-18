package service;

import dto.UserDTO;
import javafx.collections.ObservableList;
import java.sql.SQLException;

public interface UserService {
    ObservableList<UserDTO> getAllUsers() throws SQLException;
    String getNextUserId() throws SQLException;
    void saveUser(UserDTO userDTO) throws SQLException;
    void updateUser(UserDTO userDTO) throws SQLException;
    void deleteUser(String userId) throws SQLException;
    UserDTO login(String username, String password) throws SQLException;
    void validateUser(UserDTO userDTO) throws IllegalArgumentException;
    String getNextAvailableUserId() throws SQLException;
}