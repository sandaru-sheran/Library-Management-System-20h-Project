package Model;

public class RentalTM {
    private String rentalId;
    private String bookId;
    private String custId;
    private String issueDate;
    private String dueDate;
    private String status;
    private double fine;

    public RentalTM() {}

    public RentalTM(String rentalId, String bookId, String custId, String issueDate, String dueDate, String status, double fine) {
        this.rentalId = rentalId;
        this.bookId = bookId;
        this.custId = custId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = status;
        this.fine = fine;
    }

    // Getters and Setters are REQUIRED for PropertyValueFactory
    public String getRentalId() { return rentalId; }
    public void setRentalId(String rentalId) { this.rentalId = rentalId; }
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    public String getCustId() { return custId; }
    public void setCustId(String custId) { this.custId = custId; }
    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }
}