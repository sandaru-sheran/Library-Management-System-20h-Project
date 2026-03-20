package repository;

import dto.tm.CustomerTM;
import javafx.collections.ObservableList;
import java.sql.SQLException;

public interface AdminCustomerBaseRepository {
    ObservableList<CustomerTM> getAllCustomers() throws SQLException;
}