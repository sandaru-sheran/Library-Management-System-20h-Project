package dto;

public class RentalLogDTO {
    private String rentalId;
    private String bookId;
    private String custId;
    private String issueDate;
    private String returnDate;
    private String status;
    private double fine;
    private String payment;

    public RentalLogDTO(String rentalId, String bookId, String custId, String issueDate, String returnDate, String status, double fine, String payment) {
        this.rentalId = rentalId;
        this.bookId = bookId;
        this.custId = custId;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.fine = fine;
        this.payment = payment;
    }

    public String getRentalId() { return rentalId; }
    public void setRentalId(String rentalId) { this.rentalId = rentalId; }
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    public String getCustId() { return custId; }
    public void setCustId(String custId) { this.custId = custId; }
    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }
    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }
    public String getPayment() { return payment; }
    public void setPayment(String payment) { this.payment = payment; }
}