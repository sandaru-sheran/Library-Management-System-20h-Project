package controller;

import dto.TransactionDTO;
import factory.ServiceFactory;
import service.DashboardService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Label lblTotalBooks;
    @FXML private Label lblTotalMembers;
    @FXML private Label lblOverdueBooks;
    @FXML private TableView<TransactionDTO> tblRecentTransactions;
    @FXML private TableColumn<TransactionDTO, String> colTransId;
    @FXML private TableColumn<TransactionDTO, String> colMemberName;
    @FXML private TableColumn<TransactionDTO, String> colBookTitle;
    @FXML private TableColumn<TransactionDTO, String> colDate;

    private DashboardService dashboardService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dashboardService = ServiceFactory.getInstance().getService(DashboardService.class);
        colTransId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMemberName.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        loadDashboardMetrics();
    }

    private void loadDashboardMetrics() {
        try {
            lblTotalBooks.setText(String.valueOf(dashboardService.getTotalBooks()));
            lblTotalMembers.setText(String.valueOf(dashboardService.getTotalMembers()));
            lblOverdueBooks.setText(String.valueOf(dashboardService.getOverdueBooks()));
            tblRecentTransactions.setItems(FXCollections.observableArrayList(dashboardService.getRecentTransactions()));
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to fetch dashboard metrics.");
            lblTotalBooks.setText("Err");
            lblTotalMembers.setText("Err");
            lblOverdueBooks.setText("Err");
        }
    }
}