package Model; // Or your preferred models/dto package

public class RecentRentalTM {
    private String date;
    private String customerName;
    private String bookTitle;
    private String status;

    public RecentRentalTM() {
    }

    public RecentRentalTM(String date, String customerName, String bookTitle, String status) {
        this.date = date;
        this.customerName = customerName;
        this.bookTitle = bookTitle;
        this.status = status;
    }

    // Getters and Setters are REQUIRED for JavaFX PropertyValueFactory

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}