package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import Model.*;
import db.DBConnection; // Make sure this matches your DB Gateway

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class AdminCustomerBaseController implements Initializable {

    @FXML private Label lblTotalMembers;
    @FXML private TextField txtSearchCustomer;
    @FXML private TableView<CustomerTM> tblCustomers;
    @FXML private TableColumn<CustomerTM, String> colCustId;
    @FXML private TableColumn<CustomerTM, String> colCustName;
    @FXML private TableColumn<CustomerTM, String> colCustAddress;
    @FXML private TableColumn<CustomerTM, String> colCustContact;
    @FXML private TableColumn<CustomerTM, String> colCustEmail;

    // Master list holding ALL customers from the database
    private ObservableList<CustomerTM> customerList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Map TableColumns to the properties in the CustomerTM model
        colCustId.setCellValueFactory(new PropertyValueFactory<>("custId"));
        colCustName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCustAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colCustContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colCustEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // 2. Add a listener to the search TextField for dynamic filtering
        txtSearchCustomer.textProperty().addListener((observable, oldValue, newValue) -> {
            filterCustomers(newValue);
        });

        // 3. Load initial customer data
        loadCustomerData();
    }

    private void loadCustomerData() {
        customerList.clear(); // Clear existing data to prevent duplicates on reload

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Customers");

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

            // Set the data to the table and update the label
            tblCustomers.setItems(customerList);
            updateTotalMembersCount();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to load customer data from database.");
        }
    }

    private void filterCustomers(String keyword) {
        // If keyword is empty or null, display the full list
        if (keyword == null || keyword.trim().isEmpty()) {
            tblCustomers.setItems(customerList);
        } else {
            ObservableList<CustomerTM> filteredList = FXCollections.observableArrayList();
            String searchWord = keyword.toLowerCase();

            // Loop through the master list and look for matches
            for (CustomerTM customer : customerList) {
                if (customer.getCustId().toLowerCase().contains(searchWord) ||
                        customer.getName().toLowerCase().contains(searchWord) ||
                        customer.getEmail().toLowerCase().contains(searchWord) ||
                        customer.getContact().toLowerCase().contains(searchWord)) {

                    filteredList.add(customer);
                }
            }
            // Update the table to show ONLY the filtered list
            tblCustomers.setItems(filteredList);
        }

        // Ensure the total count updates to reflect the search results
        updateTotalMembersCount();
    }

    private void updateTotalMembersCount() {
        lblTotalMembers.setText(String.valueOf(tblCustomers.getItems().size()));
    }
}