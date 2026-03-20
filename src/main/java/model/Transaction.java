package model;

import java.time.LocalDate;

public class Transaction {
    private String rentalId;
    private String customerName;
    private String bookTitle;
    private LocalDate issueDate;

    public Transaction(String rentalId, String customerName, String bookTitle, LocalDate issueDate) {
        this.rentalId = rentalId;
        this.customerName = customerName;
        this.bookTitle = bookTitle;
        this.issueDate = issueDate;
    }

    // Getters and Setters
    public String getRentalId() { return rentalId; }
    public void setRentalId(String rentalId) { this.rentalId = rentalId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
}