package dto;
public class RecentRentalDTO {
    private String date;
    private String customerName;
    private String bookTitle;
    private String status;

    public RecentRentalDTO(String date, String customerName, String bookTitle, String status) {
        this.date = date;
        this.customerName = customerName;
        this.bookTitle = bookTitle;
        this.status = status;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}