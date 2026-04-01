package controller;
import dto.PopularBookDTO;
import factory.ServiceFactory;
import service.AdminReportsService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import net.sf.jasperreports.engine.JRException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AdminReportsController implements Initializable {

    @FXML private Label lblTotalRevenue;
    @FXML private Label lblOutstandingFines;
    @FXML private Label lblActiveRentals;
    @FXML private PieChart chartInventory;
    @FXML private TableView<PopularBookDTO> tblPopularBooks;
    @FXML private TableColumn<PopularBookDTO, String> colRankBookTitle;
    @FXML private TableColumn<PopularBookDTO, String> colRankCategory;
    @FXML private TableColumn<PopularBookDTO, Integer> colRankCount;

    private AdminReportsService adminReportsService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("AdminReportsController: Initializing...");
        adminReportsService = ServiceFactory.getInstance().getService(AdminReportsService.class);
        colRankBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colRankCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colRankCount.setCellValueFactory(new PropertyValueFactory<>("borrowCount"));

        loadMetrics();
        loadPieChartData();
        loadPopularBooksData();
        System.out.println("AdminReportsController: Initialization complete.");
    }

    private void loadMetrics() {
        try {
            lblTotalRevenue.setText(String.format("%.2f LKR", adminReportsService.getTotalRevenue()));
            lblOutstandingFines.setText(String.format("%.2f LKR", adminReportsService.getOutstandingFines()));
            lblActiveRentals.setText(String.valueOf(adminReportsService.getActiveRentals()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void exportReportOnAction(ActionEvent event) {
        System.out.println("AdminReportsController: exportReportOnAction button clicked.");
        try {
            adminReportsService.exportReport();
            System.out.println("AdminReportsController: exportReport() service call finished.");
        } catch (JRException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Report Error", "Could not generate the report. The template file might be corrupt or missing: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not connect to the database to generate the report: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void loadPieChartData() {
        try {
            chartInventory.setData(adminReportsService.getPieChartData());
            Platform.runLater(() -> {
                for (Node node : chartInventory.lookupAll(".chart-pie-label")) {
                    if (node instanceof Text) {
                        ((Text) node).setFill(javafx.scene.paint.Color.WHITE);
                    }
                }
                Node legend = chartInventory.lookup(".chart-legend");
                if (legend != null) {
                    legend.setStyle("-fx-background-color: transparent;");
                }
                for (Node node : chartInventory.lookupAll(".chart-legend-item")) {
                    if (node instanceof Label) {
                        ((Label) node).setTextFill(javafx.scene.paint.Color.WHITE);
                    }
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPopularBooksData() {
        try {
            tblPopularBooks.setItems(adminReportsService.getPopularBooksData());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}