package controller;

import Model.PopularBookTM;
import db.DBConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class AdminReportsController implements Initializable {

    @FXML private Label lblTotalRevenue;
    @FXML private Label lblOutstandingFines;
    @FXML private Label lblActiveRentals;
    @FXML private PieChart chartInventory;
    @FXML private TableView<PopularBookTM> tblPopularBooks;
    @FXML private TableColumn<PopularBookTM, String> colRankBookTitle;
    @FXML private TableColumn<PopularBookTM, String> colRankCategory;
    @FXML private TableColumn<PopularBookTM, Integer> colRankCount;

    private final ObservableList<PopularBookTM> popularBooksList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colRankBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colRankCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colRankCount.setCellValueFactory(new PropertyValueFactory<>("borrowCount"));

        loadMetrics();
        loadPieChartData();
        loadPopularBooksData();
    }

    private void loadMetrics() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();

            // 1. Total Revenue (Fines that are PAID)
            ResultSet rsRevenue = stm.executeQuery("SELECT SUM(fine) FROM Rentals WHERE payment_status = 'Paid'");
            if (rsRevenue.next()) {
                double revenue = rsRevenue.getDouble(1);
                lblTotalRevenue.setText(String.format("%.2f LKR", revenue));
            }

            // 2. Outstanding Fines (Fines that are PENDING)
            ResultSet rsFines = stm.executeQuery("SELECT SUM(fine) FROM Rentals WHERE payment_status = 'Pending'");
            if (rsFines.next()) {
                double fines = rsFines.getDouble(1);
                lblOutstandingFines.setText(String.format("%.2f LKR", fines));
            }

            // 3. Active Rentals (Books currently 'Borrowed' or 'Overdue')
            ResultSet rsActive = stm.executeQuery("SELECT COUNT(*) FROM Rentals WHERE status != 'Returned'");
            if (rsActive.next()) {
                lblActiveRentals.setText(String.valueOf(rsActive.getInt(1)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPieChartData() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            // Grouping books by category to see the inventory spread
            ResultSet rs = connection.createStatement().executeQuery(
                    "SELECT category, COUNT(*) FROM Books GROUP BY category"
            );

            while (rs.next()) {
                pieChartData.add(new PieChart.Data(rs.getString(1), rs.getInt(2)));
            }
            chartInventory.setData(pieChartData);

            // --- INLINE DARK THEME STYLING FOR PIE CHART ---
            Platform.runLater(() -> {
                // 1. Make the Pie Chart slice labels white
                for (Node node : chartInventory.lookupAll(".chart-pie-label")) {
                    if (node instanceof Text) {
                        ((Text) node).setFill(javafx.scene.paint.Color.WHITE);
                    }
                }

                // 2. Make the Legend background transparent
                Node legend = chartInventory.lookup(".chart-legend");
                if (legend != null) {
                    legend.setStyle("-fx-background-color: transparent;");
                }

                // 3. Make the Legend text white
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
        popularBooksList.clear();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            // Joining Rentals and Books to see which titles are trending
            String sql = "SELECT b.title, b.category, COUNT(r.rental_id) as count " +
                    "FROM Rentals r JOIN Books b ON r.book_id = b.book_id " +
                    "GROUP BY b.book_id ORDER BY count DESC LIMIT 5";

            ResultSet rs = connection.createStatement().executeQuery(sql);

            while (rs.next()) {
                popularBooksList.add(new PopularBookTM(
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getInt("count")
                ));
            }
            tblPopularBooks.setItems(popularBooksList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}