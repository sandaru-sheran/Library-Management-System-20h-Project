package service;

import model.CustomerTM;
import javafx.collections.ObservableList;
import java.sql.SQLException;

public interface AdminCustomerBaseService {
    ObservableList<CustomerTM> getAllCustomers() throws SQLException;
    ObservableList<CustomerTM> filterCustomers(ObservableList<CustomerTM> customerList, String keyword);
}