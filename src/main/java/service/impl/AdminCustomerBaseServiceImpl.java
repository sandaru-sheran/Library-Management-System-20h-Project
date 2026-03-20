package service.impl;

import factory.RepositoryFactory;
import dto.tm.CustomerTM;
import repository.AdminCustomerBaseRepository;
import service.AdminCustomerBaseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public class AdminCustomerBaseServiceImpl implements AdminCustomerBaseService {

    private final AdminCustomerBaseRepository adminCustomerBaseRepository;

    public AdminCustomerBaseServiceImpl() {
        this.adminCustomerBaseRepository = RepositoryFactory.getInstance().getRepository(AdminCustomerBaseRepository.class);
    }

    @Override
    public ObservableList<CustomerTM> getAllCustomers() throws SQLException {
        return adminCustomerBaseRepository.getAllCustomers();
    }

    @Override
    public ObservableList<CustomerTM> filterCustomers(ObservableList<CustomerTM> customerList, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return customerList;
        }
        ObservableList<CustomerTM> filteredList = FXCollections.observableArrayList();
        String searchWord = keyword.toLowerCase();

        for (CustomerTM customer : customerList) {
            if (customer.getCustId().toLowerCase().contains(searchWord) ||
                    customer.getName().toLowerCase().contains(searchWord) ||
                    customer.getEmail().toLowerCase().contains(searchWord) ||
                    customer.getContact().toLowerCase().contains(searchWord)) {
                filteredList.add(customer);
            }
        }
        return filteredList;
    }
}