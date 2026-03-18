package controller;

import dto.RentalLogDTO;
import service.AdminRentalLogsService;
import service.impl.AdminRentalLogsServiceImpl;
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

public class AdminRentalLogsController implements Initializable {

    @FXML private Label lblTotalTransactions;
    @FXML private TextField txtSearchLogs;
    @FXML private TableView<RentalLogDTO> tblRentalLogs;
    @FXML private TableColumn<RentalLogDTO, String> colRentalId;
    @FXML private TableColumn<RentalLogDTO, String> colBookId;
    @FXML private TableColumn<RentalLogDTO, String> colCustId;
    @FXML private TableColumn<RentalLogDTO, String> colIssueDate;
    @FXML private TableColumn<RentalLogDTO, String> colReturnDate;
    @FXML private TableColumn<RentalLogDTO, String> colStatus;
    @FXML private TableColumn<RentalLogDTO, Double> colFine;
    @FXML private TableColumn<RentalLogDTO, String> colPayment;

    private AdminRentalLogsService adminRentalLogsService;
    private final ObservableList<RentalLogDTO> logList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        adminRentalLogsService = new AdminRentalLogsServiceImpl();
        colRentalId.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colCustId.setCellValueFactory(new PropertyValueFactory<>("custId"));
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colFine.setCellValueFactory(new PropertyValueFactory<>("fine"));
        colPayment.setCellValueFactory(new PropertyValueFactory<>("payment"));

        txtSearchLogs.textProperty().addListener((observable, oldValue, newValue) -> {
            tblRentalLogs.setItems(adminRentalLogsService.filterLogs(logList, newValue));
            updateTotalTransactionsCount();
        });

        loadRentalLogs();
    }

    private void loadRentalLogs() {
        try {
            logList.setAll(adminRentalLogsService.getRentalLogs());
            tblRentalLogs.setItems(logList);
            updateTotalTransactionsCount();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to load rental logs from database.");
        }
    }

    private void updateTotalTransactionsCount() {
        lblTotalTransactions.setText(String.valueOf(tblRentalLogs.getItems().size()));
    }
}