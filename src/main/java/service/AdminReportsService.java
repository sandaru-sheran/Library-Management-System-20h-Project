package service;

import dto.PopularBookDTO;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import net.sf.jasperreports.engine.JRException;

import java.sql.SQLException;

public interface AdminReportsService {
    double getTotalRevenue() throws SQLException;
    double getOutstandingFines() throws SQLException;
    int getActiveRentals() throws SQLException;
    ObservableList<PieChart.Data> getPieChartData() throws SQLException;
    ObservableList<PopularBookDTO> getPopularBooksData() throws SQLException;
    void exportReport() throws JRException, SQLException;
}