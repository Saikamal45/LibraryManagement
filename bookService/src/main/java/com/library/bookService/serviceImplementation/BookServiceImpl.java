package com.library.bookService.serviceImplementation;

import com.library.bookService.exception.BookNotFoundException;
import com.library.bookService.model.Book;
import com.library.bookService.repository.BookRepo;
import com.library.bookService.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepo bookRepo;

    @Override
    public Book addBook(Book book) {
        bookRepo.save(book);
        return book;
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }

    @Override
    public Book getBookById(Integer id) {
        return bookRepo.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " not found"));
    }


    @Override
    public String deleteBook(Integer id) {
        Book book = bookRepo.findById(id).
                orElseThrow(() -> new BookNotFoundException("Book with id " + id + " not found"));

        bookRepo.delete(book);
        return "Book with id " + id + " has been deleted successfully";
    }

    @Override
    public Book updateBook(Book book) {
        // Check if book exists
        if (bookRepo.existsById(book.getId())) {
            return bookRepo.save(book);
        } else {
            throw new BookNotFoundException("Book with id " + book.getId() + " not found");
        }
    }


}
