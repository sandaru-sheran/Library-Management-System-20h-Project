package controller;
import dto.RecentRentalDTO;
import service.AdminOverviewService;
import service.impl.AdminOverviewServiceImpl;
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

public class AdminOverviewController implements Initializable {

    @FXML private Label lblTotalUsers;
    @FXML private Label lblTotalPaid;
    @FXML private Label lblTotalUnpaid;
    @FXML private Label lblUnreturned;
    @FXML private TextField txtFinePerDay;
    @FXML private Label lblFineUpdateStatus;
    @FXML private TableView<RecentRentalDTO> tblRecentRentals;
    @FXML private TableColumn<RecentRentalDTO, String> colDate;
    @FXML private TableColumn<RecentRentalDTO, String> colCustomerName;
    @FXML private TableColumn<RecentRentalDTO, String> colBookTitle;
    @FXML private TableColumn<RecentRentalDTO, String> colStatus;

    private AdminOverviewService adminOverviewService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        adminOverviewService = new AdminOverviewServiceImpl();
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadDashboardMetrics();
        loadRecentRentals();
    }

    private void loadDashboardMetrics() {
        try {
            lblTotalUsers.setText(String.valueOf(adminOverviewService.getTotalUsers()));
            lblUnreturned.setText(String.valueOf(adminOverviewService.getUnreturnedBooks()));
            lblTotalPaid.setText(String.format("%.2f", adminOverviewService.getTotalPaidFines()));
            lblTotalUnpaid.setText(String.format("%.2f", adminOverviewService.getTotalUnpaidFines()));
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error loading dashboard metrics from database!");
        }
    }

    @FXML
    void saveFineOnAction() {
        String newFineText = txtFinePerDay.getText();
        try {
            double newFineRate = Double.parseDouble(newFineText);
            adminOverviewService.saveFinePerDay(newFineRate);
            lblFineUpdateStatus.setText("Updated: " + newFineRate + " LKR");
            lblFineUpdateStatus.setStyle("-fx-text-fill: #10b981;");
        } catch (NumberFormatException e) {
            lblFineUpdateStatus.setText("Enter a valid number!");
            lblFineUpdateStatus.setStyle("-fx-text-fill: #ef4444;"); // Red
        } catch (SQLException e) {
            e.printStackTrace();
            lblFineUpdateStatus.setText("Database Error!");
        }

        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
        pause.setOnFinished(e -> lblFineUpdateStatus.setText(""));
        pause.play();
    }

    private void loadRecentRentals() {
        try {
            tblRecentRentals.setItems(adminOverviewService.getRecentRentals());
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error while loading recent rentals!");
        }
    }
}