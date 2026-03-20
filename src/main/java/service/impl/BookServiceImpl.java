package service.impl;

import factory.RepositoryFactory;
import dto.tm.BookTM;
import model.Book;
import repository.BookRepository;
import service.BookService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.stream.Collectors;

public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl() {
        this.bookRepository = RepositoryFactory.getInstance().getRepository(BookRepository.class);
    }

    @Override
    public ObservableList<BookTM> getAllBooks() throws SQLException {
        return FXCollections.observableArrayList(
                bookRepository.findAll().stream()
                        .map(book -> new BookTM(book.getId(), book.getTitle(), book.getAuthor(), book.getCategory(), book.getQuantity()))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public String getNextBookId() throws SQLException {
        return bookRepository.getNextBookId();
    }

    @Override
    public void saveBook(BookTM bookTM) throws SQLException {
        validateBook(bookTM);
        Book book = new Book(bookTM.getBookId(), bookTM.getTitle(), bookTM.getAuthor(), bookTM.getCategory(), bookTM.getQty());
        bookRepository.save(book);
    }

    @Override
    public void updateBook(BookTM bookTM) throws SQLException {
        validateBook(bookTM);
        Book book = new Book(bookTM.getBookId(), bookTM.getTitle(), bookTM.getAuthor(), bookTM.getCategory(), bookTM.getQty());
        bookRepository.update(book);
    }

    @Override
    public void deleteBook(String bookId) throws SQLException {
        bookRepository.deleteById(bookId);
    }

    @Override
    public void validateBook(BookTM bookTM) throws IllegalArgumentException {
        if (bookTM.getTitle().isEmpty() || bookTM.getAuthor().isEmpty() || bookTM.getCategory().isEmpty()) {
            throw new IllegalArgumentException("Please fill in all book details.");
        }
        if (bookTM.getQty() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
    }
}