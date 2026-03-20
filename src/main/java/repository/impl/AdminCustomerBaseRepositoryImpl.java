package repository.impl;

import db.DBConnection;
import dto.tm.CustomerTM;
import repository.AdminCustomerBaseRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdminCustomerBaseRepositoryImpl implements AdminCustomerBaseRepository {

    @Override
    public ObservableList<CustomerTM> getAllCustomers() throws SQLException {
        ObservableList<CustomerTM> customerList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Customers";
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                CustomerTM customer = new CustomerTM(
                        resultSet.getString("cust_id"),
                        resultSet.getString("name"),
                        resultSet.getString("address"),
                        resultSet.getString("contact"),
                        resultSet.getString("email")
                );
                customerList.add(customer);
            }
        }
        return customerList;
    }
}