package service.impl;

import dto.PopularBookDTO;
import model.PopularBook;
import repository.AdminReportsRepository;
import repository.impl.AdminReportsRepositoryImpl;
import service.AdminReportsService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import db.DBConnection;

import java.io.InputStream;
import java.sql.SQLException;
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
        InputStream reportStream = getClass().getResourceAsStream("/view/reports/RentalReport.jrxml");
        JasperDesign jasperDesign = JRXmlLoader.load(reportStream);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, DBConnection.getInstance().getConnection());
        JasperViewer.viewReport(jasperPrint, false);
    }

    private PopularBookDTO mapToPopularBookDTO(PopularBook popularBook) {
        return new PopularBookDTO(
                popularBook.getTitle(),
                popularBook.getCategory(),
                popularBook.getBorrowCount()
        );
    }
}