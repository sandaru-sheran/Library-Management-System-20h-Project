package repository.impl;

import db.DBConnection;
import model.PopularBook;
import repository.AdminReportsRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AdminReportsRepositoryImpl implements AdminReportsRepository {

    @Override
    public double getTotalRevenue() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stm = connection.createStatement();
             ResultSet rsRevenue = stm.executeQuery("SELECT SUM(fine) FROM Rentals WHERE payment_status = 'Paid'")) {
            if (rsRevenue.next()) {
                return rsRevenue.getDouble(1);
            }
        }
        return 0;
    }

    @Override
    public double getOutstandingFines() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stm = connection.createStatement();
             ResultSet rsFines = stm.executeQuery("SELECT SUM(fine) FROM Rentals WHERE payment_status = 'Pending'")) {
            if (rsFines.next()) {
                return rsFines.getDouble(1);
            }
        }
        return 0;
    }

    @Override
    public int getActiveRentals() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stm = connection.createStatement();
             ResultSet rsActive = stm.executeQuery("SELECT COUNT(*) FROM Rentals WHERE status != 'Returned'")) {
            if (rsActive.next()) {
                return rsActive.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public ObservableList<PieChart.Data> getPieChartData() throws SQLException {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stm = connection.createStatement();
             ResultSet rs = stm.executeQuery("SELECT category, COUNT(*) FROM Books GROUP BY category")) {
            while (rs.next()) {
                pieChartData.add(new PieChart.Data(rs.getString(1), rs.getInt(2)));
            }
        }
        return pieChartData;
    }

    @Override
    public List<PopularBook> getPopularBooksData() throws SQLException {
        List<PopularBook> popularBooksList = new ArrayList<>();
        String sql = "SELECT b.title, b.category, COUNT(r.rental_id) as count " +
                "FROM Rentals r JOIN Books b ON r.book_id = b.book_id " +
                "GROUP BY b.book_id ORDER BY count DESC LIMIT 5";

        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stm = connection.createStatement();
             ResultSet rs = stm.executeQuery(sql)) {

            while (rs.next()) {
                popularBooksList.add(new PopularBook(
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getInt("count")
                ));
            }
        }
        return popularBooksList;
    }
}