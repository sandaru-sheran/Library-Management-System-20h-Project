package repository;

import model.CustomerTM;
import javafx.collections.ObservableList;
import java.sql.SQLException;

public interface AdminCustomerBaseRepository {
    ObservableList<CustomerTM> getAllCustomers() throws SQLException;
}