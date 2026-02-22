package Model; // Or your models/dto package

public class BookTM {
    private String bookId;
    private String title;
    private String author;
    private String category;
    private int qty;

    public BookTM() {
    }

    public BookTM(String bookId, String title, String author, String category, int qty) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.qty = qty;
    }

    // Getters and Setters are REQUIRED for JavaFX PropertyValueFactory to work
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}