package controller;

import dto.BookDTO;
import service.AdminBookOverviewService;
import service.impl.AdminBookOverviewServiceImpl;
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

public class AdminBookOverviewController implements Initializable {

    @FXML private Label lblTotalBooksCount;
    @FXML private TextField txtSearchInventory;
    @FXML private TableView<BookDTO> tblInventory;
    @FXML private TableColumn<BookDTO, String> colBookId;
    @FXML private TableColumn<BookDTO, String> colBookTitle;
    @FXML private TableColumn<BookDTO, String> colBookAuthor;
    @FXML private TableColumn<BookDTO, String> colBookCategory;
    @FXML private TableColumn<BookDTO, Integer> colBookQty;

    private AdminBookOverviewService adminBookOverviewService;
    private final ObservableList<BookDTO> bookList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        adminBookOverviewService = new AdminBookOverviewServiceImpl();
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colBookAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colBookCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colBookQty.setCellValueFactory(new PropertyValueFactory<>("qty"));

        txtSearchInventory.textProperty().addListener((observable, oldValue, newValue) -> {
            tblInventory.setItems(adminBookOverviewService.filterBooks(bookList, newValue));
            updateTotalCount();
        });

        loadBookData();
    }

    private void loadBookData() {
        try {
            bookList.setAll(adminBookOverviewService.getAllBooks());
            tblInventory.setItems(bookList);
            updateTotalCount();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to load books from database.");
        }
    }

    private void updateTotalCount() {
        lblTotalBooksCount.setText(String.valueOf(tblInventory.getItems().size()));
    }
}