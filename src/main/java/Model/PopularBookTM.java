package Model; // Move to a 'dto' or 'model' package if preferred

public class PopularBookTM {
    private String bookTitle;
    private String category;
    private int borrowCount;

    public PopularBookTM() {
    }

    public PopularBookTM(String bookTitle, String category, int borrowCount) {
        this.bookTitle = bookTitle;
        this.category = category;
        this.borrowCount = borrowCount;
    }

    // Getters and Setters are REQUIRED for JavaFX PropertyValueFactory

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getBorrowCount() {
        return borrowCount;
    }

    public void setBorrowCount(int borrowCount) {
        this.borrowCount = borrowCount;
    }
}