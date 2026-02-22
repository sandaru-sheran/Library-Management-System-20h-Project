package Model;

public class TransactionTM {
    private String id;
    private String memberName;
    private String bookTitle;
    private String date;

    public TransactionTM() {}

    public TransactionTM(String id, String memberName, String bookTitle, String date) {
        this.id = id;
        this.memberName = memberName;
        this.bookTitle = bookTitle;
        this.date = date;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}