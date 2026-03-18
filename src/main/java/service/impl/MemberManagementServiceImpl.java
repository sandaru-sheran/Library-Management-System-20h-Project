package service.impl;

import model.CustomerTM;
import domain.Customer;
import repository.MemberManagementRepository;
import repository.impl.MemberManagementRepositoryImpl;
import service.MemberManagementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.stream.Collectors;

public class MemberManagementServiceImpl implements MemberManagementService {

    private final MemberManagementRepository memberManagementRepository;

    public MemberManagementServiceImpl() {
        this.memberManagementRepository = new MemberManagementRepositoryImpl();
    }

    @Override
    public ObservableList<CustomerTM> getAllCustomers() throws SQLException {
        return FXCollections.observableArrayList(
                memberManagementRepository.findAll().stream()
                        .map(customer -> new CustomerTM(customer.getId(), customer.getName(), customer.getAddress(), customer.getContact(), customer.getEmail()))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public String getNextCustomerId() throws SQLException {
        return memberManagementRepository.getNextCustomerId();
    }

    @Override
    public void saveCustomer(CustomerTM customerTM) throws SQLException {
        validateCustomer(customerTM);
        Customer customer = new Customer(customerTM.getCustId(), customerTM.getName(), customerTM.getAddress(), customerTM.getContact(), customerTM.getEmail());
        memberManagementRepository.save(customer);
    }

    @Override
    public void updateCustomer(CustomerTM customerTM) throws SQLException {
        validateCustomer(customerTM);
        Customer customer = new Customer(customerTM.getCustId(), customerTM.getName(), customerTM.getAddress(), customerTM.getContact(), customerTM.getEmail());
        memberManagementRepository.update(customer);
    }

    @Override
    public void deleteCustomer(String customerId) throws SQLException {
        memberManagementRepository.deleteById(customerId);
    }

    @Override
    public void validateCustomer(CustomerTM customerTM) throws IllegalArgumentException {
        if (customerTM.getName().isEmpty() || customerTM.getContact().isEmpty()) {
            throw new IllegalArgumentException("Name and Contact are required.");
        }
    }
}