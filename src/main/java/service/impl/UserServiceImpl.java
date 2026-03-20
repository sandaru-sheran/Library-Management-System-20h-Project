package service.impl;

import dto.UserDTO;
import factory.RepositoryFactory;
import model.User;
import repository.UserRepository;
import service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl() {
        this.userRepository = RepositoryFactory.getInstance().getRepository(UserRepository.class);
    }

    @Override
    public ObservableList<UserDTO> getAllUsers() throws SQLException {
        return FXCollections.observableArrayList(
                userRepository.findAll().stream()
                        .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getPassword(), user.getRole()))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public String getNextUserId() throws SQLException {
        return userRepository.getNextUserId();
    }

    @Override
    public void saveUser(UserDTO userDTO) throws SQLException {
        validateUser(userDTO);
        User user = new User(userDTO.getUserId(), userDTO.getUsername(), userDTO.getPassword(), userDTO.getRole());
        userRepository.save(user);
    }

    @Override
    public void updateUser(UserDTO userDTO) throws SQLException {
        validateUser(userDTO);
        User user = new User(userDTO.getUserId(), userDTO.getUsername(), userDTO.getPassword(), userDTO.getRole());
        userRepository.update(user);
    }

    @Override
    public void deleteUser(String userId) throws SQLException {
        userRepository.deleteById(userId);
    }

    @Override
    public UserDTO login(String username, String password) throws SQLException {
        ObservableList<UserDTO> allUsers = getAllUsers();
        for (UserDTO user : allUsers) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void validateUser(UserDTO userDTO) throws IllegalArgumentException {
        if (userDTO.getUserId().isEmpty() || userDTO.getUsername().isEmpty() || userDTO.getPassword().isEmpty() || userDTO.getRole() == null) {
            throw new IllegalArgumentException("Please fill in all fields before saving.");
        }
        if (userDTO.getUsername().length() <= 3) {
            throw new IllegalArgumentException("Username must be longer than 3 characters.");
        }
        if (!userDTO.getUsername().matches("^[A-Za-z].*")) {
            throw new IllegalArgumentException("Username must start with a letter.");
        }
        if (userDTO.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }
    }

    @Override
    public String getNextAvailableUserId() throws SQLException {
        return userRepository.getNextUserId();
    }
}