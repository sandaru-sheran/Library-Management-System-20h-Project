package repository.impl;

import db.DBConnection;
import model.Customer;
import repository.MemberManagementRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberManagementRepositoryImpl implements MemberManagementRepository {

    @Override
    public List<Customer> findAll() throws SQLException {
        List<Customer> customerList = new ArrayList<>();
        String sql = "SELECT * FROM Customers";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                customerList.add(new Customer(
                        rs.getString("cust_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("contact"),
                        rs.getString("email")
                ));
            }
        }
        return customerList;
    }

    @Override
    public String getNextCustomerId() throws SQLException {
        String sql = "SELECT cust_id FROM Customers ORDER BY cust_id DESC LIMIT 1";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            if (rs.next()) {
                int idNum = Integer.parseInt(rs.getString("cust_id").substring(1)) + 1;
                return String.format("C%03d", idNum);
            } else {
                return "C001";
            }
        }
    }

    @Override
    public void save(Customer customer) throws SQLException {
        String sql = "INSERT INTO Customers (cust_id, name, address, contact, email) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, customer.getId());
            pstm.setString(2, customer.getName());
            pstm.setString(3, customer.getAddress());
            pstm.setString(4, customer.getContact());
            pstm.setString(5, customer.getEmail());
            pstm.executeUpdate();
        }
    }

    @Override
    public void update(Customer customer) throws SQLException {
        String sql = "UPDATE Customers SET name=?, address=?, contact=?, email=? WHERE cust_id=?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, customer.getName());
            pstm.setString(2, customer.getAddress());
            pstm.setString(3, customer.getContact());
            pstm.setString(4, customer.getEmail());
            pstm.setString(5, customer.getId());
            pstm.executeUpdate();
        }
    }

    @Override
    public void deleteById(String customerId) throws SQLException {
        String sql = "DELETE FROM Customers WHERE cust_id=?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, customerId);
            pstm.executeUpdate();
        }
    }
}