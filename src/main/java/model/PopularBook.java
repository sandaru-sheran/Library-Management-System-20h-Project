package model;

public class PopularBook {
    private String title;
    private String category;
    private int borrowCount;

    public PopularBook(String title, String category, int borrowCount) {
        this.title = title;
        this.category = category;
        this.borrowCount = borrowCount;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getBorrowCount() { return borrowCount; }
    public void setBorrowCount(int borrowCount) { this.borrowCount = borrowCount; }
}