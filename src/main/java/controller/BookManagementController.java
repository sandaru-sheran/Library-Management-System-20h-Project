package controller;
import factory.ServiceFactory;
import dto.tm.BookTM;
import service.BookService;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class BookManagementController implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private TextField txtId;
    @FXML private TextField txtTitle;
    @FXML private TextField txtAuthor;
    @FXML private TextField txtCategory;
    @FXML private TextField txtQty;

    @FXML private TableView<BookTM> tblBooks;
    @FXML private TableColumn<BookTM, String> colId;
    @FXML private TableColumn<BookTM, String> colTitle;
    @FXML private TableColumn<BookTM, String> colAuthor;
    @FXML private TableColumn<BookTM, String> colCategory;
    @FXML private TableColumn<BookTM, Integer> colQty;

    private BookService bookService;
    private final ObservableList<BookTM> bookList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bookService = ServiceFactory.getInstance().getService(BookService.class);
        colId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));

        txtId.setEditable(false);
        txtId.setStyle("-fx-text-fill: #94a3b8; -fx-background-color: #0f172a;");

        loadAllBooks();
        generateNextBookId();

        tblBooks.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillFields(newSelection);
            }
        });

        FilteredList<BookTM> filteredData = new FilteredList<>(bookList, b -> true);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(book -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (book.getTitle().toLowerCase().contains(lowerCaseFilter)) return true;
                if (book.getAuthor().toLowerCase().contains(lowerCaseFilter)) return true;
                if (book.getBookId().toLowerCase().contains(lowerCaseFilter)) return true;
                if (book.getCategory().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });

        SortedList<BookTM> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblBooks.comparatorProperty());
        tblBooks.setItems(sortedData);
    }

    private void loadAllBooks() {
        try {
            bookList.setAll(bookService.getAllBooks());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load books.");
        }
    }

    private void fillFields(BookTM book) {
        txtId.setText(book.getBookId());
        txtTitle.setText(book.getTitle());
        txtAuthor.setText(book.getAuthor());
        txtCategory.setText(book.getCategory());
        txtQty.setText(String.valueOf(book.getQty()));
    }

    private void clearFields() {
        txtTitle.clear();
        txtAuthor.clear();
        txtCategory.clear();
        txtQty.clear();
        tblBooks.getSelectionModel().clearSelection();
        generateNextBookId();
    }

    @FXML
    void clearIdOnAction(ActionEvent event) {
        clearFields();
    }

    private void generateNextBookId() {
        try {
            txtId.setText(bookService.getNextBookId());
        } catch (SQLException e) {
            e.printStackTrace();
            txtId.setText("B001");
        }
    }

    @FXML
    void saveBookOnAction(ActionEvent event) {
        try {
            int qty = Integer.parseInt(txtQty.getText());
            BookTM book = new BookTM(txtId.getText(), txtTitle.getText(), txtAuthor.getText(), txtCategory.getText(), qty);
            bookService.saveBook(book);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book saved successfully!");
            loadAllBooks();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Quantity must be a valid number.");
        } catch (IllegalArgumentException | SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    void updateBookOnAction(ActionEvent event) {
        BookTM selectedBook = tblBooks.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a book from the table to update.");
            return;
        }

        try {
            int qty = Integer.parseInt(txtQty.getText());
            BookTM book = new BookTM(txtId.getText(), txtTitle.getText(), txtAuthor.getText(), txtCategory.getText(), qty);
            bookService.updateBook(book);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book updated successfully!");
            loadAllBooks();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Quantity must be a valid number.");
        } catch (IllegalArgumentException | SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Update Error", e.getMessage());
        }
    }

    @FXML
    void deleteBookOnAction(ActionEvent event) {
        BookTM selectedBook = tblBooks.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a book to delete.");
            return;
        }

        boolean confirm = showDarkConfirmation(
                "Confirm Deletion",
                "Are you sure you want to permanently delete '" + selectedBook.getTitle() + "'?"
        );

        if (confirm) {
            try {
                bookService.deleteBook(selectedBook.getBookId());
                showAlert(Alert.AlertType.INFORMATION, "Deleted", "Book has been removed from inventory.");
                loadAllBooks();
                clearFields();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Cannot delete a book that is currently rented.");
            }
        }
    }

    private boolean showDarkConfirmation(String title, String message) {
        javafx.stage.Stage dialogStage = new javafx.stage.Stage();

        // --- THE FIX: Grab window and set owner immediately ---
        javafx.stage.Window mainWindow = tblBooks.getScene().getWindow();
        dialogStage.initOwner(mainWindow);
        // ------------------------------------------------------

        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

        boolean[] result = {false};

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new javafx.geometry.Insets(25));
        root.setStyle(
                "-fx-background-color: #1e293b; " +
                        "-fx-border-color: #ef4444; " +
                        "-fx-border-width: 2; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-radius: 8; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 20, 0, 0, 0);"
        );

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label lblMessage = new Label(message);
        lblMessage.setWrapText(true);
        lblMessage.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px; -fx-alignment: center;");

        Button btnYes = new Button("Yes, Delete");
        btnYes.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 20; -fx-background-radius: 5;");
        btnYes.setOnAction(e -> {
            result[0] = true;
            dialogStage.close();
        });

        Button btnCancel = new Button("Cancel");
        btnCancel.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 20; -fx-background-radius: 5;");
        btnCancel.setOnAction(e -> {
            result[0] = false;
            dialogStage.close();
        });

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(btnCancel, btnYes);

        root.getChildren().addAll(lblTitle, lblMessage, buttonBox);

        javafx.scene.Scene scene = new javafx.scene.Scene(root, 350, 180);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialogStage.setScene(scene);

        dialogStage.setX(mainWindow.getX() + (mainWindow.getWidth() / 2) - 175);
        dialogStage.setY(mainWindow.getY() + (mainWindow.getHeight() / 2) - 90);

        dialogStage.showAndWait();

        return result[0];
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        String accentColor = "#10b981";
        String symbol = "✔";

        if (alertType == Alert.AlertType.ERROR) {
            accentColor = "#ef4444";
            symbol = "✖";
        } else if (alertType == Alert.AlertType.WARNING) {
            accentColor = "#f59e0b";
            symbol = "⚠";
        }

        Label lblIcon = new Label(symbol);
        lblIcon.setStyle("-fx-text-fill: " + accentColor + "; -fx-font-size: 26px; -fx-font-weight: bold;");

        VBox container = new VBox(2);
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");

        Label lblMsg = new Label(message);
        lblMsg.setWrapText(true);
        lblMsg.setMaxWidth(250);
        lblMsg.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");
        container.getChildren().addAll(lblTitle, lblMsg);

        HBox notificationBox = new HBox(15);
        notificationBox.setAlignment(Pos.CENTER_LEFT);
        notificationBox.setPrefWidth(320);
        notificationBox.setPadding(new javafx.geometry.Insets(15));
        notificationBox.setStyle(
                "-fx-background-color: #0f172a; " +
                        "-fx-border-color: " + accentColor + "; " +
                        "-fx-border-width: 0 0 0 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-border-radius: 0 5 5 0; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 15, 0, 0, 8);"
        );
        notificationBox.getChildren().addAll(lblIcon, container);

        Popup popup = new Popup();
        popup.getContent().add(notificationBox);
        popup.setAutoHide(true);

        javafx.stage.Window window = tblBooks.getScene().getWindow();
        double x = window.getX() + window.getWidth() - 340;
        double y = window.getY() + window.getHeight() - 110;
        popup.show(window, x, y);

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> popup.hide());
        delay.play();
    }
}