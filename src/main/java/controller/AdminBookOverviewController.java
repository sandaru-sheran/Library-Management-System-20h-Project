package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import Model.*;
import db.DBConnection; // Import your database gateway

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class AdminBookOverviewController implements Initializable {

    @FXML private Label lblTotalBooksCount;
    @FXML private TextField txtSearchInventory;
    @FXML private TableView<BookTM> tblInventory;
    @FXML private TableColumn<BookTM, String> colBookId;
    @FXML private TableColumn<BookTM, String> colBookTitle;
    @FXML private TableColumn<BookTM, String> colBookAuthor;
    @FXML private TableColumn<BookTM, String> colBookCategory;
    @FXML private TableColumn<BookTM, Integer> colBookQty;

    // Master list holding ALL books from the database
    private ObservableList<BookTM> bookList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Map TableColumns
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colBookAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colBookCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colBookQty.setCellValueFactory(new PropertyValueFactory<>("qty"));

        // 2. Add listener to the search box for real-time filtering
        txtSearchInventory.textProperty().addListener((observable, oldValue, newValue) -> {
            filterBooks(newValue);
        });

        // 3. Load data from MySQL
        loadBookData();
    }

    private void loadBookData() {
        bookList.clear(); // Clear old data

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Books");

            while (resultSet.next()) {
                BookTM book = new BookTM(
                        resultSet.getString("book_id"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getInt("qty")
                );
                bookList.add(book);
            }

            // Set the master list to the table
            tblInventory.setItems(bookList);
            updateTotalCount();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to load books from database.");
        }
    }

    private void filterBooks(String keyword) {
        // If search box is empty, show all books
        if (keyword == null || keyword.trim().isEmpty()) {
            tblInventory.setItems(bookList);
        } else {
            // Create a temporary list to hold the matching books
            ObservableList<BookTM> filteredList = FXCollections.observableArrayList();

            // Convert keyword to lowercase so search is not case-sensitive
            String searchWord = keyword.toLowerCase();

            // Loop through the master list and look for matches
            for (BookTM book : bookList) {
                // Check if ID, Title, Author, or Category contains the typed letters
                if (book.getBookId().toLowerCase().contains(searchWord) ||
                        book.getTitle().toLowerCase().contains(searchWord) ||
                        book.getAuthor().toLowerCase().contains(searchWord) ||
                        book.getCategory().toLowerCase().contains(searchWord)) {

                    filteredList.add(book); // Match found! Add to temp list
                }
            }

            // Update the table to show ONLY the filtered list
            tblInventory.setItems(filteredList);
        }

        // Update the count label to show how many results were found
        updateTotalCount();
    }

    private void updateTotalCount() {
        // Updates the label with the current number of books VISIBLE in the table
        lblTotalBooksCount.setText(String.valueOf(tblInventory.getItems().size()));
    }
}