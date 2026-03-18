package controller;

import model.CustomerTM;
import service.AdminCustomerBaseService;
import service.impl.AdminCustomerBaseServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
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

    private AdminCustomerBaseService adminCustomerBaseService;
    private final ObservableList<CustomerTM> customerList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        adminCustomerBaseService = new AdminCustomerBaseServiceImpl();
        colCustId.setCellValueFactory(new PropertyValueFactory<>("custId"));
        colCustName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCustAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colCustContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colCustEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        txtSearchCustomer.textProperty().addListener((observable, oldValue, newValue) -> {
            tblCustomers.setItems(adminCustomerBaseService.filterCustomers(customerList, newValue));
            updateTotalMembersCount();
        });

        loadCustomerData();
    }

    private void loadCustomerData() {
        try {
            customerList.setAll(adminCustomerBaseService.getAllCustomers());
            tblCustomers.setItems(customerList);
            updateTotalMembersCount();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to load customer data from database.");
        }
    }

    private void updateTotalMembersCount() {
        lblTotalMembers.setText(String.valueOf(tblCustomers.getItems().size()));
    }
}