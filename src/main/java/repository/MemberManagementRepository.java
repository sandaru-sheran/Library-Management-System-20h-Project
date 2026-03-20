package repository;

import model.Customer;
import java.sql.SQLException;
import java.util.List;

public interface MemberManagementRepository {
    List<Customer> findAll() throws SQLException;
    String getNextCustomerId() throws SQLException;
    void save(Customer customer) throws SQLException;
    void update(Customer customer) throws SQLException;
    void deleteById(String customerId) throws SQLException;
}