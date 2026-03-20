package service;

import dto.tm.CustomerTM;
import javafx.collections.ObservableList;
import java.sql.SQLException;

public interface MemberManagementService {
    ObservableList<CustomerTM> getAllCustomers() throws SQLException;
    String getNextCustomerId() throws SQLException;
    void saveCustomer(CustomerTM customerTM) throws SQLException;
    void updateCustomer(CustomerTM customerTM) throws SQLException;
    void deleteCustomer(String customerId) throws SQLException;
    void validateCustomer(CustomerTM customerTM) throws IllegalArgumentException;
}