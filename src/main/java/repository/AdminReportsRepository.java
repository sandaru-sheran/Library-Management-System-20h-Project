package repository;

import model.PopularBook;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import java.sql.SQLException;
import java.util.List;

public interface AdminReportsRepository {
    double getTotalRevenue() throws SQLException;
    double getOutstandingFines() throws SQLException;
    int getActiveRentals() throws SQLException;
    ObservableList<PieChart.Data> getPieChartData() throws SQLException;
    List<PopularBook> getPopularBooksData() throws SQLException;
}