package service.impl;

import db.DBConnection;
import dto.PopularBookDTO;
import model.PopularBook;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import repository.AdminReportsRepository;
import repository.impl.AdminReportsRepositoryImpl;
import service.AdminReportsService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.stream.Collectors;

public class AdminReportsServiceImpl implements AdminReportsService {

    private final AdminReportsRepository adminReportsRepository;

    public AdminReportsServiceImpl() {
        this.adminReportsRepository = new AdminReportsRepositoryImpl();
    }

    @Override
    public double getTotalRevenue() throws SQLException {
        return adminReportsRepository.getTotalRevenue();
    }

    @Override
    public double getOutstandingFines() throws SQLException {
        return adminReportsRepository.getOutstandingFines();
    }

    @Override
    public int getActiveRentals() throws SQLException {
        return adminReportsRepository.getActiveRentals();
    }

    @Override
    public ObservableList<PieChart.Data> getPieChartData() throws SQLException {
        return adminReportsRepository.getPieChartData();
    }

    @Override
    public ObservableList<PopularBookDTO> getPopularBooksData() throws SQLException {
        return FXCollections.observableArrayList(
                adminReportsRepository.getPopularBooksData().stream()
                        .map(this::mapToPopularBookDTO)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void exportReport() throws JRException, SQLException {
        // 1. Get the report template from the resources folder
        InputStream reportStream = getClass().getResourceAsStream("/view/reports/RentalReport.jrxml");
        if (reportStream == null) {
            throw new JRException("Report file not found at /view/reports/RentalReport.jrxml");
        }

        // 2. Get a database connection
        Connection connection = DBConnection.getInstance().getConnection();
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database connection is not available or closed.");
        }

        // 3. Compile the report
        JasperDesign jasperDesign = JRXmlLoader.load(reportStream);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        // 4. Fill the report with data using the connection
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), connection);

        if (jasperPrint.getPages().isEmpty()) {
            System.err.println("The generated report has no pages. Check the report's query and the database content.");
        }

        // 5. Display the report viewer and FORCE it to the front
        JasperViewer viewer = new JasperViewer(jasperPrint, false);
        viewer.setTitle("System Analytics Report");
        viewer.setVisible(true); // Show the window
        viewer.setAlwaysOnTop(true); // Force it over the JavaFX dashboard
        viewer.toFront(); // Bring focus to it
        viewer.setAlwaysOnTop(false); // Remove the lock so you can still minimize it later
    }

    private PopularBookDTO mapToPopularBookDTO(PopularBook popularBook) {
        return new PopularBookDTO(
                popularBook.getTitle(),
                popularBook.getCategory(),
                popularBook.getBorrowCount()
        );
    }
}